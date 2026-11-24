package com.fincore.ledgerservice.entity;

import com.fincore.ledgerservice.entity.enums.AccountStatus;
import com.fincore.ledgerservice.entity.enums.AccountType;
import com.fincore.ledgerservice.entity.enums.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_user_id")
    private UUID ownerUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, name = "account_type")
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(updatable = false, nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status =  AccountStatus.ACTIVE;

    @CreationTimestamp
    @Column(updatable = false, nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private AccountBalance balance;
}


