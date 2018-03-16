package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.AuthenticationBackend;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.data.DevicesCSVDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.User;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.data.UsersCSVDTO;

import java.util.ArrayList;
import java.util.List;

@Controller
public class WebController extends WebMvcConfigurerAdapter {

    @Autowired
    private WebBackend webBackend;

    @Autowired
    private AuthenticationBackend authenticationBackend;

    /**
     * Adding view controllers to serve the basic pages that don't require complex mappings.
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/about");
        registry.addViewController("/download");
        registry.addViewController("/login");
        registry.addViewController("/help");
    }

    /**
     * GET request handler for serving the admin dashboard with the active user's name as a model attribute.
     * @param model
     * @return
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        AdminUser user = authenticationBackend.findByEmail(email);
        String name = user.getName();
        model.addAttribute("name", name);
        return "dashboard";
    }

    /**
     * Basic request handler for serving the users page when no specific user is specified.
     * @param model
     * @return
     */
    @RequestMapping("/user")
    public String user(Model model) {
        model.addAttribute("blank", true);
        return "users";
    }

    /**
     * GET request handler for returning the page populated with all the users in the database.
     * @param model
     * @return
     */
    @GetMapping("/users")
    public String allUsers(Model model) {
        model.addAttribute("multi", true);
        Boolean found = false;
        List<User> userList = new ArrayList<>();
        try {
            userList = webBackend.getUsers();
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

    /**
     * GET request handler for serving the users page populated with a specified (id) user from the database.
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/user/{id}")
    public String userSpecified(@PathVariable String id, Model model) {
        List<User> userList = new ArrayList<>();
        Boolean found = false;
        model.addAttribute("searchTerm", id);
        try {
            User user = webBackend.getUser(id);
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

    /**
     * Basic request handler for serving the devices page when no specific device is specified.
     * @param model
     * @return
     */
    @GetMapping("/device")
    public String device(Model model) {
        model.addAttribute("blank", true);
        return "devices";
    }

    /**
     * GET request handler for returning the page populated with all the devices in the database.
     * @param model
     * @return
     */
    @GetMapping("/devices")
    public String allDevices(Model model) {
        model.addAttribute("multi", true);
        Boolean found = false;
        List<Device> deviceList = new ArrayList<>();
        try {
            // TODO
            // deviceList = webBackend.getDevices();
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

    /**
     * GET request handler for serving the device page populated with a specified (id) device from the database.
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/device/{id}")
    public String deviceSpecified(@PathVariable String id, Model model) {
        List<Device> deviceList = new ArrayList<>();
        Boolean found = false;
        model.addAttribute("searchTerm", id);
        try {
            // TODO
            //Device device = webBackend.getDevice(id);
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

    /**
     * Basic request handler for serving the search page when no search type is specified.
     * @param model
     * @return
     */
    @RequestMapping("/search")
    public String search(Model model) {
        model.addAttribute("blank", true);
        return "search";
    }

    /**
     * GET request handler for searching a type of entity e.g. user or device.
     * @param type
     * @param model
     * @return
     */
    @GetMapping("/search/{type}")
    public String searchType(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        return "search";
    }

    /**
     * Basic request handler for serving the add page when no add type is specified.
     * @param model
     * @return
     */
    @RequestMapping("/add")
    public String add(Model model) {
        model.addAttribute("blank", true);
        return "add";
    }

    /**
     * GET request handler for adding a type of entity e.g. user or device.
     * @param type
     * @param model
     * @return
     */
    @GetMapping("/add/{type}")
    public String addType(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        return "add";
    }

    /**
     * POST request handler for adding users through CSV upload.
     * It accepts JSON form of the CSV containing one list for each column.
     * The size of each list must be the same and any columns that do not exist in the JSON should be initialised with
     * default values.
      @param usersCSVDTO A JSON object containing:
        - List of Scan Values : scanValue
        - List of Device Limits : deviceLimit
        - List of Number of Devices Removed : devicesRemoved
        - List of Booleans, canRemove : canRemove
        - List of GroupIDs : groupID
     * @param model
     * @return
     */
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
            webBackend.insertUsers(newUsers);
            model.addAttribute("messageType", "Successful Upload");
            model.addAttribute("messageString", "New users successfully added!");
        } catch (Exception e) {
            model.addAttribute("messageType", "Failed Upload");
            model.addAttribute("messageString","Unknown CSV upload error, please try again!");
        }

        return "CSVUploaded";
    }

    /**
     * POST request handler for adding devices through CSV upload.
     * It accepts JSON form of the CSV containing one list for each column.
     * The size of each list must be the same and any columns that do not exist in the JSON should be initialised with
     * default values.
      @param devicesCSVDTO A JSON object containing:
        - List of Device Types : deviceType
        - List of Booleans, Is Available : available
        - List of Booleans, Currently Assigned : currentlyAssigned
        - List of Rule IDs : ruleID
        - List of Scan Values : scanValue
     * @param model
     * @return
     */
    @PostMapping(value = "/add/device/CSV", consumes = "text/json", produces = "text/plain")
    public String addDevicesCSV(@RequestBody DevicesCSVDTO devicesCSVDTO, Model model) {
        List<Device> newDevices = new ArrayList<>();

        for (int i = 0; i < devicesCSVDTO.getScanValue().size(); i++) {
            Device newDevice = new Device();

            newDevice.setType(devicesCSVDTO.getType().get(i));
            newDevice.setAvailable(devicesCSVDTO.getAvailable().get(i));
            newDevice.setCurrentlyAssigned(devicesCSVDTO.getCurrentlyAssigned().get(i));
            newDevice.setRuleID(devicesCSVDTO.getRuleID().get(i));
            newDevice.setScanValue(devicesCSVDTO.getScanValue().get(i));
            //TODO: how to set device ID?

            newDevices.add(newDevice);
        }
        try {
            webBackend.insertDevices(newDevices);
            model.addAttribute("messageType", "Successful Upload");
            model.addAttribute("messageString", "New devices successfully added!");
        } catch (Exception e) {
            model.addAttribute("messageType", "Failed Upload");
            model.addAttribute("messageString","Unknown CSV upload error, please try again!");
        }

        return "message";
    }
}
