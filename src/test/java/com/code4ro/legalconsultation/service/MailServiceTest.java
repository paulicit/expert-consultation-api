package com.code4ro.legalconsultation.service;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.service.impl.AmazonEmailService;
import com.code4ro.legalconsultation.service.impl.I18nService;
import com.code4ro.legalconsultation.service.impl.MailService;
import com.code4ro.legalconsultation.util.RandomObjectFiller;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MailServiceTest {

    @Mock
    private I18nService i18nService;
    @Mock
    private Configuration freemarkerConfig;
    @Mock
    private AmazonEmailService amazonEmailService;

    @InjectMocks
    private MailService mailService;

    @Captor
    private ArgumentCaptor<Map<String, String>> modelCaptor;

    @Test
    public void sendRegisterMail() throws IOException, TemplateException {
        ReflectionTestUtils.setField(mailService, "signupUrl", "signupurl");
        final User user = RandomObjectFiller.createAndFill(User.class);
        final Template template = mock(Template.class);
        when(freemarkerConfig.getTemplate(anyString())).thenReturn(template);
        when(i18nService.translate("register.User.confirmation.subject")).thenReturn("subject");

        mailService.sendRegisterMail(Collections.singletonList(user));

        verify(amazonEmailService).sendEmail(eq(user.getEmail()), eq("subject"), anyString());
        verify(template).process(modelCaptor.capture(), any());
        assertThat(modelCaptor.getValue().get("username")).isEqualTo(user.getFirstName() + ' ' + user.getLastName());
        assertThat(modelCaptor.getValue().get("signupurl")).isEqualTo("signupurl" + '/' + user.getEmail());
    }

    @Test(expected = LegalValidationException.class)
    public void sendRegisterMailFailed() {
        final User user = RandomObjectFiller.createAndFill(User.class);

        mailService.sendRegisterMail(Collections.singletonList(user));
    }
}
