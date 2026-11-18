package com.fincore.identityservice.controller;

import com.fincore.identityservice.dto.profile.ProfileMajorResponseDto;
import com.fincore.identityservice.dto.profile.ProfileMinorResponseDto;
import com.fincore.identityservice.dto.profile.ProfileUpdateDto;
import com.fincore.identityservice.security.UserPrincipal;
import com.fincore.identityservice.service.ProfileServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/profiles")
public class ProfileController {


    private final ProfileServiceImpl profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileMajorResponseDto> getMyProfile(@AuthenticationPrincipal UserPrincipal user) {
        log.info("GET /api/v1/profiles/me - Fetching major profile for user: {}", user.getUsername());
        ProfileMajorResponseDto myProfile = profileService.getMyProfile(user);
        log.info("GET /api/v1/profiles/me - Major profile: {} successfully fetched", myProfile.username());
        return ResponseEntity.status(200).body(myProfile);
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileMajorResponseDto> updateMyProfile(@AuthenticationPrincipal UserPrincipal user,
                                                            @RequestBody ProfileUpdateDto dto) {
        log.info("PUT /api/v1/profiles/me - Update user with username: {}", user.getUsername());
        ProfileMajorResponseDto updatedProfile = profileService.updateMyProfile(user, dto);
        log.info("PUT /api/v1/profiles/me - User with username: {} was successfully updated", updatedProfile.username());
        return ResponseEntity.status(200).body(updatedProfile);
    }

    @GetMapping("/{username}")
    public ResponseEntity<ProfileMinorResponseDto> getProfileByUsername(@PathVariable String username) {
        log.info("GET /api/v1/profiles/{} - Fetching minor profile for user: {}", username);
        ProfileMinorResponseDto profileByUsername = profileService.getProfileByUsername(username);
        log.info("GET /api/v1/profiles/{} - Minor profile: {} successfully getched", profileByUsername.username());
        return ResponseEntity.status(200).body(profileByUsername);
    }

}
