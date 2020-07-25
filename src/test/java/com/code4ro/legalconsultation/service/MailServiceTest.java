package com.code4ro.legalconsultation.service;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.model.persistence.Invitation;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.service.impl.I18nService;
import com.code4ro.legalconsultation.service.impl.MailService;
import com.code4ro.legalconsultation.factory.RandomObjectFiller;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private I18nService i18nService;
    @Mock
    private Configuration freemarkerConfig;

    @InjectMocks
    private MailService mailService;

    @Captor
    private ArgumentCaptor<Map<String, String>> modelCaptor;

    @Test
    public void sendRegisterMail() throws IOException, TemplateException {
        ReflectionTestUtils.setField(mailService, "signupUrl", "signupurl");
        ReflectionTestUtils.setField(mailService, "from", "email@legalconsultingtest.ro");
        final User user = RandomObjectFiller.createAndFill(User.class);
        final Invitation invitation = RandomObjectFiller.createAndFill(Invitation.class);
        invitation.setUser(user);
        final MimeMessage message = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);
        final Template template = mock(Template.class);
        when(freemarkerConfig.getTemplate(anyString())).thenReturn(template);
        when(i18nService.translate("register.User.confirmation.subject")).thenReturn("subject");

        mailService.sendRegisterMail(Collections.singletonList(invitation));

        verify(mailSender).send(message);
        verify(template).process(modelCaptor.capture(), any());
        assertThat(modelCaptor.getValue().get("username")).isEqualTo(user.getFirstName() + ' ' + user.getLastName());
        assertThat(modelCaptor.getValue().get("signupurl")).isEqualTo("signupurl" + '/' + invitation.getCode());
    }

    @Test(expected = LegalValidationException.class)
    public void sendRegisterMailFailed() {
        final User user = RandomObjectFiller.createAndFill(User.class);
        final Invitation invitation = RandomObjectFiller.createAndFill(Invitation.class);
        invitation.setUser(user);
        final MimeMessage message = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);

        mailService.sendRegisterMail(Collections.singletonList(invitation));
    }
}
