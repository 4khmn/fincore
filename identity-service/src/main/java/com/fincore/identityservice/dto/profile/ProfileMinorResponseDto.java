package com.fincore.identityservice.dto.profile;

import com.fincore.identityservice.entity.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfileMinorResponseDto(
        String username,
        String firstName,
        String lastName,
        String bio,
        String avatarUrl,
        LocalDateTime createdAt
) { }
