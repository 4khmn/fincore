package com.fincore.identityservice.service;

import com.fincore.identityservice.dto.auth.LoginRequestDto;
import com.fincore.identityservice.dto.auth.LoginResponseDto;
import com.fincore.identityservice.dto.auth.RegisterRequestDto;
import com.fincore.identityservice.dto.auth.RegisterResponseDto;

public interface AuthenticationService {

    RegisterResponseDto register(RegisterRequestDto requestDto);

    LoginResponseDto login(LoginRequestDto requestDto);
}
