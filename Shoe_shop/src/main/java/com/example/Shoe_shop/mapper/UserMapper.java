package com.example.Shoe_shop.mapper;

import com.example.Shoe_shop.dto.request.RegisterRequest;
import com.example.Shoe_shop.dto.request.UserUpdateDTO;
import com.example.Shoe_shop.dto.response.UserResponse;
import com.example.Shoe_shop.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "password", target = "passwordHash")
    User toUser(RegisterRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "password", target = "passwordHash")
    void updateUserFromDto(UserUpdateDTO dto, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateUserFromDtoExcludingSensitive(UserUpdateDTO dto, @MappingTarget User user);

    @Mapping(target = "roleNames", expression = "java(user.getRoles().stream().map(role -> role.getRoleName().name()).toList())")
    UserResponse toUserResponse(User user);
}
