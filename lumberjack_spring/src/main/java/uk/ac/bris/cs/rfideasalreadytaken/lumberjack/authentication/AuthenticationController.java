package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader.CardReaderBackend;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.EmailExistsException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.EmailNotPermittedException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.WebBackend;

import javax.validation.Valid;
import java.util.Calendar;

@Controller
public class AuthenticationController extends WebMvcConfigurerAdapter {

    @Autowired
    private WebBackend webBackend;

    @Autowired
    private AuthenticationBackend authenticationBackend;

    @Autowired
    private IUserService userService;

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
            return "message";
        }

        user.setEnabled(true);
        try {
            userService.saveRegisteredUser(user);
            model.addAttribute("messageType", "Registration Successful");
            model.addAttribute("messageString", "You can now login.");
            return "message";
        } catch (Exception e) {
            model.addAttribute("messageType", "Server Error");
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

}
