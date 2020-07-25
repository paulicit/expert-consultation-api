package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.converters.UserMapper;
import com.code4ro.legalconsultation.model.dto.UserDto;
import com.code4ro.legalconsultation.model.persistence.Invitation;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.model.persistence.UserRole;
import com.code4ro.legalconsultation.repository.UserRepository;
import com.code4ro.legalconsultation.service.api.InvitationService;
import com.code4ro.legalconsultation.service.api.MailApi;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
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

@Slf4j
@Service
public class UserService {
    private static final String COMMA_REGEX = ",";

    private final UserRepository userRepository;
    private final CsvMapper csvMapper = new CsvMapper();
    private final MailApi mailApi;
    private final UserMapper mapperService;
    private final InvitationService invitationService;

    @Autowired
    public UserService(final UserRepository userRepository,
                       final MailApi mailApi,
                       final UserMapper mapperService,
                       final InvitationService invitationService) {
        this.userRepository = userRepository;
        this.mailApi = mailApi;
        this.mapperService = mapperService;
        this.invitationService = invitationService;
    }

    public User saveEntity(final User user) {
        return userRepository.save(user);
    }

    public UserDto saveAndSendRegistrationMail(final UserDto userDto) throws LegalValidationException {
        final User user = mapperService.map(userDto);
        final boolean isNew = user.isNew();
        final User savedUser = userRepository.save(user);

        if (isNew) {
            final Invitation invitation = invitationService.create(savedUser);
            mailApi.sendRegisterMail(Collections.singletonList(invitation));
        }

        return mapperService.map(savedUser);
    }

    public List<UserDto> saveAndSendRegistrationMail(final List<UserDto> userDtos) throws LegalValidationException {
        final List<User> allUsers = userDtos.stream()
                .map(mapperService::map)
                .collect(Collectors.toList());

        final List<String> newUserEmails = allUsers.stream()
                .filter(User::isNew)
                .map(User::getEmail)
                .collect(Collectors.toList());

        final List<User> savedUsers = userRepository.saveAll(allUsers);

        final List<Invitation> invitations = savedUsers.stream()
                .filter(user -> newUserEmails.contains(user.getEmail()))
                .map(invitationService::create)
                .collect(Collectors.toList());
        if (!invitations.isEmpty()) {
            mailApi.sendRegisterMail(invitations);
        }

        return savedUsers.stream()
                .map(mapperService::map)
                .collect(Collectors.toList());
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
            return extractUsersFromInputStream(csvFile.getInputStream());
        } catch (Exception e) {
            log.error("Exception while parsing the csv file", e);
            throw LegalValidationException.builder()
                    .i18nKey("user.Extract.csv.failed")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    public List<UserDto> extractFromCopyPaste(final List<String> usersList) {
        final String concatenatedUsers = StringUtils.join(usersList, "\n");
        return extractUsersFromInputStream(new ByteArrayInputStream(concatenatedUsers.getBytes()));
    }

    public List<User> findByIds(final Collection<UUID> ids) {
        return userRepository.findAllById(ids);
    }

    public List<UserDto> extractUsersFromInputStream(final InputStream usersInputStream) {
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
            log.error("Exception while parsing the input stream", e);
            throw LegalValidationException.builder()
                    .i18nKey("user.Extract.users.failed")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    public List<User> searchByTerm(final String term) {
        User userWithDesiredFields = new User();
        userWithDesiredFields.setFirstName(term);
        userWithDesiredFields.setLastName(term);
        userWithDesiredFields.setEmail(term);

        ExampleMatcher anyMatcher = ExampleMatcher.matchingAny()
                .withMatcher("firstName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("lastName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        return userRepository.findAll(Example.of(userWithDesiredFields, anyMatcher));
    }

}
