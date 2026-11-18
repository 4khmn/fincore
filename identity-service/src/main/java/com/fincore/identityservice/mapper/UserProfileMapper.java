package com.fincore.identityservice.mapper;

import com.fincore.identityservice.dto.profile.ProfileMajorResponseDto;
import com.fincore.identityservice.dto.profile.ProfileMinorResponseDto;
import com.fincore.identityservice.dto.profile.ProfileUpdateDto;
import com.fincore.identityservice.entity.Profile;
import com.fincore.identityservice.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "firstName", source = "user.profile.firstName")
    @Mapping(target = "lastName", source = "user.profile.lastName")
    @Mapping(target = "bio", source = "user.profile.bio")
    @Mapping(target = "avatar_url", source = "user.profile.avatar_url")
    ProfileMinorResponseDto toMinorProfileDto(User user);

    @Mapping(target = "firstName", source = "user.profile.firstName")
    @Mapping(target = "lastName", source = "user.profile.lastName")
    @Mapping(target = "bio", source = "user.profile.bio")
    @Mapping(target = "avatar_url", source = "user.profile.avatar_url")
    @Mapping(target = "phoneNumber", source = "user.profile.phoneNumber")
    @Mapping(target = "email", source = "user.profile.email")
    ProfileMajorResponseDto toMajorProfileDto(User user);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProfileUpdateDto dto, @MappingTarget Profile profile);
}
