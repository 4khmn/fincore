package com.fincore.ledgerservice.service;

import com.fincore.ledgerservice.dto.AccountCreateDto;
import com.fincore.ledgerservice.dto.AccountResponseDto;
import com.fincore.ledgerservice.entity.Account;
import com.fincore.ledgerservice.entity.AccountBalance;
import com.fincore.ledgerservice.entity.enums.AccountType;
import com.fincore.ledgerservice.exception.AccountCreationException;
import com.fincore.ledgerservice.mapper.AccountMapper;
import com.fincore.ledgerservice.repository.AccountBalanceRepository;
import com.fincore.ledgerservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper mapper;


    @Override
    @Transactional
    public AccountResponseDto createAccount(AccountCreateDto accountCreateDto, UUID ownerId) {
        validateAccountCreation(ownerId);

        Account account = mapper.toEntity(accountCreateDto);
        account.setOwnerUserId(ownerId);
        addAccountBalance(account);

        accountRepository.save(account);

        return mapper.toDto(account);
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


    private void validateAccountCreation(UUID ownerId){
        if (accountRepository.countByOwnerUserId(ownerId) >= 5) {
            throw new AccountCreationException("This user (" + ownerId + ") already have 5 accounts");
        }
    }
    private void addAccountBalance(Account account) {
        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setAccount(account);
        account.setBalance(accountBalance);
    }
}
