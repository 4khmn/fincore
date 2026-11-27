package com.fincore.ledgerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TopUpRequestDto(

        @NotBlank
        @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$")
        String amount

) {}