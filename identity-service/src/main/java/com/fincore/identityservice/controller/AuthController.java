package com.fincore.identityservice.controller;

import com.fincore.identityservice.dto.auth.LoginRequestDto;
import com.fincore.identityservice.dto.auth.LoginResponseDto;
import com.fincore.identityservice.dto.auth.RegisterRequestDto;
import com.fincore.identityservice.dto.auth.RegisterResponseDto;
import com.fincore.identityservice.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody @Valid RegisterRequestDto requestDto) {
        log.info("POST /api/v1/auth/register -  Request to register user with username: {}", requestDto.username());
        RegisterResponseDto responseDto = authenticationService.register(requestDto);
        log.info("POST /api/v1/auth/register - User with username: {} successfully registered", requestDto.username());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto requestDto) {
        log.info("POST /api/v1/auth/login - Request to login user with username: {}", requestDto.username());
        LoginResponseDto responseDto = authenticationService.login(requestDto);
        log.info("POST /api/v1/auth/login - User with username: {} successfully logged in", requestDto.username());
        return ResponseEntity.ok(responseDto);
    }
}
