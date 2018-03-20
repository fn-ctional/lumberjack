package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.ui.Model;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUserDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.VerificationToken;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.EmailExistsException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.EmailNotPermittedException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.WebBackend;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Locale;

@Controller
public class AuthenticationController extends WebMvcConfigurerAdapter {

    @Autowired
    private WebBackend webBackend;

    @Autowired
    private AuthenticationBackend authenticationBackend;

    @Autowired
    private IUserService userService;

    @Autowired
    private Environment environment;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping(value = "/registrationConfirm")
    public String confirmRegistration
            (WebRequest request, Model model, @RequestParam("token") String token) {

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            model.addAttribute("messageType", "Bad Verification Link");
            model.addAttribute("messageString", "Try and register again.");
            return "message";
        }

        AdminUser user = verificationToken.getAdminUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            model.addAttribute("messageType", "Token Expired");
            model.addAttribute("messageString", "Your verification token expired, try to register again.");
            model.addAttribute("expired", true);
            model.addAttribute("token", token);
            return "message";
        }

        user.setEnabled(true);
        try {
            userService.saveRegisteredUser(user);
            model.addAttribute("messageType", "Registration Successful");
            model.addAttribute("messageString", "You can now login.");
            return "message";
        } catch (SQLException e) {
            model.addAttribute("messageType", "Database Error");
            model.addAttribute("messageString", "Sorry! Try and register again.");
            return "message";
        } catch (MailAuthenticationException e) {
            model.addAttribute("messageType", "Email Server Error");
            model.addAttribute("messageString", "Sorry! Try and register again.");
            return "message";
        } catch (Exception e) {
            model.addAttribute("messageType", "Unknown Server Error");
            model.addAttribute("messageString", "Sorry! Try and register again.");
            return "message";
        }
    }

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @GetMapping(value = "/registration")
    public String showRegistrationForm(WebRequest request, Model model) {
        AdminUserDTO userDto = new AdminUserDTO();
        model.addAttribute("user", userDto);
        return "registration";
    }

    @PostMapping("/registration")
    public ModelAndView registerUserAccount(
            @ModelAttribute("user") @Valid AdminUserDTO accountDTO,
            BindingResult result,
            WebRequest request,
            Errors errors,
            Model model) {
        if (result.hasErrors()) {
            return new ModelAndView("registration", "user", accountDTO);
        }
        try {
            AdminUser registered = createUserAccount(accountDTO, result);
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
            model.addAttribute("messageType", "Registration");
            model.addAttribute("messageString", "Check your emails for a verification link!");
        } catch (EmailNotPermittedException e) {
            model.addAttribute("messageType", "Failed Registration");
            model.addAttribute("messageString", "You are not permitted to create an account.");
        } catch (EmailExistsException e) {
            model.addAttribute("messageType", "Failed Registration");
            model.addAttribute("messageString", "A user with this email address has already registered.");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("messageType", "Registration Error");
            model.addAttribute("messageString", "Your registration failed!");
        }
        return new ModelAndView("message", "user", accountDTO);
    }

    private AdminUser createUserAccount(AdminUserDTO accountDTO, BindingResult result) throws EmailNotPermittedException, Exception {
        AdminUser registered = null;

        registered = userService.registerNewUserAccount(accountDTO);

        return registered;
    }



    @GetMapping(value = "/user/resendRegistrationToken")
    @ResponseBody
    public ModelAndView resendRegistrationToken(
            HttpServletRequest request, @RequestParam("token") String existingToken, Model model) {
        try {
            VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
            AdminUser user = userService.getUser(newToken.getToken());
            String appUrl =
                "http://" + request.getServerName() +
                        ":" + request.getServerPort() +
                        request.getContextPath();
            SimpleMailMessage email =
                constructResendVerificationTokenEmail(appUrl, request.getLocale(), newToken, user);
            mailSender.send(email);
            model.addAttribute("messageType", "Successfully resent!");
            model.addAttribute("messageString", "Please check your inbox!");
        } catch (SQLException e) {
            model.addAttribute("messageType", "Database Error!");
            model.addAttribute("messageString", "Please try again.");
        }
        catch (Exception e) {
            model.addAttribute("messageType", "Unknown Server Error!");
            model.addAttribute("messageString", "Please try again.");
        }

        return new ModelAndView("message", "user", hashCode());
    }

    private SimpleMailMessage constructResendVerificationTokenEmail
            (String contextPath, Locale locale, VerificationToken newToken, AdminUser adminUser) {
        String confirmationUrl =
                contextPath + "/regitrationConfirm.html?token=" + newToken.getToken();
        String message = "please resend please";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject("Resend Registration Token");
        email.setText(message + " rn" + confirmationUrl);
        email.setFrom(environment.getProperty("support.email"));
        email.setTo(adminUser.getEmail());
        return email;
    }

}
