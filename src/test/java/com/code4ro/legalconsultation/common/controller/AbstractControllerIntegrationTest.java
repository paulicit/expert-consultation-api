package com.code4ro.legalconsultation.common.controller;

import com.code4ro.legalconsultation.factory.RandomObjectFiller;
import com.code4ro.legalconsultation.model.dto.SignUpRequest;
import com.code4ro.legalconsultation.model.persistence.Invitation;
import com.code4ro.legalconsultation.model.persistence.InvitationStatus;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.repository.InvitationRepository;
import com.code4ro.legalconsultation.repository.UserRepository;
import com.code4ro.legalconsultation.service.impl.ApplicationUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractControllerIntegrationTest {

    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected ApplicationUserService applicationUserService;
    @MockBean
    protected JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvitationRepository invitationRepository;

    protected static String endpoint(Object... args) {
        final List<String> stringArgs = Arrays.stream(args)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toList());
        return StringUtils.join(stringArgs, '/');
    }

    protected void persistMockedUser() {
        final User user = userRepository.save(RandomObjectFiller.createAndFill(User.class));
        final Invitation invitation = RandomObjectFiller.createAndFill(Invitation.class);
        invitation.setUser(user);
        invitation.setStatus(InvitationStatus.PENDING);
        invitationRepository.save(invitation);

        final SignUpRequest signUpRequest = RandomObjectFiller.createAndFill(SignUpRequest.class);
        signUpRequest.setUsername("user");
        signUpRequest.setPassword("password");
        signUpRequest.setInvitationCode(invitation.getCode());
        signUpRequest.setEmail(user.getEmail());
        applicationUserService.save(signUpRequest);
    }
}
