package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.OnRegistrationCompleteEvent;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.AdminUser;


import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private IUserService service;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

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
                = event.getAppUrl() + "/registrationConfirm.html?token=" + token;
        String message = messages.getMessage("message.regSucc", null, event.getLocale());

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " rn" + "http://localhost:8080" + confirmationUrl);
        mailSender.send(email);
    }
}
