package com.fincore.identityservice.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank String username,
        @NotBlank @Size(min = 6) String password,
        String firstName,
        String lastName
) {
}
