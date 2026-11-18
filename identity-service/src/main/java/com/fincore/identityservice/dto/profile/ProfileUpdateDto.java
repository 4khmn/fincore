package com.fincore.identityservice.dto.profile;

import com.fincore.identityservice.entity.enums.Role;

import java.time.LocalDateTime;

public record ProfileUpdateDto(
        String firstName,
        String lastName,
        String bio,
        String email,
        String phoneNumber,
        String avatar_url
) {
}
