package com.fincore.identityservice.service;

import com.fincore.identityservice.dto.auth.LoginRequestDto;
import com.fincore.identityservice.dto.auth.LoginResponseDto;
import com.fincore.identityservice.dto.auth.RegisterRequestDto;
import com.fincore.identityservice.dto.auth.RegisterResponseDto;
import com.fincore.identityservice.entity.Profile;
import com.fincore.identityservice.entity.User;
import com.fincore.identityservice.mapper.AuthenticationMapper;
import com.fincore.identityservice.repository.UserRepository;
import com.fincore.identityservice.security.JwtService;
import com.fincore.identityservice.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthenticationMapper authenticationMapper;

    @Transactional
    public RegisterResponseDto register(RegisterRequestDto requestDto) {
        if (userRepository.existsByUsername(requestDto.username())) {
            throw new IllegalArgumentException("Username is already in use");
        }

        User user = authenticationMapper.toUser(requestDto);
        user.setPasswordHash(passwordEncoder.encode(requestDto.password()));

        Profile profile = authenticationMapper.toProfile(requestDto);

        user.setProfile(profile);
        User savedUser = userRepository.save(user);
        return authenticationMapper.toRegisterResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto requestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.username(), requestDto.password())
        );

        User user = userRepository.findByUsername(requestDto.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        UserPrincipal userPrincipal = UserPrincipal.create(user);
        String token = jwtService.generateToken(userPrincipal);

        return new LoginResponseDto(token);
    }
}
