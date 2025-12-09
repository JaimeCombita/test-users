package com.test.users.mapper;

import com.test.users.dto.PhoneDTO;
import com.test.users.dto.UserRequestDTO;
import com.test.users.dto.UserResponseDTO;
import com.test.users.model.Phone;
import com.test.users.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "identificationNumber", source = "identificationNumber")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "phones", source = "phones")
    User toEntity(UserRequestDTO dto);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "user", ignore = true)
    Phone toEntity(PhoneDTO dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "identificationNumber", source = "identificationNumber")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "phones", source = "phones")
    UserRequestDTO toUserRequestDto(User user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "identificationNumber", source = "identificationNumber")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phones", source = "phones")
    @Mapping(target = "password", source = "password")
    UserResponseDTO toUserResponseDto(User user);

    List<UserResponseDTO> toUserResponseDtoList(List<User> user);

    @AfterMapping
    default void linkPhones(@MappingTarget User user) {
        if (user.getPhones() != null) {
            user.getPhones().forEach(phone -> phone.setUser(user));
        }
    }


}
