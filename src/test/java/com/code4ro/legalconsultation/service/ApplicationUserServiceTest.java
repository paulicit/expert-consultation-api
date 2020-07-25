package com.code4ro.legalconsultation.service;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.factory.RandomObjectFiller;
import com.code4ro.legalconsultation.model.dto.SignUpRequest;
import com.code4ro.legalconsultation.model.persistence.ApplicationUser;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.repository.ApplicationUserRepository;
import com.code4ro.legalconsultation.service.api.InvitationService;
import com.code4ro.legalconsultation.service.impl.ApplicationUserService;
import com.code4ro.legalconsultation.service.impl.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationUserServiceTest {
    private final SignUpRequest signUpRequest = RandomObjectFiller.createAndFill(SignUpRequest.class);
    @Mock
    private ApplicationUserRepository applicationUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserService userService;
    @Mock
    private InvitationService invitationService;
    @InjectMocks
    private ApplicationUserService applicationUserService;

    @Test
    public void save() {
        final User user = RandomObjectFiller.createAndFill(User.class);

        when(invitationService.isValid(signUpRequest)).thenReturn(true);
        when(userService.findByEmail(signUpRequest.getEmail())).thenReturn(Optional.of(user));

        applicationUserService.save(signUpRequest);

        verify(passwordEncoder).encode(signUpRequest.getPassword());
        verify(applicationUserRepository).save(any(ApplicationUser.class));
    }

    @Test(expected = LegalValidationException.class)
    public void saveDuplicateUser() {
        when(applicationUserRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(true);

        applicationUserService.save(signUpRequest);
    }

    @Test(expected = LegalValidationException.class)
    public void saveDuplicateEmail() {
        final User user = RandomObjectFiller.createAndFill(User.class);
        when(userService.findByEmail(signUpRequest.getEmail())).thenReturn(Optional.of(user));
        when(applicationUserRepository.findById(user.getId())).thenReturn(Optional.of(new ApplicationUser()));

        applicationUserService.save(signUpRequest);
    }
}
