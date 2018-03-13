package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
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
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.*;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController extends WebMvcConfigurerAdapter {

    @Autowired
    private BackendCardReaderManager backend;

    @Autowired
    private BackendFrontEndManager backendFrontEndManager;

    @Autowired
    private AuthenticationDatabaseManager authenticationDatabaseManager;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/about");
        registry.addViewController("/download");
        registry.addViewController("/login");
    }

    /**
     * Handler for taking out and returning device scans.
     *
     * @param scanDTO A JSON containing device and user strings.
     * @return An HTTP status code and body description of error or action performed.
     */
    @PatchMapping(value = "/rpi", consumes = "application/json", produces = "text/plain")
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

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        AdminUser user = authenticationDatabaseManager.findByEmail(email);
        String name = user.getName();
        model.addAttribute("name", name);
        return "dashboard";
    }

    @GetMapping("/user")
    public String user(Model model) {
        model.addAttribute("blank", true);
        return "users";
    }

    @GetMapping("/users")
    public String allUsers(Model model) {
        model.addAttribute("multi", true);
        Boolean found = false;
        List<User> userList = new ArrayList<>();
        try {
            userList = backendFrontEndManager.getUsers();
            if (!userList.isEmpty()) {
                found = true;
            }
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
        model.addAttribute("found", found);
        model.addAttribute(userList);
        return "users";
    }

    @GetMapping("/user/{id}")
    public String userSpecified(@PathVariable String id, Model model) {
        List<User> userList = new ArrayList<>();
        Boolean found = false;
        model.addAttribute("searchTerm", id);
        try {
            User user = backendFrontEndManager.getUser(id);
            if (user.getId().equals(id)) {
                userList.add(user);
                found = true;
            }
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
        model.addAttribute("found", found);
        model.addAttribute(userList);
        return "users";
    }

    @GetMapping("/device")
    public String device(Model model) {
        model.addAttribute("blank", true);
        return "devices";
    }

    @GetMapping("/devices")
    public String allDevices(Model model) {
        model.addAttribute("multi", true);
        Boolean found = false;
        List<Device> deviceList = new ArrayList<>();
        try {
            // TODO
            // deviceList = backendFrontEndManager.getDevices();
            Device device = new Device();
            deviceList.add(device);
            if (!deviceList.isEmpty()) {
                found = true;
            }
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
        model.addAttribute("found", found);
        model.addAttribute(deviceList);
        return "devices";
    }

    @GetMapping("/device/{id}")
    public String deviceSpecified(@PathVariable String id, Model model) {
        List<Device> deviceList = new ArrayList<>();
        Boolean found = false;
        model.addAttribute("searchTerm", id);
        try {
            // TODO
            //Device device = backendFrontEndManager.getDevice(id);
            Device device = new Device();
            if (device.getId().equals(id)) {
                deviceList.add(device);
                found = true;
            }
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
        model.addAttribute("found", found);
        model.addAttribute(deviceList);
        return "devices";
    }

    @RequestMapping("/search")
    public String search(Model model) {
        model.addAttribute("blank", true);
        return "search";
    }

    @GetMapping("/search/{type}")
    public String searchType(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        return "search";
    }

    @RequestMapping("/add")
    public String add(Model model) {
        model.addAttribute("blank", true);
        return "add";
    }

    @GetMapping("/add/{type}")
    public String addType(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        return "add";
    }

    @PostMapping(value = "/add/user/CSV", consumes = "text/json", produces = "text/plain")
    public String addUsersCSV(@RequestBody UsersCSVDTO usersCSVDTO, Model model) {
        List<User> newUsers = new ArrayList<>();

        for (int i = 0; i < usersCSVDTO.getScanValue().size(); i++) {
            User newUser = new User();
            newUser.setScanValue(usersCSVDTO.getScanValue().get(i));
            newUser.setDeviceLimit(usersCSVDTO.getDeviceLimit().get(i));
            newUser.setDevicesRemoved(usersCSVDTO.getDevicesRemoved().get(i));
            newUser.setCanRemove(usersCSVDTO.getCanRemove().get(i));
            newUser.setGroupId(usersCSVDTO.getGroupID().get(i));
            //TODO: how to assign the userID?

            newUsers.add(newUser);
        }
        try {
            backendFrontEndManager.insertUsers(newUsers);
            model.addAttribute("messageType", "Successful Upload");
            model.addAttribute("messageString", "New users successfully added!");
        } catch (Exception e) {
            model.addAttribute("messageType", "Failed Upload");
            model.addAttribute("messageString","Unknown CSV upload error, please try again!");
        }

        return "CSVUploaded";
    }

    @PostMapping(value = "/add/device/CSV", consumes = "text/json", produces = "text/plain")
    public String addDevicesCSV(@RequestBody DevicesCSVDTO devicesCSVDTO, Model model) {
        List<Device> newDevices = new ArrayList<>();

        for (int i = 0; i < devicesCSVDTO.getScanValue().size(); i++) {
            Device newDevice = new Device();

            newDevice.setType(devicesCSVDTO.getType().get(i));
            newDevice.setAvailable(devicesCSVDTO.getAvailable().get(i));
            newDevice.setCurrentlyAssigned(devicesCSVDTO.getAvailable().get(i));
            newDevice.setRuleID(devicesCSVDTO.getRuleID().get(i));
            newDevice.setScanValue(devicesCSVDTO.getScanValue().get(i));
            //TODO: how to set device ID?

            newDevices.add(newDevice);
        }
        try {
            backendFrontEndManager.insertDevices(newDevices);
            model.addAttribute("messageType", "Successful Upload");
            model.addAttribute("messageString", "New devices successfully added!");
        } catch (Exception e) {
            model.addAttribute("messageType", "Failed Upload");
            model.addAttribute("messageString","Unknown CSV upload error, please try again!");
        }

        return "message";
    }
}
