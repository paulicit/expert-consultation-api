package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.model.dto.UserDto;
import com.code4ro.legalconsultation.model.persistence.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class UserInitService {

    private final UserService userService;

    @Autowired
    public UserInitService(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    void importDefaultUsers() {
        final InputStream usersStream = getClass().getResourceAsStream("/import/default_users.csv");

        final List<UserDto> userDtos = userService.extractUsersFromInputStream(usersStream);
        userDtos.forEach(user -> user.setRole(UserRole.OWNER));

        this.userService.saveAndSendRegistrationMail(userDtos);
    }
}
