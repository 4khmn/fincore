package com.fincore.ledgerservice.dto;

import com.fincore.ledgerservice.entity.enums.Currency;
import jakarta.validation.constraints.NotNull;

public record AccountCreateDto(
        @NotNull
        Currency accountCurrency
) {

}
