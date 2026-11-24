package com.fincore.ledgerservice.repository;

import com.fincore.ledgerservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    long countByOwnerUserId(UUID ownerUserId);


    List<Account> findByOwnerUserId(UUID ownerUserId);

    Optional<Account> findByIdAndOwnerUserId(UUID id, UUID ownerUserId);
}
