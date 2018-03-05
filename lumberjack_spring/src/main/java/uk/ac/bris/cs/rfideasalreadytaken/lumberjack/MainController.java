package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.AdminUserDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.EmailExistsException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.EmailNotPermittedException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.UserService;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.AdminUser;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.ScanDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController extends WebMvcConfigurerAdapter {

    @Autowired
    private BackendCardReaderManager backend;

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
            ScanReturn result = backend.scanReceived(scanDTO);
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
    private UserService userService;

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

    @GetMapping("/dashboard")
    public String dashboard(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //TODO: Make this user name instead of email
        String name = auth.getName();
        model.addAttribute("name", name);
        return "dashboard";
    }

    @GetMapping("/users")
    public String allUsers(Model model){
        // Add dummy data
        User test1 = new User("1", "812937528", 2, 0, true);
        User test2 = new User("2", "127482930", 1, 1, false);
        List<User> userList = new ArrayList<>();
        userList.add(test1);
        userList.add(test2);
        model.addAttribute(userList);
        return "users";
    }
}
