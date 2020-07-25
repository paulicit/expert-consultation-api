package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.common.exceptions.I18nError;
import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.model.dto.SignUpRequest;
import com.code4ro.legalconsultation.model.persistence.ApplicationUser;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.model.persistence.UserRole;
import com.code4ro.legalconsultation.repository.ApplicationUserRepository;
import com.code4ro.legalconsultation.service.api.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.Optional;

@Service
public class ApplicationUserService {
    private final ApplicationUserRepository applicationUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final InvitationService invitationService;

    @Autowired
    public ApplicationUserService(final ApplicationUserRepository applicationUserRepository,
                                  final PasswordEncoder passwordEncoder,
                                  final UserService userService,
                                  final InvitationService invitationService) {
        this.applicationUserRepository = applicationUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.invitationService = invitationService;
    }

    @Transactional
    @CachePut(cacheNames = "users")
    public ApplicationUser save(SignUpRequest signUpRequest) throws LegalValidationException {
        if (applicationUserRepository.existsByUsername(signUpRequest.getUsername())) {
            throw LegalValidationException.builder()
                    .i18nFieldErrors(Map.of("username", new I18nError("register.Duplicate.username")))
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
        }

        final ApplicationUser applicationUser = new ApplicationUser(signUpRequest.getName(),
                signUpRequest.getUsername(), signUpRequest.getPassword());
        applicationUser.setPassword(passwordEncoder.encode(applicationUser.getPassword()));
        final User user = getUser(signUpRequest.getEmail());

        if (!invitationService.isValid(signUpRequest)) {
            throw LegalValidationException.builder()
                    .i18nKey("user.invitation.invalid")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }

        applicationUser.setUser(user);
        final ApplicationUser savedApplicationUser = applicationUserRepository.save(applicationUser);

        invitationService.markAsUsed(signUpRequest.getInvitationCode());

        return savedApplicationUser;
    }

    @Transactional(readOnly = true)
    public ApplicationUser getByUsernameOrEmail(final String usernameOrEmail) {
        return applicationUserRepository.findByUsernameOrEmail(usernameOrEmail).orElseThrow(() ->
                LegalValidationException.builder()
                        .i18nKey("login.Bad.credentials")
                        .httpStatus(HttpStatus.UNAUTHORIZED)
                        .build()
        );
    }

    private User getUser(final String email) {
        final Optional<User> byEmail = userService.findByEmail(email);

        //if user is persisted but with a different email address throw exception
        byEmail.flatMap(user -> applicationUserRepository.findById(user.getId())).ifPresent(e -> {
            throw LegalValidationException.builder()
                    .i18nFieldErrors(Map.of("email", new I18nError("register.Duplicate.email")))
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
        });

        return byEmail.orElseThrow(EntityNotFoundException::new);
    }

}
