package com.fincore.ledgerservice.service;

import com.fincore.ledgerservice.dto.AccountCreateDto;
import com.fincore.ledgerservice.dto.AccountResponseDto;
import com.fincore.ledgerservice.dto.TopUpRequestDto;
import com.fincore.ledgerservice.dto.TopUpResponseDto;
import com.fincore.ledgerservice.entity.*;
import com.fincore.ledgerservice.entity.enums.*;
import com.fincore.ledgerservice.exception.AccountCreationException;
import com.fincore.ledgerservice.exception.MissMatchIdempotencyKeyException;
import com.fincore.ledgerservice.mapper.AccountMapper;
import com.fincore.ledgerservice.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final AccountBalanceRepository accountBalanceRepository;
    private final LedgerTransactionRepository ledgerTransactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final AccountMapper mapper;


    @Override
    @Transactional
    public AccountResponseDto createAccount(AccountCreateDto accountCreateDto, UUID ownerId) {
        validateAccountCreation(ownerId);

        Account account = mapper.toEntity(accountCreateDto);
        account.setOwnerUserId(ownerId);
        addAccountBalance(account);
        account.setCreatedAt(LocalDateTime.now());
        Account saved = accountRepository.save(account);


        return mapper.toDto(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDto> getAllAccountsByUser(UUID ownerId) {
        List<Account> accounts = accountRepository.findByOwnerUserId(ownerId);

        return accounts.stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDto getAccountById(UUID accountId, UUID ownerId) {
        Account account = accountRepository.findByIdAndOwnerUserId(accountId, ownerId).orElseThrow(
                () -> new EntityNotFoundException("Account with id: " + accountId + " with user: " + ownerId + " not found")
        );

        return  mapper.toDto(account);
    }

    @Override
    @Transactional
    public TopUpResponseDto topUp(UUID accountId, UUID ownerId, TopUpRequestDto dto, UUID idempotencyKey) {

        long needToAddAmountMinor = toMinorUnits(dto.amount());
        validateTopUpAmountMinor(needToAddAmountMinor);

        Account account = getActiveAccountByIdAndOwnerUserId(accountId, ownerId);
        String requestHash = calculateRequestHash(accountId, needToAddAmountMinor, account.getCurrency());

        Optional<IdempotencyRecord> existingRecord =
                idempotencyRecordRepository.findByUserIdAndIdempotencyKey(ownerId, idempotencyKey);

        if (existingRecord.isPresent()) {
            IdempotencyRecord record = existingRecord.get();

            validateIdempotencyRecordHasSameHash(record, requestHash);
            return buildTopUpResponseDto(record.getTransaction(), dto.amount(), account);
        }

        Account systemAccount = accountRepository
                .findByAccountTypeAndCurrency(AccountType.SYSTEM, account.getCurrency()).orElseThrow(
                        () -> new EntityNotFoundException(
                                "System account with currency: " + account.getCurrency().name() + " not found"
                        )
                );

        LedgerTransaction transaction = new LedgerTransaction();
        transaction.setOperationId(UUID.randomUUID());
        transaction.setOperationType(LedgerTransactionOperationType.TOP_UP);
        transaction.setStatus(LedgerTransactionStatus.COMPLETED);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction = ledgerTransactionRepository.save(transaction);

        LedgerEntry systemEntry = createLedgerEntry(transaction, systemAccount,
                -needToAddAmountMinor, systemAccount.getCurrency());

        LedgerEntry userEntry = createLedgerEntry(transaction, account, needToAddAmountMinor, account.getCurrency());

        ledgerEntryRepository.saveAll(List.of(systemEntry, userEntry));

        AccountBalance balance = account.getBalance();

        balance.setBalanceMinor(Math.addExact(balance.getBalanceMinor(), needToAddAmountMinor));
        balance.setUpdatedAt(LocalDateTime.now());
        accountBalanceRepository.save(balance);

        IdempotencyRecord idempotencyRecord = IdempotencyRecord.builder()
                .userId(ownerId)
                .idempotencyKey(idempotencyKey)
                .requestHash(requestHash)
                .transaction(transaction)
                .createdAt(LocalDateTime.now())
                .build();

        idempotencyRecordRepository.save(idempotencyRecord);

        return buildTopUpResponseDto(transaction, dto.amount(), account);
    }

    private void validateIdempotencyRecordHasSameHash(IdempotencyRecord record, String requestHash) {
        if (!record.getRequestHash().equals(requestHash)) {
            log.error("Idempotency key: {} already used with another request", record.getIdempotencyKey());
            throw new MissMatchIdempotencyKeyException(
                    "Idempotency key already used with another request"
            );
        }
    }

    private void validateTopUpAmountMinor(long needToAddAmountMinor) {
        if (needToAddAmountMinor <= 0 ) {
            log.error("Amount minor must be greater than zero: {}", needToAddAmountMinor);
            throw new IllegalArgumentException("Amount minor must be greater than zero");
        }
    }

    private Account getActiveAccountByIdAndOwnerUserId(UUID accountId, UUID ownerId) {
        Account account = accountRepository.findByIdAndOwnerUserId(accountId, ownerId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Account with id: " + accountId + " with user: " + ownerId + " not found"
                )
        );

        if (account.getStatus() != AccountStatus.ACTIVE) {
            log.error("Account with id: " + accountId + " with user: " + ownerId + " is not active");
            throw new IllegalStateException(
                    "Account with id: " + accountId + " with user: " + ownerId + " is not active"
            );
        }
        return account;
    }

    private LedgerEntry createLedgerEntry(LedgerTransaction transaction, Account account, long amountMinor, Currency currency) {
        return LedgerEntry.builder()
                .transaction(transaction)
                .account(account)
                .amountMinor(amountMinor)
                .currency(currency)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private TopUpResponseDto buildTopUpResponseDto(LedgerTransaction transaction, String amountMinor, Account account) {
        return new TopUpResponseDto(
                transaction.getId(),
                account.getId(),
                amountMinor,
                account.getCurrency().name(),
                transaction.getStatus().name()
        );
    }

    private void validateAccountCreation(UUID ownerId){
        if (accountRepository.countActiveOrBlockedByOwnerUserId(ownerId) >= 5) {
            throw new AccountCreationException("This user (" + ownerId + ") already have 5 accounts");
        }
    }
    private void addAccountBalance(Account account) {
        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setAccount(account);
        account.setBalance(accountBalance);
    }

    private long toMinorUnits(String amount) {
        return new BigDecimal(amount)
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();
    }

    private String calculateRequestHash(UUID accountId, long amountMinor, Currency currency) {
        try {
            String data = accountId + ":" + amountMinor + ":" + currency;

            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(data.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
