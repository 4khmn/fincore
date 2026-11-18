package com.fincore.identityservice.mapper;

import com.fincore.identityservice.dto.auth.RegisterRequestDto;
import com.fincore.identityservice.dto.auth.RegisterResponseDto;
import com.fincore.identityservice.entity.Profile;
import com.fincore.identityservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthenticationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "profile", ignore = true)
    User toUser(RegisterRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    Profile toProfile(RegisterRequestDto requestDto);



    RegisterResponseDto toRegisterResponseDto(User user);
}
