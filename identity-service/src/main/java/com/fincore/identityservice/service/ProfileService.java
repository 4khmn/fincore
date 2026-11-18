package com.fincore.identityservice.service;

import com.fincore.identityservice.dto.profile.ProfileMajorResponseDto;
import com.fincore.identityservice.dto.profile.ProfileMinorResponseDto;
import com.fincore.identityservice.dto.profile.ProfileUpdateDto;
import com.fincore.identityservice.security.UserPrincipal;

public interface ProfileService {
    ProfileMajorResponseDto getMyProfile(UserPrincipal user);
    ProfileMinorResponseDto getProfileByUsername(String username);
    ProfileMajorResponseDto updateMyProfile(UserPrincipal user, ProfileUpdateDto dto);
}
