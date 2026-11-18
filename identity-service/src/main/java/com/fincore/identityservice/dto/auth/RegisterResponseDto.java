package com.fincore.identityservice.dto.auth;

import java.util.UUID;

public record RegisterResponseDto(
        UUID id,
        String email
) {
}
