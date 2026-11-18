package com.fincore.identityservice.service;

import com.fincore.identityservice.dto.profile.ProfileMajorResponseDto;
import com.fincore.identityservice.dto.profile.ProfileMinorResponseDto;
import com.fincore.identityservice.dto.profile.ProfileUpdateDto;
import com.fincore.identityservice.entity.User;
import com.fincore.identityservice.mapper.UserProfileMapper;
import com.fincore.identityservice.repository.ProfileRepository;
import com.fincore.identityservice.repository.UserRepository;
import com.fincore.identityservice.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final UserProfileMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public ProfileMajorResponseDto getMyProfile(UserPrincipal userPrincipal) {
        User user = userRepository.findByUsername(userPrincipal.getUsername()).orElseThrow(
                () -> new EntityNotFoundException("User with username " + userPrincipal.getUsername() + " not found")
        );
        return mapper.toMajorProfileDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileMinorResponseDto getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User with username " + username + " not found")
        );
        return mapper.toMinorProfileDto(user);
    }

    @Override
    @Transactional
    public ProfileMajorResponseDto updateMyProfile(UserPrincipal userPrincipal, ProfileUpdateDto dto) {

        User user = userRepository.findByUsername(userPrincipal.getUsername()).orElseThrow(
                () -> new EntityNotFoundException("User with username " + userPrincipal.getUsername() + " not found")
        );


        validUpdateProfile(user, dto);
        mapper.updateEntityFromDto(dto, user.getProfile());

        userRepository.save(user);
        return mapper.toMajorProfileDto(user);
    }


    private void validUpdateProfile(User user, ProfileUpdateDto dto) {
        if (dto.email() != null && profileRepository.existsByEmailAndUserNot(dto.email(), user)) {
            throw new IllegalArgumentException("User with this email already exist");
        }
        if (dto.phoneNumber() != null && profileRepository.existsByPhoneNumberAndUserNot(dto.phoneNumber(), user)) {
            throw new IllegalArgumentException("User with this phoneNumber already exist");
        }
    }
}
