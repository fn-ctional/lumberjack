package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.AuthenticationBackend;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.FileDownloadException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.FileUploadException;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

@Controller
public class WebController extends WebMvcConfigurerAdapter {

    @Autowired
    private WebBackend webBackend;

    @Autowired
    private AuthenticationBackend authenticationBackend;

    /**
     * Adding view controllers to serve the basic pages that don't require complex mappings.
     * @param registry The registry of simple views with no need for model attributes
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
     * @param model The session/page model.
     * @return dashboard.html.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        AdminUser user = authenticationBackend.findByEmail(email);
        String name = user.getName();
        // Get stats for the graphs
        List<Integer> takeouts = new ArrayList<>();
        List<Integer> returns  = new ArrayList<>();
        List<String>  times    = webBackend.getTimes(6);
        int available = 0, taken = 0, other = 0;
        try {
            available = webBackend.getAvailableCount();
            taken     = webBackend.getTakenCount();
            other     = webBackend.getOtherCount();
            takeouts  = webBackend.getRecentTakeouts(6);
            returns   = webBackend.getRecentReturns(6);
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
//        // Spoof Values
//        takeouts = Arrays.asList(4, 8, 12, 2, 6, 0);
//        returns  = Arrays.asList(0, 2, 4, 10, 2, 2);
        // Add list model attributes separately
        for (int i = 0; i < times.size(); i++) {
            String timeI    = "time"   + Integer.toString(i);
            String takeoutI = "take"   + Integer.toString(i);
            String returnI  = "return" + Integer.toString(i);
            model.addAttribute(timeI, times.get(i));
            model.addAttribute(takeoutI, takeouts.get(i));
            model.addAttribute(returnI, returns.get(i));
        }
        // Add attributes
        model.addAttribute("available", available);
        model.addAttribute("taken", taken);
        model.addAttribute("other", other);
        model.addAttribute("name", name);
        return "dashboard";
    }

    @RequestMapping("/test")
    public String test(Model model) {
        model.addAttribute("messageType", "Test");
        List<String> takeouts = new ArrayList<>();
        try {
            takeouts = webBackend.getTimes(10);
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
        model.addAttribute("messageString", takeouts);
        return "message";
    }

    /**
     * Basic request handler for serving the users page when no specific user is specified.
     * @param model The session/page model.
     * @return users.html.
     */
    @GetMapping("/user")
    public String user(Model model) {
        model.addAttribute("blank", true);
        return "users";
    }

    /**
     * GET request handler for returning the page populated with all the users in the database.
     * @param model The session/page model.
     * @return users.html.
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
     * @param id The target user ID.
     * @param model The session/page model.
     * @return users.html.
     */
    @GetMapping("/user/{id}")
    public String userSpecified(@PathVariable String id, Model model) {
        // Get the user details
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
        // Get the assignment history
        List<AssignmentHistory> takeoutList = new ArrayList<>();
        boolean taken = false;
        if (found){
            try {
                takeoutList = webBackend.getUserAssignmentHistory(id);
                if (!takeoutList.isEmpty()) {
                    taken = true;
                }
            } catch (Exception e) {
                System.out.println("SQL Error");
            }
        }
        model.addAttribute("taken", taken);
        model.addAttribute("found", found);
        model.addAttribute("userList", userList);
        model.addAttribute("takeoutList", takeoutList);
        return "users";
    }

    /**
     * Basic request handler for serving the devices page when no specific device is specified.
     * @param model The session/page model.
     * @return devices.html.
     */
    @GetMapping("/device")
    public String device(Model model) {
        model.addAttribute("blank", true);
        return "devices";
    }

    /**
     * GET request handler for returning the page populated with all the devices in the database.
     * @param model The session/page model.
     * @return devices.html.
     */
    @GetMapping("/devices")
    public String allDevices(Model model) {
        model.addAttribute("multi", true);
        Boolean found = false;
        List<Device> deviceList = new ArrayList<>();
        try {
            deviceList = webBackend.getDevices();
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
     * @param id The target device ID.
     * @param model The session/page model.
     * @return devices.html.
     */
    @GetMapping("/device/{id}")
    public String deviceSpecified(@PathVariable String id, Model model) {
        List<Device> deviceList = new ArrayList<>();
        Boolean found = false;
        model.addAttribute("searchTerm", id);
        try {
            Device device = webBackend.getDevice(id);
            if (device.getId().equals(id)) {
                deviceList.add(device);
                found = true;
            }
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
        // Get the assignment history
        List<AssignmentHistory> takeoutList = new ArrayList<>();
        boolean taken = false;
        if (found){
            try {
                takeoutList = webBackend.getDeviceAssignmentHistory(id);
                if (!takeoutList.isEmpty()) {
                    taken = true;
                }
            } catch (Exception e) {
                System.out.println("SQL Error");
            }
        }
        model.addAttribute("found", found);
        model.addAttribute("taken", taken);
        model.addAttribute("takeoutList", takeoutList);
        model.addAttribute("deviceList", deviceList);
        return "devices";
    }

    /**
     * Basic request handler for serving the search page when no search type is specified.
     * @param model The session/page model.
     * @return search.html.
     */
    @RequestMapping("/search")
    public String search(Model model) {
        model.addAttribute("blank", true);
        return "search";
    }

    /**
     * GET request handler for searching a type of entity e.g. user or device.
     * @param type The target search type.
     * @param model The session/page model.
     * @return search.html.
     */
    @GetMapping("/search/{type}")
    public String searchType(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        return "search";
    }

    /**
     * Basic request handler for serving the add page when no add type is specified.
     * @param model The session/page model.
     * @return add.html.
     */
    @RequestMapping("/add")
    public String add(Model model) {
        model.addAttribute("blank", true);
        return "add";
    }

    /**
     * GET request handler for adding a type of entity e.g. user or device.
     * @param type The target type to add.
     * @param model The session/page model.
     * @return add.html.
     */
    @GetMapping("/add/{type}")
    public String addType(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        model.addAttribute("groups", new ArrayList<UserGroup>());
        List<Rule> rules;
        switch (type) {
            case "user":
                List<UserGroup> groups = new ArrayList<>();
                try {
                    groups = webBackend.getUserGroups();
                } catch (Exception e) {
                    System.out.println("SQL Exception");
                }
                model.addAttribute("groups", groups);
                break;
            case "group":
                rules = new ArrayList<>();
                try {
                    rules = webBackend.getRules();
                } catch (Exception e) {
                    System.out.println("SQL Exception");
                }
                model.addAttribute("rules", rules);
                break;
            case "device":
                rules = new ArrayList<>();
                try {
                    rules = webBackend.getRules();
                } catch (Exception e) {
                    System.out.println("SQL Exception");
                }
                model.addAttribute("rules", rules);
                break;
            default:
                break;
        }
        return "add";
    }

    @PostMapping("/add/user")
    public String addUser(@RequestParam Map<String, String> request, Model model) {
        // Set user attributes
        User newUser = new User();
        newUser.setId(UUID.randomUUID().toString());
        newUser.setScanValue(request.get("scanValue"));
        newUser.setDeviceLimit(new Integer(request.get("deviceLimit")));
        newUser.setDevicesRemoved(0);
        newUser.setCanRemove(request.containsKey("canRemove"));
        newUser.setGroupId(request.get("groupID"));
        // Add user to the database
        try {
            webBackend.insertUser(newUser);
        } catch (Exception e) {
            System.out.println("SQL Exception");
            model.addAttribute("messageType", "User Adding Failed");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }
        model.addAttribute("messageType", "User Added");
        model.addAttribute("messageString", "The user has been added!");
        return "message";
    }

    @PostMapping("/add/device")
    public String addDevice(@RequestParam Map<String, String> request, Model model) {
        // Set device attributes
        Device newDevice = new Device();
        newDevice.setId(UUID.randomUUID().toString());
        newDevice.setScanValue(request.get("scanValue"));
        newDevice.setType(request.get("deviceType"));
        newDevice.setAvailable(request.containsKey("available"));
        newDevice.setCurrentlyAssigned(false);
        newDevice.setRuleID(request.get("ruleID"));
        // Add device to the database
        try {
            webBackend.insertDevice(newDevice);
        } catch (Exception e) {
            System.out.println("SQL Exception");
            model.addAttribute("messageType", "Device Adding Failed");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }
        model.addAttribute("messageType", "Device Added");
        model.addAttribute("messageString", "The device has been added!");
        return "message";
    }

    @PostMapping("/add/group")
    public String addGroup(@RequestParam Map<String, String> request, Model model) {
        // Set group and permission(s) attributes
        UserGroup newGroup = new UserGroup();
        newGroup.setId(request.get("groupName"));
        // Get selected rules
        List<GroupPermission> permissions = new ArrayList<>();
        List<String> keys = new ArrayList<>(request.keySet());
        for (int i = 1; i < request.size(); i++) {
            GroupPermission permission = new GroupPermission();
            permission.setId(UUID.randomUUID().toString());
            permission.setUserGroupID(request.get("groupName"));
            permission.setRuleID(request.get(keys.get(i)));
            permissions.add(permission);
        }
        // Add group and permissions(s) to the database
        try {
            webBackend.insertUserGroup(newGroup);
            for (GroupPermission permission : permissions) {
                webBackend.insertGroupPermission(permission);
            }
        } catch (Exception e) {
            System.out.println("SQL Exception");
            model.addAttribute("messageType", "Group Adding Failed");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }
        model.addAttribute("messageType", "Group Added");
        model.addAttribute("messageString", "The group has been added!");
        return "message";
    }

    @PostMapping("/add/rule")
    public String addRule(@RequestParam Map<String, String> request, Model model) {
        // Set rule attributes
        Rule newRule = new Rule();
        newRule.setId(request.get("ruleName"));
        newRule.setMaximumRemovalTime(new Integer(request.get("maximumTime")));
        // Add rule to the database
        try {
            webBackend.insertRule(newRule);
        } catch (Exception e) {
            System.out.println("SQL Exception");
            model.addAttribute("messageType", "Rule Adding Failed");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }
        model.addAttribute("messageType", "Rule Added");
        model.addAttribute("messageString", "The rule has been added!");
        return "message";
    }

    /**
     * CSV file upload POST mapping. CSV must include headers that match user object.
     * Any missing columns will be filled in with default values.
     * @param file CSV file including headers that match the user object.
     *            Headers are non-capitalised and separated by spaces.
     * @param model The session/page model.
     * @return The message page detailing the success or error of the upload.
     */
    @PostMapping(value = "/add/user/CSV")
    public String addUsersCSV(@RequestParam("file") MultipartFile file, Model model) throws FileUploadException, SQLException {
        List<User> newUsers = webBackend.parseUserCSV(file);

        webBackend.insertUsers(newUsers);
        model.addAttribute("messageType", "Successful Upload");
        model.addAttribute("messageString", "New users successfully added!");

        return "message";
    }

    /**
     * CSV file upload POST mapping. CSV must include headers that match device object.
     * Any missing columns will be filled in with default values.
     * @param file CSV file including headers that match the user object.
     *            Headers are non-capitalised and separated by spaces.
     * @param model The session/page model.
     * @return The message page detailing the success or error of the upload.
     */
    @PostMapping(value = "/add/device/CSV")
    public String addDevicesCSV(@RequestParam("file") MultipartFile file, Model model) throws FileUploadException, SQLException {
            List<Device> newDevices = webBackend.parseDeviceCSV(file);

            webBackend.insertDevices(newDevices);
            model.addAttribute("messageType", "Successful Upload");
            model.addAttribute("messageString", "New devices successfully added!");

        return "message";
    }

    @GetMapping(value = "/csv/users", produces = "text/csv")
    public void getUsersCSV(HttpServletResponse response) throws FileDownloadException {
        try {
            String csv = webBackend.getUsersCSV();
            response.getWriter().append(csv);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            throw new FileDownloadException();
        }
    }

    @GetMapping(value = "/csv/devices", produces = "text/csv")
    public void getDevicesCSV(HttpServletResponse response) throws FileDownloadException {
        try {
            String csv = webBackend.getDevicesCSV();
            response.getWriter().append(csv);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            throw new FileDownloadException();
        }
    }

    @ExceptionHandler(FileUploadException.class)
    public ModelAndView handleUploadError() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("message");
        mav.addObject("messageType", "Failed Upload");
        mav.addObject("messageString","Unknown CSV upload error, please try again!");
        return mav;
    }

    @ExceptionHandler(FileDownloadException.class)
    public ModelAndView handleDownloadError() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("message");
        mav.addObject("messageType", "Failed Download");
        mav.addObject("messageString","Unknown CSV download error, please try again!");
        return mav;
    }

    @GetMapping("/group")
    public String group(Model model) {
        model.addAttribute("blank", true);
        return "groups";
    }

    @GetMapping("/groups")
    public String allGroups(Model model) {
        model.addAttribute("multi", true);
        Boolean found = false;
        List<UserGroup> userGroupList = new ArrayList<>();
        try {
            userGroupList = webBackend.getUserGroups();
            if (!userGroupList.isEmpty()) {
                found = true;
            }
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
        model.addAttribute("found", found);
        model.addAttribute(userGroupList);
        return "groups";
    }

    @GetMapping("/group/{id}")
    public String groupSpecified(@PathVariable String id, Model model) {
        // Make sure group exists
        Boolean found = false;
        model.addAttribute("searchTerm", id);
        try {
            UserGroup userGroup = webBackend.getUserGroup(id);
            if (userGroup.getId().equals(id)) {
                found = true;
            }
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
        List<User> userList = new ArrayList<>();
        List<Rule> ruleList = new ArrayList<>();
        boolean gotUsers = false;
        boolean gotRules = false;
        if (found) {
            // Get group users
            try {
                userList = webBackend.getGroupUsers(id);
                if (!userList.isEmpty()) {
                    gotUsers = true;
                }
            } catch (Exception e) {
                System.out.println("SQL Error");
            }
            // Get group rules
            try {
                ruleList = webBackend.getGroupRules(id);
                if (!ruleList.isEmpty()) {
                    gotRules = true;
                }
            } catch (Exception e) {
                System.out.println("SQL Error");
            }
        }
        model.addAttribute("found", found);
        model.addAttribute("gotUsers", gotUsers);
        model.addAttribute("gotRules", gotRules);
        model.addAttribute("userList", userList);
        model.addAttribute("ruleList", ruleList);
        return "groups";
    }

    @GetMapping("/rule")
    public String rule(Model model) {
        model.addAttribute("blank", true);
        return "rules";
    }

    @GetMapping("/rules")
    public String allRules(Model model) {
        model.addAttribute("multi", true);
        Boolean found = false;
        List<Rule> ruleList = new ArrayList<>();
        try {
            ruleList = webBackend.getRules();
            if (!ruleList.isEmpty()) {
                found = true;
            }
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
        model.addAttribute("found", found);
        model.addAttribute(ruleList);
        return "rules";
    }

    @GetMapping("/rule/{id}")
    public String ruleSpecified(@PathVariable String id, Model model) {
        // Make sure rule exists
        Boolean found = false;
        model.addAttribute("searchTerm", id);
        try {
            Rule rule = webBackend.getRule(id);
            if (rule.getId().equals(id)) {
                found = true;
                model.addAttribute("thisRule", rule);
            }
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
        List<UserGroup> groupList = new ArrayList<>();
        List<Device> deviceList = new ArrayList<>();
        boolean gotGroups = false;
        boolean gotDevices = false;
        if (found) {
            // Get groups with this rule
            try {
                groupList = webBackend.getUserGroupsByRule(id);
                if (!groupList.isEmpty()) {
                    gotGroups = true;
                }
            } catch (Exception e) {
                System.out.println("SQL Error");
            }
            // Get devices with this rule
            try {
                deviceList = webBackend.getDevicesByRule(id);
                if (!deviceList.isEmpty()) {
                    gotDevices = true;
                }
            } catch (Exception e) {
                System.out.println("SQL Error");
            }
        }
        model.addAttribute("found", found);
        model.addAttribute("gotGroups", gotGroups);
        model.addAttribute("gotDevices", gotDevices);
        model.addAttribute("groupList", groupList);
        model.addAttribute("deviceList", deviceList);
        return "rules";
    }

    @PostMapping("/delete/user")
    public String deleteUser(@RequestParam Map<String, String> request, Model model) {
        String id = request.get("userID");
        System.out.println(request);
        try {
            // If user doesn't exist
            if (!webBackend.userExists(id)) {
                model.addAttribute("messageType", "Deletion Failed");
                model.addAttribute("messageString", "This user doesn't exist!");
                return "message";
            }
            // User has outstanding devices, can't be deleted
            if (webBackend.userHasOutstandingDevices(id)) {
                model.addAttribute("messageType", "Deletion Failed");
                model.addAttribute("messageString", "You cannot delete a user with outstanding " +
                        "device assignments. They must first return the device. This can be done by finding the outstanding " +
                        "device on the user's page and returning it, or manually by setting Currently Assigned on the device's page.");
                return "message";
            }
            webBackend.deleteUser(id);
            // Delete assignment history if the box was ticked
            if (request.containsKey("deleteHistory")) {
                webBackend.deleteAssignmentHistoryByUser(id);
            }
        }
        catch (Exception e) {
            model.addAttribute("messageType", "Deletion Failed");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }

        // Succeeded
        model.addAttribute("messageType", "Successful Deletion");
        model.addAttribute("messageString", "User successfully deleted!");
        return "message";
    }

}
