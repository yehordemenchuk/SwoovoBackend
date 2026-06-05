package com.swoovo.users.mapper;

import com.swoovo.users.dto.UserRequest;
import com.swoovo.users.dto.UserResponse;
import com.swoovo.users.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity fromRequest(UserRequest userRequest);

    UserResponse toResponse(UserEntity userEntity);

    void updateUserFromRequest(UserRequest userRequest, @MappingTarget UserEntity userEntity);
}
