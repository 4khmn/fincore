package com.fincore.ledgerservice.entity;

import com.fincore.ledgerservice.entity.enums.LedgerTransactionOperationType;
import com.fincore.ledgerservice.entity.enums.LedgerTransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table(name = "ledger_transactions")
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LedgerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "operation_id")
    private UUID operationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "operation_type")
    private LedgerTransactionOperationType operationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LedgerTransactionStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;
}
