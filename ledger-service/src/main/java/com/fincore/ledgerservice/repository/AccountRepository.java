package com.fincore.ledgerservice.repository;

import com.fincore.ledgerservice.entity.Account;
import com.fincore.ledgerservice.entity.enums.AccountStatus;
import com.fincore.ledgerservice.entity.enums.AccountType;
import com.fincore.ledgerservice.entity.enums.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    long countByOwnerUserIdAndStatusNot(UUID ownerUserId, AccountStatus status);

    default long countActiveOrBlockedByOwnerUserId(UUID ownerUserId) {
        return countByOwnerUserIdAndStatusNot(ownerUserId, AccountStatus.CLOSED);
    }

    List<Account> findByOwnerUserId(UUID ownerUserId);

    Optional<Account> findByIdAndOwnerUserId(UUID id, UUID ownerUserId);

    Optional<Account> findByAccountTypeAndCurrency(AccountType accountType, Currency currency);
}
