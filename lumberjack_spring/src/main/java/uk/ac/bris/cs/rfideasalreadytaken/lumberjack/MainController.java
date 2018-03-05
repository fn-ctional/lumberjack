package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUserDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.ScanDTO;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Locale;

@Controller
public class MainController extends WebMvcConfigurerAdapter {

    @Autowired
    private PiDatabaseManager piDatabaseManager;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/about");
        registry.addViewController("/download");
        registry.addViewController("/login");
    }

    /**
     * Handler for taking out and returning device scans.
     * @param scanDTO A JSON containing device and user strings.
         * @return An HTTP status code and body description of error or action performed.
     */
    @PatchMapping(value = "/devices", consumes = "application/json", produces = "text/plain")
    @ResponseBody
    public ResponseEntity changeDeviceState(@RequestBody ScanDTO scanDTO) {
        try {
            ScanReturn result = piDatabaseManager.scanReceived(scanDTO);
            switch (result) {
                case SUCCESSRETURN:
                    return ResponseEntity.status(200).body("Device " + scanDTO.getDevice() + " successfully returned by " + scanDTO.getUser() + ".");
                case SUCCESSREMOVAL:
                    return ResponseEntity.status(200).body("Device " + scanDTO.getDevice() + " successfully taken out by " + scanDTO.getUser() + ".");
                case SUCCESSRETURNANDREMOVAL:
                    return ResponseEntity.status(200).body("Device " + scanDTO.getDevice() + " successfully returned and taken out by " + scanDTO.getUser() + ".");
                case FAILUSERNOTRECOGNISED:
                    return ResponseEntity.status(500).body("User not recognised");
                case FAILDEVICENOTRECOGNISED:
                    return ResponseEntity.status(500).body("Device not recognised");
                case FAILUSERATDEVICELIMIT:
                    return ResponseEntity.status(403).body("User " + scanDTO.getUser() + " is at their limit of removable devices.");
                case FAILUSERNORPERMITTEDTOREMOVE:
                    return ResponseEntity.status(403).body("User " + scanDTO.getUser() + " is not permitted to remove");
                case FAILDEVICEUNAVIALABLE:
                    return ResponseEntity.status(403).body("Device " + scanDTO.getDevice() + " can not be taken out.");
                case ERRORCONNECTIONFAILED:
                    return ResponseEntity.status(500).body("Error connecting to database.");
                case ERRORUSERNOTLOADED:
                    return ResponseEntity.status(403).body("User " + scanDTO.getUser() + " not recognised.");
                case ERRORDEVICENOTLOADED:
                    return ResponseEntity.status(403).body("Device " + scanDTO.getDevice() + " not recognised.");
                case ERRORDEVICEHANDLINGFAILED:
                    return ResponseEntity.status(500).body("Error handling device return or takeout.");
                case ERRORRETURNFAILED:
                    return ResponseEntity.status(500).body("Error returning device.");
                case ERRORREMOVALFAILED:
                    return ResponseEntity.status(500).body("Error taking out device.");
                default:
                    throw new Exception();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Unknown server error.");
        }
    }

    @GetMapping(value = "/register")
    public String showRegistrationForm(WebRequest request, Model model) {
        AdminUserDTO userDto = new AdminUserDTO();
        model.addAttribute("user", userDto);
        return "register";
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private IUserService userService;

    @Autowired
    private MessageSource messages;

    @GetMapping(value = "/registrationConfirm")
    public String confirmRegistration
            (WebRequest request, Model model, @RequestParam("token") String token) {

        Locale locale = request.getLocale();

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            String message = messages.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:/badUser.html";
        }

        AdminUser user = verificationToken.getAdminUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String messageValue = messages.getMessage("auth.message.expired", null, locale);
            model.addAttribute("message", messageValue);
            return "redirect:/badUser.html";
        }

        user.setEnabled(true);
        userService.saveRegisteredUser(user);
        return "redirect:/login.html";
    }
    /*
    @PostMapping("/register")
    public ModelAndView registerUserAccount(
            @ModelAttribute("user") @Valid AdminUserDTO accountDTO,
            BindingResult result, WebRequest request, Errors errors) {

        AdminUser registered = new AdminUser();
        if (!result.hasErrors()) {
            try {
                userService.registerNewUserAccount(accountDTO);
            } catch (EmailExistsException e) {
                result.rejectValue("email", "message.regError");
            } catch (EmailNotPermittedException e) {
                //TODO: This should say something different but I can't work out where it is
                result.rejectValue("email", "message.regError");
            }
        }
        if (result.hasErrors()) {
            return new ModelAndView("registration", "user", accountDTO);
        }
        else {
            return new ModelAndView("successRegister", "user", accountDTO);
        }
    }
    */
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/registration")
    public ModelAndView registerUserAccount(
            @ModelAttribute("user") @Valid AdminUserDTO accountDTO,
            BindingResult result,
            WebRequest request,
            Errors errors) {
        if (result.hasErrors()) {
            return new ModelAndView("registration", "user", accountDTO);
        }
        try {
            AdminUser registered = createUserAccount(accountDTO, result);
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
        } catch (EmailNotPermittedException e) {
            //TODO: Implement this
        } catch (EmailExistsException e) {
            //TODO: Implement this
        }
        return new ModelAndView("successRegister", "user", accountDTO);
    }

    private AdminUser createUserAccount(AdminUserDTO accountDTO, BindingResult result) throws EmailNotPermittedException, EmailExistsException {
        AdminUser registered = null;

        registered = userService.registerNewUserAccount(accountDTO);

        return registered;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //TODO: Make this user name instead of email
        String name = auth.getName();
        model.addAttribute("name", name);
        return "dashboard";
    }
}
