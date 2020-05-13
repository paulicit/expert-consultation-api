package com.code4ro.legalconsultation.controller;

import com.code4ro.legalconsultation.service.api.MailApi;
import com.code4ro.legalconsultation.service.impl.AmazonEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * This EmailController is used only for testing purposes! Should not be used in production.
 */
@RestController
@RequestMapping("/send-email")
@RequiredArgsConstructor
public class EmailController {

    private final AmazonEmailService amazonEmailService;
    private final MailApi mailService;

    /**
     * Do not use this in production ! It's a utility endpoint and should be used only for testing purposes.
     */
    @PostMapping("/")
    public void sendEmailTest(@RequestParam String to, @RequestParam String subject, @RequestBody String body){
//        User user = new User();
//        user.setEmail(<yourSESValidatedTestEmail>);
//        mailService.sendRegisterMail(Collections.singletonList(user));
        amazonEmailService.sendEmail(to,subject,body);
    }
}
