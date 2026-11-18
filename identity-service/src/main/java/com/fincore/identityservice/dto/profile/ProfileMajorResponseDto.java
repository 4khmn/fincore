package com.fincore.identityservice.dto.profile;

import com.fincore.identityservice.entity.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfileMajorResponseDto(
        String username,
        String firstName,
        String lastName,
        String bio,
        String email,
        String phoneNumber,
        String avatar_url,
        Role role,
        LocalDateTime createdAt
) { }
