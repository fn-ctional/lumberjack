package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.LumberjackApplication;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.OnRegistrationCompleteEvent;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;


import java.util.Properties;
import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private IUserService service;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        AdminUser adminUser = event.getAdminUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(adminUser, token);

        String recipientAddress = adminUser.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl
                = event.getAppUrl() + "/registrationConfirm?token=" + token;
        String message = "Registration Successful ";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "<a href=\"" + "http://localhost:8080" + confirmationUrl + "\"\\a>");
        email.setFrom(env.getProperty("support.email"));

        final Logger log = LoggerFactory.getLogger(LumberjackApplication.class);
        log.info("About to send email!");
        mailSender.send(email);
    }
}
