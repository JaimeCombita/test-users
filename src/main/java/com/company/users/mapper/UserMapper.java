package com.company.users.mapper;

import com.company.users.dto.*;
import com.company.users.model.Phone;
import com.company.users.model.User;
import org.mapstruct.*;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "accessToken", target = "token")
    @Mapping(source = "expiration", target = "expiration")
    @Mapping(source = "user.rol", target = "rol")
    LoginResponseDTO toLoginResponseDto(User user, String accessToken, Instant expiration);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    User toEntity(UserRequestDTO dto);

    @Mapping(source = "user.rol", target = "rol")
    UserResponseDTO toUserResponseDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserUpdateDTO dto, @MappingTarget User user);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "user", ignore = true)
    Phone toEntity(PhoneDTO dto);

    @AfterMapping
    default void linkPhones(@MappingTarget User user) {
        if (user.getPhones() != null) {
            user.getPhones().forEach(phone -> phone.setUser(user));
        }
    }

}
