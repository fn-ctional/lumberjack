package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.ui.Model;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.EmailExistsException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.EmailNotPermittedException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.NotFoundException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.WebBackend;

import javax.servlet.GenericServlet;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

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

    /**
     * Registration confirmation from the email link.
     * @param request
     * @param model
     * @param token
     * @return
     */
    @GetMapping(value = "/registrationConfirm")
    public String confirmRegistration
            (WebRequest request, Model model, @RequestParam("token") String token) {

        Token verificationToken = userService.getVerificationToken(token);
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

    /**
     * GET mapping for the registration page.
     * @param request
     * @param model
     * @return
     */
    @GetMapping(value = "/registration")
    public String showRegistrationForm(WebRequest request, Model model) {
        AdminUserDTO userDto = new AdminUserDTO();
        model.addAttribute("user", userDto);
        return "registration";
    }

    /**
     * POST mapping for the registration form.
     * @param accountDTO An accountDTO object containing the new user information. (Password plaintext at this stage).
     * @param result
     * @param request
     * @param errors
     * @param model
     * @return
     */
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
        return userService.registerNewUserAccount(accountDTO);
    }

    @GetMapping(value = "/user/resendRegistrationToken")
    @ResponseBody
    public ModelAndView resendRegistrationToken(
            HttpServletRequest request, @RequestParam("token") String existingToken, Model model) {
        try {
            Token newToken = userService.generateNewVerificationToken(existingToken);
            AdminUser user = userService.getUser(newToken.getToken());
            String appUrl =
                "http://" + request.getServerName() +
                        ":" + request.getServerPort() +
                        request.getContextPath();
            SimpleMailMessage email =
                constructResendVerificationTokenEmail(appUrl, newToken, user);
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
            (String contextPath, Token newToken, AdminUser adminUser) {
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

    @PostMapping(value = "/user/resetPassword")
    @ResponseBody
    public ModelAndView resetPassword(@RequestParam("email") String userEmail, Model model) {
        AdminUser adminUser = authenticationBackend.findByEmail(userEmail);
        if (adminUser == null) {
            model.addAttribute("messageType", "Email not found!");
            model.addAttribute("messageString", "The administrator you are trying to change the password of was not found!");
            return new ModelAndView("message", "user", hashCode());
        }
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(adminUser, token);
        mailSender.send(constructResetTokenEmail(token, adminUser));
        model.addAttribute("messageType", "Reset email sent!");
        model.addAttribute("messageString", "Please check your inbox!");
        return new ModelAndView("message", "user", hashCode());
    }

    private SimpleMailMessage constructResetTokenEmail(String token, AdminUser adminUser) {
        String url = "/user/changePassword?id=" + adminUser.getEmail() + "&token=" + token;
        String body = "Please click the following link to reset your password!";
        return constructEmail("Reset Password", body + " \r\n" + url, adminUser);
    }

    private SimpleMailMessage constructEmail(String subject, String body, AdminUser adminUser) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(adminUser.getEmail());
        email.setFrom(environment.getProperty("support.email"));
        return email;
    }

    /**
     * Request to send a change password email to an admin.
     * @param id
     * @param token
     * @param model
     * @return
     */
    @GetMapping(value = "/user/changePassword")
    public ModelAndView showChangePasswordPage(@RequestParam("id") String id, @RequestParam("token") String token, Model model) {

        String result = authenticationBackend.validatePasswordResetToken(id, token);
        if (result != null) {
            model.addAttribute("messageType", "Not permitted to change this password!");
            model.addAttribute("messageString", "Please try again!");
            return new ModelAndView("message", "user", hashCode());
        }
        model.addAttribute("messageType", "Password successfully changed!");
        model.addAttribute("messageString", "You can now log in with the new password!");
        return new ModelAndView("message", "user", hashCode());
    }

    /**
     * POST request mapping for changing the currently logged in admin's password.
     * @param passwordDto Containing the new password, but email field is not used.
     * @param model
     * @return
     */
    @PostMapping(value = "/user/savePassword")
    @ResponseBody
    public ModelAndView savePassword(PasswordDTO passwordDto, Model model) {
        AdminUser adminUser =
                (AdminUser) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal();

        try {
            userService.changeUserPassword(adminUser, passwordDto.getNewPassword());
        } catch (SQLException e) {
            model.addAttribute("messageType", "Database Error!");
            model.addAttribute("messageString", "Please try again!");
            return new ModelAndView("message", "user", hashCode());
        }
        model.addAttribute("messageType", "Reset email sent!");
        model.addAttribute("messageString", "Please check your inbox!");
        return new ModelAndView("message", "user", hashCode());
    }


    /**
     * POST request mapping for changing the password of any user.
     * @param passwordDto Containing email and new password.
     * @param model
     * @return
     */
    @PostMapping(value = "/user/changePassword")
    @ResponseBody
    public ModelAndView manualChangePassword(PasswordDTO passwordDto, Model model) {
        AdminUser adminUser = authenticationBackend.findByEmail(passwordDto.getEmail());
        try {
            userService.changeUserPassword(adminUser, passwordDto.getNewPassword());
        } catch (SQLException e) {
            model.addAttribute("messageType", "Database Error!");
            model.addAttribute("messageString", "Please try again!");
            return new ModelAndView("message", "user", hashCode());
        }
        model.addAttribute("messageType", "Reset email sent!");
        model.addAttribute("messageString", "Please check your inbox!");
        return new ModelAndView("message", "user", hashCode());
    }

}
