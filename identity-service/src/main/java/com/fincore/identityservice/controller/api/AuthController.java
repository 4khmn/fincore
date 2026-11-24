package com.fincore.identityservice.controller.api;


import com.fincore.identityservice.dto.auth.LoginRequestDto;
import com.fincore.identityservice.dto.auth.LoginResponseDto;
import com.fincore.identityservice.dto.auth.RegisterRequestDto;
import com.fincore.identityservice.dto.auth.RegisterResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Authentication", description = "Операции аутентификации и регистрации новых пользователей")
public interface AuthController {

    @Operation(summary = "Регистрация нового пользователя",
            description = "Создает новый аккаунт")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно создан")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    @ApiResponse(responseCode = "409", description = "Пользователь с таким логином уже существует")
    ResponseEntity<RegisterResponseDto> register(RegisterRequestDto requestDto);

    @Operation(summary = "Вход в систему",
            description = "Проверяет учетные данные и возвращает JWT-токен")
    @ApiResponse(responseCode = "200", description = "Успешная авторизация")
    @ApiResponse(responseCode = "401", description = "Неверный логин или пароль")
    ResponseEntity<LoginResponseDto> login(LoginRequestDto requestDto);
}
