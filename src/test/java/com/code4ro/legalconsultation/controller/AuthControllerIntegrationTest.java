package com.code4ro.legalconsultation.controller;


import com.code4ro.legalconsultation.common.controller.AbstractControllerIntegrationTest;
import com.code4ro.legalconsultation.factory.RandomObjectFiller;
import com.code4ro.legalconsultation.model.dto.LoginRequest;
import com.code4ro.legalconsultation.model.dto.SignUpRequest;
import com.code4ro.legalconsultation.model.persistence.Invitation;
import com.code4ro.legalconsultation.model.persistence.InvitationStatus;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.repository.ApplicationUserRepository;
import com.code4ro.legalconsultation.repository.InvitationRepository;
import com.code4ro.legalconsultation.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvitationRepository invitationRepository;

    @Test
    @Transactional
    public void signUp() throws Exception {
        final User user = userRepository.save(RandomObjectFiller.createAndFill(User.class));
        final Invitation invitation = RandomObjectFiller.createAndFill(Invitation.class);
        invitation.setUser(user);
        invitation.setStatus(InvitationStatus.PENDING);
        invitationRepository.save(invitation);

        final SignUpRequest signUpRequest = RandomObjectFiller.createAndFill(SignUpRequest.class);
        signUpRequest.setInvitationCode(invitation.getCode());
        signUpRequest.setEmail(user.getEmail());
        String json = objectMapper.writeValueAsString(signUpRequest);

        // register successfuly
        mvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertThat(applicationUserRepository.existsByUsername(signUpRequest.getUsername())).isNotNull();

        // fail to register with same username
        mvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.i18nFieldErrors.username.i18nErrorKey")
                        .value("register.Duplicate.username"))
                .andExpect(status().isConflict());

        // fail to register with same email
        signUpRequest.setUsername("userName2");
        json = objectMapper.writeValueAsString(signUpRequest);
        mvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.i18nFieldErrors.email.i18nErrorKey")
                        .value("register.Duplicate.email"))
                .andExpect(status().isConflict());
    }

    @Test
    @Transactional
    public void login() throws Exception {
        final User user = userRepository.save(RandomObjectFiller.createAndFill(User.class));
        final Invitation invitation = RandomObjectFiller.createAndFill(Invitation.class);
        invitation.setUser(user);
        invitation.setStatus(InvitationStatus.PENDING);
        invitationRepository.save(invitation);

        // register user
        final SignUpRequest signUpRequest = RandomObjectFiller.createAndFill(SignUpRequest.class);
        signUpRequest.setInvitationCode(invitation.getCode());
        signUpRequest.setEmail(user.getEmail());
        String json = objectMapper.writeValueAsString(signUpRequest);
        mvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        final LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail(signUpRequest.getUsername());
        loginRequest.setPassword(signUpRequest.getPassword());
        json = objectMapper.writeValueAsString(loginRequest);

        // login successfully with existing user
        mvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // fail to login
        loginRequest.setUsernameOrEmail("differentUsername");
        json = objectMapper.writeValueAsString(loginRequest);
        mvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.i18nErrors[0].i18nErrorKey")
                        .value("login.Bad.credentials"))
                .andExpect(status().isUnauthorized());
    }
}
