package com.fincore.ledgerservice.repository;

import com.fincore.ledgerservice.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry,Integer> {
}
