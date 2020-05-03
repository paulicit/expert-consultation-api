package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.converters.UserMapper;
import com.code4ro.legalconsultation.model.dto.UserDto;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.model.persistence.UserRole;
import com.code4ro.legalconsultation.repository.UserRepository;
import com.code4ro.legalconsultation.service.api.MailApi;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private static final String COMMA_REGEX = ",";

    private final UserRepository userRepository;
    private final CsvMapper csvMapper = new CsvMapper();
    private final MailApi mailApi;
    private final UserMapper mapperService;

    @Autowired
    public UserService(final UserRepository userRepository,
                       final MailApi mailApi,
                       final UserMapper mapperService) {
        this.userRepository = userRepository;
        this.mailApi = mailApi;
        this.mapperService = mapperService;
    }

    public User saveEntity(final User user) {
        return userRepository.save(user);
    }

    public UserDto saveAndSendRegistrationMail(final UserDto userDto) throws LegalValidationException {
        final User user = mapperService.map(userDto);
        final User savedUser = userRepository.save(user);
        if (user.isNew()) {
            mailApi.sendRegisterMail(Collections.singletonList(user));
        }
        return mapperService.map(savedUser);
    }

    public List<UserDto> saveAndSendRegistrationMail(final List<UserDto> userDtos) throws LegalValidationException {
        final List<User> users = userDtos.stream().map(mapperService::map).collect(Collectors.toList());
        final List<User> newUsers = users.stream()
                .filter(User::isNew)
                .collect(Collectors.toList());
        final List<User> savedUsers = userRepository.saveAll(users);
        if (!newUsers.isEmpty()) {
            mailApi.sendRegisterMail(newUsers);
        }

        return savedUsers.stream().map(mapperService::map).collect(Collectors.toList());
    }

    public UserDto getOne(final String id) {
        final User user = userRepository.findById(UUID.fromString(id)).orElseThrow(EntityNotFoundException::new);
        return mapperService.map(user);
    }

    public Page<User> findAll(final Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Optional<User> findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    public void deleteById(final String id) {
        userRepository.deleteById(UUID.fromString(id));
    }


    private List<UserDto> read(InputStream stream) throws IOException {
        final CsvSchema schema = CsvSchema.builder()
                .addColumn("firstName")
                .addColumn("lastName")
                .addColumn("email")
                .addColumn("phoneNumber")
                .addColumn("district")
                .addColumn("organisation")
                .build();
        final ObjectReader reader = csvMapper.readerFor(UserDto.class).with(schema);
        return reader.<UserDto>readValues(stream).readAll();
    }

    public List<UserDto> extractFromCsv(final MultipartFile csvFile) throws LegalValidationException {
        try {
            return extractUsers(csvFile.getInputStream());
        } catch (Exception e) {
            LOG.error("Exception while parsing the csv file", e);
            throw new LegalValidationException("user.Extract.csv.failed", HttpStatus.BAD_REQUEST);
        }
    }

    public List<UserDto> extractFromCopyPaste(final List<String> usersList) {
        final String concatenatedUsers = StringUtils.join(usersList, "\n");
        return extractUsers(new ByteArrayInputStream(concatenatedUsers.getBytes()));
    }

    public List<User> findByIds(final Collection<UUID> ids) {
        return userRepository.findAllById(ids);
    }

    private List<UserDto> extractUsers(final InputStream usersInputStream) {
        try {
            final List<UserDto> users = read(usersInputStream);
            final List<String> userEmails = users.stream()
                    .map(UserDto::getEmail)
                    .collect(Collectors.toList());
            final Map<String, User> alreadySaved = userRepository.findAllByEmailIn(userEmails).stream()
                    .collect(Collectors.toMap(User::getEmail, user -> user));

            users.forEach(user -> {
                if (alreadySaved.containsKey(user.getEmail())) {
                    user.setId(alreadySaved.get(user.getEmail()).getId());
                    user.setRole(alreadySaved.get(user.getEmail()).getRole());
                } else {
                    user.setRole(UserRole.CONTRIBUTOR);
                }
            });

            return users;
        } catch (Exception e) {
            LOG.error("Exception while parsing the input stream", e);
            throw new LegalValidationException("user.Extract.users.failed", HttpStatus.BAD_REQUEST);
        }
    }

}
