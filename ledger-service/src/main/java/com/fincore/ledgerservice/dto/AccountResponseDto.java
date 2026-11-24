package com.fincore.ledgerservice.dto;

import com.fincore.ledgerservice.entity.enums.AccountStatus;
import com.fincore.ledgerservice.entity.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponseDto(
        UUID id,
        Currency currency,
        BigDecimal balance,
        AccountStatus accountStatus,
        LocalDateTime createdAt
) {
}