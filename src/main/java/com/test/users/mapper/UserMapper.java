package com.test.users.mapper;

import com.test.users.dto.PhoneDTO;
import com.test.users.dto.UserRequestDTO;
import com.test.users.model.Phone;
import com.test.users.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    //@Mapping(target = "passwordHash", expression = "java(hashPassword(dto.getPassword()))")
    @Mapping(target = "phones", source = "phones")
    User toEntity(UserRequestDTO dto);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "user", ignore = true) // se asigna manualmente después
    Phone toEntity(PhoneDTO dto);

    List<Phone> toPhoneEntities(List<PhoneDTO> dtos);

    /*default String hashPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }*/

}
