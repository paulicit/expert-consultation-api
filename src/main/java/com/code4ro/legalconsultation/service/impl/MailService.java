package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.service.api.MailApi;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Profile("production")
public class MailService implements MailApi {
    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    @Value("${app.signupurl}")
    private String signupUrl;

    @Value("${spring.mvc.locale}")
    private String configuredLocale;

    private final AmazonEmailService mailSender;
    private final I18nService i18nService;
    private final Configuration freemarkerConfig;

    @Autowired
    public MailService(final AmazonEmailService mailSender,
                       final I18nService i18nService,
                       final Configuration freemarkerConfig) {
        this.mailSender = mailSender;
        this.i18nService = i18nService;
        this.freemarkerConfig = freemarkerConfig;
    }

    @Override
    public void sendRegisterMail(final List<User> users) throws LegalValidationException {
        final List<String> failedEmails = new ArrayList<>();
        users.forEach(user -> {
            try {
                final Template template = freemarkerConfig.getTemplate(getRegisterTemplate());
                final String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, getRegisterModel(user));
                final String subject = i18nService.translate("register.User.confirmation.subject");
                mailSender.sendEmail(user.getEmail(), subject, content);
            } catch (final Exception e) {
                LOG.error("Problem preparing or sending email to user with address {}", user.getEmail(), e);
                failedEmails.add(user.getEmail());
            }
        });
        if (!failedEmails.isEmpty()) {
            throw new LegalValidationException("user.Email.send.failed", failedEmails, HttpStatus.BAD_REQUEST);
        }
    }

    private String getRegisterTemplate() {
        return "register-email-" + configuredLocale + ".ftl";
    }

    private Map<String, String> getRegisterModel(final User user) {
        return Map.of(
                "username", getUserName(user),
                "signupurl", getSignupUrl(user)
        );
    }

    private String getSignupUrl(final User user) {
        return signupUrl + '/' + user.getEmail();
    }

    private String getUserName(final User user) {
        final String USERNAME_SEPARATOR = " ";
        return Stream.of(user.getFirstName(), user.getLastName())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(USERNAME_SEPARATOR));
    }
}
