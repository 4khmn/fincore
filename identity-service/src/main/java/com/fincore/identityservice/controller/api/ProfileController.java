package com.fincore.identityservice.controller.api;

import com.fincore.identityservice.dto.profile.ProfileMajorResponseDto;
import com.fincore.identityservice.dto.profile.ProfileMinorResponseDto;
import com.fincore.identityservice.dto.profile.ProfileUpdateDto;
import com.fincore.identityservice.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Profile", description = "Управление аккаунтами: получение, обновление")
public interface ProfileController {

    @Operation(
            summary = "Получить профиль текущего пользователя",
            description = "Требуется авторизация. Полная информация о пользователе.",
            security = @SecurityRequirement(name = "JWT")
    )
    ResponseEntity<ProfileMajorResponseDto> getMyProfile(UserPrincipal user);

    @Operation(
            summary = "Обновить информацию своего профиля",
            description = "Требуется авторизация. Поля dto опциональны.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    @ApiResponse(responseCode = "409", description = "Пользователь с таким логином или email уже существует")
    @ApiResponse(responseCode = "200")
    ResponseEntity<ProfileMajorResponseDto> updateMyProfile(UserPrincipal user, ProfileUpdateDto dto);

    @Operation(
            summary = "Получить профиль пользователя по id (UUID)",
            description = "Требуется авторизация. Сокращенная информация о пользователе.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponse(responseCode = "404", description = "Профиль не найден")
    @ApiResponse(responseCode = "200")
    ResponseEntity<ProfileMinorResponseDto> getProfileByUsername(String username);
}
