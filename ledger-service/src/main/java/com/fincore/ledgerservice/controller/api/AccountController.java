package com.fincore.ledgerservice.controller.api;

import com.fincore.ledgerservice.dto.AccountCreateDto;
import com.fincore.ledgerservice.dto.AccountResponseDto;
import com.fincore.ledgerservice.dto.TopUpRequestDto;
import com.fincore.ledgerservice.dto.TopUpResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


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


    @Operation(
            summary = "Пополнить счет",
            description = "Требуется авторизация. Виртуальное пополнение счета!!! Для пополнения необходимо в " +
                    "загаловках передать UUID \"Idempotency-Key\". Значение \"amount\" передается строкой. При " +
                    "одинаковом \"Idempotency-Key\" и разном \"amount\" возвращается 409 CONFLICT. При одинаковом " +
                    "запросе возвращается 201 с первым ответом (идемпотентность). Сами разберетесь короче " +
                    "что эта ручка делает",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    @ApiResponse(responseCode = "409", description = "Хэш не совпал (одинаковые ключи и разные тела запроса")
    @ApiResponse(responseCode = "201", description = "Счет успешно пополнен")
    ResponseEntity<TopUpResponseDto> topUp(UUID id, TopUpRequestDto dto, UUID idempotencyKey, Jwt jwt);
}
