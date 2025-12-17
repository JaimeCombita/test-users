package com.company.users.factory;

import com.company.users.dto.PhoneDTO;
import com.company.users.model.Phone;
import com.company.users.model.User;

import java.util.UUID;

public class PhoneDataFactory {
    public static PhoneDTO createPhoneDTO() {
        return new PhoneDTO(
                "3001234567",
                "1",
                "57"
        );
    }

    public static Phone createPhone(User user) {
        Phone phone = new Phone();
        phone.setId(UUID.randomUUID());
        phone.setNumber("3001234567");
        phone.setCityCode("1");
        phone.setCountryCode("57");
        phone.setUser(user);
        return phone;
    }

    public static Phone createPhoneWithoutUser() {
        Phone phone = new Phone();
        phone.setId(UUID.randomUUID());
        phone.setNumber("3009876543");
        phone.setCityCode("2");
        phone.setCountryCode("57");
        return phone;
    }

}
