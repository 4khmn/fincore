package com.fincore.ledgerservice.dto;

import java.util.UUID;

public record TopUpResponseDto(
        UUID transactionId,
        UUID accountId,
        String amount,
        String currency,
        String status
) {}