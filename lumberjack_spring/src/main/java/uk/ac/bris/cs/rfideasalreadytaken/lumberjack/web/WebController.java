package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.security.Permission;
import java.sql.SQLException;
import java.time.Period;
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
        model.addAttribute("messageString", "test");
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
        List<User> userList = new ArrayList<>();
        List<AssignmentHistory> takeoutList = new ArrayList<>();
        List<Assignment> assignmentList = new ArrayList<>();
        boolean found = false;
        boolean taken = false;
        boolean assignments = false;
        model.addAttribute("searchTerm", id);
        try {
            User user = webBackend.getUser(id);
            if (user.getId().equals(id)) {
                userList.add(user);
                found = true;
                takeoutList = webBackend.getUserAssignmentHistory(id);
                assignmentList = webBackend.getUserAssignments(id);
                if (!takeoutList.isEmpty()) {
                    taken = true;
                }
                if (!assignmentList.isEmpty()) {
                    assignments = true;
                }
            }
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
        model.addAttribute("taken", taken);
        model.addAttribute("found", found);
        model.addAttribute("assignments", assignments);
        model.addAttribute("userList", userList);
        model.addAttribute("takeoutList", takeoutList);
        model.addAttribute("assignmentList", assignmentList);
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
        List<AssignmentHistory> takeoutList = new ArrayList<>();
        List<Assignment> assignmentList = new ArrayList<>();
        Boolean found = false;
        boolean taken = false;
        model.addAttribute("isOut", false);
        model.addAttribute("searchTerm", id);
        try {
            Device device = webBackend.getDevice(id);
            if (device.getId().equals(id)) {
                deviceList.add(device);
                found = true;
                takeoutList = webBackend.getDeviceAssignmentHistory(id);
                model.addAttribute("isOut", deviceList.get(0).isCurrentlyAssigned());
                taken = true;
                assignmentList = webBackend.getDeviceAssignments(id);
            }
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
        model.addAttribute("found", found);
        model.addAttribute("taken", taken);
        model.addAttribute("takeoutList", takeoutList);
        model.addAttribute("deviceList", deviceList);
        model.addAttribute("assignmentList", assignmentList);
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
        model.addAttribute("permittedEmails", new ArrayList<String>());
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
        try {
            switchOnType(type, model, null);
        } catch (Exception e) {
            System.out.println("SQL Error");
            model.addAttribute("messageType", "Error");
            model.addAttribute("messageString", e.getMessage());
            return "message";
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

    @PostMapping("/add/admin")
    public String addPermittedEmail(@RequestParam Map<String, String> request, Model model) {
        try {
            webBackend.insertPermittedEmail(request.get("newEmail"));
        } catch (Exception e) {
            model.addAttribute("messageType", "Rule Adding Failed");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }
        return "redirect:../add/admin";
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
        try {
            // User has outstanding devices, can't be deleted
            if (webBackend.userHasOutstandingDevices(id)) {
                model.addAttribute("messageType", "Deletion Failed");
                model.addAttribute("messageString", "You cannot delete a user with outstanding " +
                        "device assignments. You must first return the device. This can be done by finding the outstanding " +
                        "device on the user's page and, returning it on the device's page, or manually.");
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

    @PostMapping("/delete/device")
    public String deleteDevice(@RequestParam Map<String, String> request, Model model) {
        String id = request.get("deviceID");
        try {
            if (webBackend.deviceIsOut(id)) {
                model.addAttribute("messageType", "Deletion Failed");
                model.addAttribute("messageString", "You cannot delete a device that is " +
                        "currently out. You must first return the device. This can be done on the device's page, or " +
                        "by getting the user who has the device to return it manually.");
                return "message";
            }
            webBackend.deleteDevice(id);
            // Delete assignment history if the box was ticked
            if (request.containsKey("deleteHistory")) {
                webBackend.deleteAssignmentHistoryByDevice(id);
                System.out.println("delete");
            }
        } catch (Exception e) {
            model.addAttribute("messageType", "Deletion Failed");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }

        // Succeeded
        model.addAttribute("messageType", "Successful Deletion");
        model.addAttribute("messageString", "Device successfully deleted!");
        return "message";
    }

    @PostMapping("/delete/group")
    public String deleteGroup(@RequestParam Map<String, String> request, Model model) {
        String id = request.get("groupID");
        try {
            webBackend.removeGroupFromUsers(id);
            webBackend.deletePermissionsByGroup(id);
            webBackend.deleteUserGroup(id);
        } catch (Exception e) {
            model.addAttribute("messageType", "Deletion Failed");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }

        // Succeeded
        model.addAttribute("messageType", "Successful Deletion");
        model.addAttribute("messageString", "Group successfully deleted!");
        return "message";
    }

    @PostMapping("/delete/rule")
    public String deleteRule(@RequestParam Map<String, String> request, Model model) {
        String id = request.get("ruleID");
        try {
            webBackend.removeRuleFromDevices(id);
            webBackend.deletePermissionsByRule(id);
            webBackend.deleteRule(id);
        } catch (Exception e) {
            model.addAttribute("messageType", "Deletion Failed");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }

        // Succeeded
        model.addAttribute("messageType", "Successful Deletion");
        model.addAttribute("messageString", "Rule successfully deleted!");
        return "message";
    }

    @GetMapping("/delete")
    public String delete(Model model) {
        model.addAttribute("messageType", "Delete");
        model.addAttribute("messageString", "To delete something, go to it's page, and " +
                "press the delete button (and selecting any relevant deletion options).");
        return "message";
    }

    @RequestMapping("/update")
    public String update(Model model) {
        model.addAttribute("blank", true);
        return "update";
    }

    @GetMapping("/update/{type}")
    public String updateType(Model model, @PathVariable String type) {
        model.addAttribute("messageType", "Update");
        model.addAttribute("messageString", "To update a " + type + ", go to it's page and " +
                "click the update button.");
        return "message";
    }

    @GetMapping("/update/{type}/{id}")
    public String updateTypeID(@PathVariable String type, @PathVariable String id, Model model) {
        model.addAttribute("type", type);
        model.addAttribute("id", id);
        model.addAttribute("groups", new ArrayList<UserGroup>());
        model.addAttribute("groupRules", new HashMap<String, String>());
        model.addAttribute("rules", new ArrayList<Rule>());
        model.addAttribute("user", new User());
        model.addAttribute("device", new Device());
        model.addAttribute("group", new UserGroup());
        model.addAttribute("rule", new Rule());
        try {
            switchOnType(type, model, id);
        } catch (Exception e) {
            System.out.println("SQL Error");
            model.addAttribute("messageType", "Error");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }
        return "update";
    }

    private void switchOnType(@PathVariable String type, Model model, String id) throws SQLException {
        List<Rule> rules;
        switch (type) {
            case "user":
                List<UserGroup> groups = new ArrayList<>();
                if (id != null) {
                    model.addAttribute("user", webBackend.getUser(id));
                }
                groups = webBackend.getUserGroups();
                model.addAttribute("groups", groups);
                break;
            case "group":
                rules = new ArrayList<>();
                rules = webBackend.getRules();
                model.addAttribute("rules", rules);
                model.addAttribute("group", webBackend.getUserGroup(id));
                HashMap<String, String> groupRules = new HashMap<>();
                for (Rule rule : webBackend.getGroupRules(id)) {
                    groupRules.put(rule.getId(), "");
                }
                model.addAttribute("groupRules", groupRules);
                break;
            case "device":
                if (id != null) {
                    model.addAttribute("device", webBackend.getDevice(id));
                }
                rules = new ArrayList<>();
                rules = webBackend.getRules();
                model.addAttribute("rules", rules);
                break;
            case "rule" :
                if (id != null) {
                    model.addAttribute("rule", webBackend.getRule(id));
                }
            case "admin" :
                model.addAttribute("permittedEmails", webBackend.getPermittedEmails());
            default:
                break;
        }
    }

    @PostMapping("/update/user")
    public String updateUser(@RequestParam Map<String, String> request, Model model) {
        // Set user attributes
        User user = new User();
        user.setId(request.get("id"));
        user.setScanValue(request.get("scanValue"));
        user.setDeviceLimit(new Integer(request.get("deviceLimit")));
        user.setDevicesRemoved(new Integer(request.get("removed")));
        user.setCanRemove(request.containsKey("canRemove"));
        user.setGroupId(request.get("groupID"));
        // Add user to the database
        try {
            webBackend.editUser(user.getId(), user);
        } catch (Exception e) {
            model.addAttribute("messageType", "User Updating Failed");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }
        model.addAttribute("messageType", "User Updated");
        model.addAttribute("messageString", "The user has been updated!");
        return "message";
    }

    @PostMapping("/update/device")
    public String updateDevice(@RequestParam Map<String, String> request, Model model) {
        // Set device attributes
        Device device = new Device();
        device.setId(request.get("id"));
        device.setScanValue(request.get("scanValue"));
        device.setType(request.get("deviceType"));
        device.setAvailable(request.containsKey("available"));
        device.setCurrentlyAssigned(request.get("assigned").equals("true"));
        device.setRuleID(request.get("ruleID"));
        // Add device to the database
        try {
            webBackend.editDevice(device.getId(), device);
        } catch (Exception e) {
            model.addAttribute("messageType", "Device Updating Failed");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }
        model.addAttribute("messageType", "Device Updated");
        model.addAttribute("messageString", "The device has been updated!");
        return "message";
    }

    @PostMapping("/update/group")
    public String updateGroup(@RequestParam Map<String, String> request, Model model) {
        String id = request.get("id");
        // Get selected rules
        List<GroupPermission> permissions = new ArrayList<>();
        List<String> keys = new ArrayList<>(request.keySet());
        for (int i = 1; i < request.size(); i++) {
            GroupPermission permission = new GroupPermission();
            permission.setId(UUID.randomUUID().toString());
            permission.setUserGroupID(id);
            permission.setRuleID(request.get(keys.get(i)));
            permissions.add(permission);
        }
        // Add group and permissions(s) to the database
        try {
            // Get deselected rules
            List<Rule> allRules = webBackend.getRules();
            List<Rule> deletedRules = new ArrayList<>();
            for (Rule r : allRules) {
                if (!request.containsKey(r.getId())) {
                    deletedRules.add(r);
                }
            }
            // Delete removed permissions
            List<GroupPermission> deletedPermissions = new ArrayList<>();
            for (Rule r : deletedRules) {
                deletedPermissions.add(new GroupPermission(r.getId(), id));
            }
            webBackend.deletePermissions(deletedPermissions);
            // Insert all selected permissions
            for (GroupPermission permission : permissions) {
                // Only insert permission if it doesn't exist
                if (!webBackend.groupHasRule(permission.getUserGroupID(), permission.getRuleID())) {
                    webBackend.insertGroupPermission(permission);
                }
            }

        } catch (Exception e) {
            model.addAttribute("messageType", "Group Updating Failed");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }
        model.addAttribute("messageType", "Group Updated");
        model.addAttribute("messageString", "The group has been updated!");
        return "message";
    }

    @PostMapping("/update/rule")
    public String updateRule(@RequestParam Map<String, String> request, Model model) {
        // Set rule attributes
        Rule rule = new Rule();
        rule.setId(request.get("id"));
        rule.setMaximumRemovalTime(new Integer(request.get("maximumTime")));
        // Add rule to the database
        try {
            webBackend.updateRule(rule);
        } catch (Exception e) {
            model.addAttribute("messageType", "Rule Updating Failed");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }
        model.addAttribute("messageType", "Rule Updated");
        model.addAttribute("messageString", "The rule has been updated!");
        return "message";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        AdminUser user = authenticationBackend.findByEmail(email);
        String name = user.getName();
        // Add attributes
        model.addAttribute("email", email);
        model.addAttribute("name", name);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam Map<String, String> request, Model model) {
        String id = request.get("id");
        try {
            AdminUser adminUser = webBackend.getAdminUser(id);
            adminUser.setEmail(request.get("email"));
            adminUser.setName(request.get("username"));
            webBackend.updateAdmin(id, adminUser);
        } catch (Exception e) {
            model.addAttribute("messageType", "Profile Updating Failed");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }
        return "redirect:profile";
    }

    @GetMapping("/incidents")
    public String incidents(Model model) {
        List<Device> lateDevices = new ArrayList<>();
        List<Device> oldLateDevices = new ArrayList<>();
        try {
            lateDevices = webBackend.getCurrentlyLateDevices();
            oldLateDevices = webBackend.getPreviouslyLateDevices();
        } catch (Exception e) {
            model.addAttribute("messageType", "Incidents Error");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }
        model.addAttribute("blankLate", lateDevices.isEmpty());
        model.addAttribute("blankOldLate", oldLateDevices.isEmpty());
        model.addAttribute("lateDevices", lateDevices);
        model.addAttribute("oldLateDevices", oldLateDevices);
        return "incidents";
    }

    @PostMapping("/return/device/{id}")
    public String returnDevice(Model model, @PathVariable("id") String id) {
        try {
            webBackend.returnDevice(id);
        } catch (Exception e) {
            model.addAttribute("messageType", "Return Error");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }
        model.addAttribute("messageType", "Return Successful");
        model.addAttribute("messageString", "The device was successfully returned!");
        return "message";
    }

    @PostMapping("/delete/admin")
    public String deletePermittedEmail(@RequestParam Map<String, String> request, Model model) {
        List<String> toDelete = new ArrayList<>(request.keySet());
        try {
            for (String email : toDelete) {
                webBackend.deletePermittedEmail(email);
            }
        } catch (Exception e) {
            model.addAttribute("messageType", "Deletion Error");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }
        return "redirect:../add/admin";
    }

}
