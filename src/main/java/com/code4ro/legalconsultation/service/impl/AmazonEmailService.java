package com.code4ro.legalconsultation.service.impl;


import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AmazonEmailService {
    private static final Logger LOG = LoggerFactory.getLogger(AmazonEmailService.class);

    private static final String EMAIL_CHARSET = "UTF-8";
    private static final String HTML_EMAIL_NOT_SUPPORTED_MESSAGE = "Your email client can't open this email. Contact us at contact@code4.ro";

    private final AmazonSimpleEmailService amazonSimpleEmailService;

    private final String sender;

    public AmazonEmailService(final AmazonSimpleEmailService amazonSimpleEmailService, @Value("${app.emailSender}") String sender) {
        this.amazonSimpleEmailService = amazonSimpleEmailService;
        this.sender = sender;
    }

    public void sendEmail(String to, String subject, String htmlBody){
        sendEmail(sender, to,subject,htmlBody, HTML_EMAIL_NOT_SUPPORTED_MESSAGE);
    }

    private void sendEmail(String from, String to, String subject, String htmlBody, String textBody) {
        try {
            SendEmailRequest request = new SendEmailRequest()
                    .withSource(from)
                    .withDestination(
                            new Destination().withToAddresses(to))
                    .withMessage(new Message()
                            .withSubject(new Content()
                                    .withCharset(EMAIL_CHARSET).withData(subject))
                            .withBody(new Body()
                                            .withHtml(new Content()
                                                    .withCharset(EMAIL_CHARSET).withData(htmlBody))
                                    //Use this if there are email clients that don't support html
                                    .withText(new Content()
                                            .withCharset(EMAIL_CHARSET).withData(textBody)))
                            );
            amazonSimpleEmailService.sendEmail(request);
            LOG.debug("Email sent to %s: " + censorEmail(from));
        } catch (Exception ex) {
            LOG.error(String.format("The email from: %s was not sent to user: %s. Reason: %s", from, censorEmail(to), ex));
        }
    }

    /**
     *  Censoring second half of email. Main purpose to be used for logging.
     *  e.g. : popescu.ion@gmail.com -> popes*****@gmail.com
     */
    private static String censorEmail(String email){
        String emailWithoutDomain = email.split("@")[0];
        StringBuilder sb = new StringBuilder();
        for (int i=0; i< email.length(); i++){
            if (i>=emailWithoutDomain.length()/2 && i<emailWithoutDomain.length()){
                sb.append('*');
            } else {
                sb.append(email.charAt(i));
            }
        }
        return sb.toString();
    }

}
