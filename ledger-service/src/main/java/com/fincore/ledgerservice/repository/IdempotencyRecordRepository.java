package com.fincore.ledgerservice.repository;

import com.fincore.ledgerservice.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord,Long> {

    Optional<IdempotencyRecord> findByUserIdAndIdempotencyKey(UUID ownerId, UUID idempotencyKey);
}
