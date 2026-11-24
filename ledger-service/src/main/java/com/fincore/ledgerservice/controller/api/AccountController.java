package com.fincore.ledgerservice.controller.api;

import com.fincore.ledgerservice.dto.AccountCreateDto;
import com.fincore.ledgerservice.dto.AccountResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;


import java.util.List;
import java.util.UUID;

@Tag(name = "Accounts", description = "Управление счетами: создание, пополнение, просмотр")
public interface AccountController {

    @Operation(
            summary = "Создать новый счет",
            description = "Требуется авторизация. Максимум можно создать 5. Валюта может повторяться.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    @ApiResponse(responseCode = "201", description = "Счет успешно создано")
    ResponseEntity<AccountResponseDto> createAccount(AccountCreateDto dto, Jwt jwt);

    @Operation(
            summary = "Просмотреть все счета пользователя",
            security = @SecurityRequirement(name = "JWT")
    )
    ResponseEntity<List<AccountResponseDto>> getAllAccountsByUser(Jwt jwt);

    @Operation(
            summary = "Просмотреть счет пользователя по его id",
            description = "Требуется авторизация. Счет может посмотреть только его владелец",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponse(responseCode = "404", description = "Счет не найден")
    @ApiResponse(responseCode = "200")
    ResponseEntity<AccountResponseDto> getAccountById(UUID id, Jwt jwt);
}
