package com.code4ro.legalconsultation.config.mail;

import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import com.code4ro.legalconsultation.model.persistence.Invitation;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.service.api.MailApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile("dev")
@Configuration
public class MailConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MailConfiguration.class);

    @Bean
    @Primary
    public MailApi mailApi() {
        return new MailApi() {
            @Override
            public void sendRegisterMail(List<Invitation> invitations) {
                LOG.info("Email sending not supported in dev environment.");
            }

            @Override
            public void sendDocumentAssignedEmail(DocumentMetadata documentMetadata, List<User> users) {
                LOG.info("Email sending not supported in dev environment.");
            }
        };
    }
}
