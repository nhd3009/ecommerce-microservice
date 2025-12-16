package com.nhd.auth_service.mapper;

import com.nhd.auth_service.dto.UserDto;
import com.nhd.auth_service.entity.Role;
import com.nhd.auth_service.entity.User;
import java.util.stream.Collectors;

public class UserMapper {
  public static UserDto toDto(User user) {
    return UserDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .roles(
            user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet())
        )
        .build();
  }
}
