package com.code4ro.legalconsultation.service;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.converters.UserMapper;
import com.code4ro.legalconsultation.converters.UserMapperImpl;
import com.code4ro.legalconsultation.model.dto.UserDto;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.model.persistence.UserRole;
import com.code4ro.legalconsultation.repository.UserRepository;
import com.code4ro.legalconsultation.service.impl.MailService;
import com.code4ro.legalconsultation.service.impl.UserService;
import com.code4ro.legalconsultation.util.RandomObjectFiller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private MailService mailService;

    private UserMapper mapperService = new UserMapperImpl();
    private UserService userService;
    private static final String USER_AS_STRING = "john,doe,john@email.com,42345,district,org";

    @Before
    public void before() {
        this.userService = new UserService(userRepository, mailService, mapperService);
    }

    @Test
    public void saveUserAndSendRegistrationMail() {
        final UserDto userDto = RandomObjectFiller.createAndFill(UserDto.class);

        userService.saveAndSendRegistrationMail(userDto);

        verify(mailService).sendRegisterMail(anyList());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void saveUsersAndSendRegistrationMail() {
        final List<UserDto> userDtos = Arrays.asList(
                RandomObjectFiller.createAndFill(UserDto.class), RandomObjectFiller.createAndFill(UserDto.class));

        userService.saveAndSendRegistrationMail(userDtos);

        verify(mailService).sendRegisterMail(anyList());
        verify(userRepository).saveAll(anyList());
    }

    @Test
    public void getUser() {
        final String id = UUID.randomUUID().toString();
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(new User
                ("email@email.com", UserRole.CONTRIBUTOR)));
        userService.getOne(id);

        verify(userRepository).findById(UUID.fromString(id));
    }

    @Test(expected = EntityNotFoundException.class)
    public void getUserNotFound() {
        final String id = UUID.randomUUID().toString();
        userService.getOne(id);
    }

    @Test
    public void findAll() {
        //given
        final Pageable pageable = mock(Pageable.class);
        Mockito.when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(new User())));

        //when
        Page<User> all = userService.findAll(pageable);

        //then
        assertThat(all.getContent().size()).isEqualTo(1);
    }

    @Test
    public void extractExistingUsersFromCsv() throws IOException {
        final User user = RandomObjectFiller.createAndFill(User.class);
        user.setEmail("john@email.com");

        final MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(
                new ByteArrayInputStream("john,doe,john@email.com,42345,district,org".getBytes()));
        when(userRepository.findAllByEmailIn(Collections.singletonList("john@email.com")))
                .thenReturn(Collections.singletonList(user));

        final List<UserDto> results = userService.extractFromCsv(file);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getId()).isEqualTo(user.getId());
        assertThat(results.get(0).getRole()).isEqualTo(user.getRole());
    }

    @Test
    public void extractNewUsersFromCsv() throws IOException {
        final User user = RandomObjectFiller.createAndFill(User.class);
        user.setEmail("john@email.com");
        user.setId(null);

        final MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(
                new ByteArrayInputStream("john,doe,john@email.com,42345,district,org".getBytes()));

        final List<UserDto> results = userService.extractFromCsv(file);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getId()).isNull();
        assertThat(results.get(0).getRole()).isEqualTo(UserRole.CONTRIBUTOR);
    }

    @Test(expected = LegalValidationException.class)
    public void extractUsersInvalidFile() {
        userService.extractFromCsv(null);
    }

    @Test
    public void extractUsersFromCopy() {
        final List<String> usersList = Collections.singletonList(USER_AS_STRING);
        final List<UserDto> results = userService.extractFromCopyPaste(usersList);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("john");
        assertThat(results.get(0).getLastName()).isEqualTo("doe");
        assertThat(results.get(0).getEmail()).isEqualTo("john@email.com");
        assertThat(results.get(0).getPhoneNumber()).isEqualTo("42345");
        assertThat(results.get(0).getDistrict()).isEqualTo("district");
        assertThat(results.get(0).getOrganisation()).isEqualTo("org");
        assertThat(results.get(0).getRole()).isEqualTo(UserRole.CONTRIBUTOR);
    }
}
