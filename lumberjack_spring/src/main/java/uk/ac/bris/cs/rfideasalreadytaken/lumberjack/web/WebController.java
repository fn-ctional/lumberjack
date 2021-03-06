package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.AuthenticationBackend;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.DatabaseUtility;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.*;

import java.sql.SQLException;
import java.util.*;

@Controller
public class WebController extends WebMvcConfigurerAdapter {

    @Autowired
    private WebBackend webBackend;

    @Autowired
    private DatabaseUtility databaseUtility;

    @Autowired
    private AuthenticationBackend authenticationBackend;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean isDatabaseInitialised = false;

    /**
     * Adding view controllers to serve the basic pages that don't require complex mappings.
     * @param registry The registry of simple views with no need for model attributes
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/about");
        registry.addViewController("/download");
        registry.addViewController("/login");
        registry.addViewController("/help");
        registry.addViewController("/forgotPassword");
    }

    /**
     * GET request handler for serving the home page.
     * Also has the functionality of checking if the database has been initialised, and if not, will initialise it.
     * @return home.html.
     * @throws SQLException Throws in case of database error, picked up by the SQLException handler.
     */
    @GetMapping("/")
    public String home() throws SQLException {
        if (!this.isDatabaseInitialised) {
            databaseUtility.checkDatabase();
            this.isDatabaseInitialised = true;
        }
        return "home";
    }

    /**
     * GET request handler for serving the admin dashboard with the active user's name as a model attribute.
     * @param model The session/page model.
     * @return dashboard.html.
     * @throws SQLException Throws in case of database error, picked up by the SQLException handler.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) throws SQLException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        AdminUser user = authenticationBackend.findByEmail(email);
        String name = user.getName();
        // Get stats for the graphs
        int available = webBackend.getAvailableCount();
        int taken = webBackend.getTakenCount();
        int other = webBackend.getOtherCount();
        List<String>  times    = webBackend.getTimes(6);
        List<Integer> takeouts = webBackend.getRecentTakeouts(6);
        List<Integer> returns = webBackend.getRecentReturns(6);
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
     * @throws SQLException Throws in case of database error, picked up by the SQLException handler.
     */
    @GetMapping("/users")
    public String allUsers(Model model) throws SQLException {
        model.addAttribute("multi", true);
        Boolean found = false;
        List<User> userList = webBackend.getUsers();
        if (!userList.isEmpty()) {
            found = true;
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
     * @throws SQLException Throws in case of database error, picked up by the SQLException handler.
     */
    @GetMapping("/user/{id}")
    public String userSpecified(@PathVariable String id, Model model) throws SQLException {
        List<User> userList = new ArrayList<>();
        List<AssignmentHistory> takeoutList = new ArrayList<>();
        List<Assignment> assignmentList = new ArrayList<>();
        boolean found = false;
        boolean taken = false;
        boolean assignments = false;
        model.addAttribute("searchTerm", id);

        User user = webBackend.getUser(id);

        if (user != null) {
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
     * @throws SQLException Throws in case of database error, picked up by the SQLException handler.
     */
    @GetMapping("/devices")
    public String allDevices(Model model) throws SQLException {
        model.addAttribute("multi", true);
        Boolean found = false;
        List<Device> deviceList = webBackend.getDevices();
        if (!deviceList.isEmpty()) {
            found = true;
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
     * @throws SQLException Throws in case of database error, picked up by the SQLException handler.
     */
    @GetMapping("/device/{id}")
    public String deviceSpecified(@PathVariable String id, Model model) throws SQLException {
        List<Device> deviceList = new ArrayList<>();
        List<AssignmentHistory> takeoutList = new ArrayList<>();
        List<Assignment> assignmentList = new ArrayList<>();
        Boolean found = false;
        boolean taken = false;
        model.addAttribute("isOut", false);
        model.addAttribute("searchTerm", id);
        Device device = webBackend.getDevice(id);
        if (device.getId().equals(id)) {
            deviceList.add(device);
            found = true;
            takeoutList = webBackend.getDeviceAssignmentHistory(id);
            model.addAttribute("isOut", deviceList.get(0).isCurrentlyAssigned());
            taken = true;
            assignmentList = webBackend.getDeviceAssignments(id);
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
            return updateError(model, e);
        }
        return "add";
    }

    private String updateError(Model model, Exception e) {
        e.printStackTrace();
        System.out.println("SQL Error");
        model.addAttribute("messageType", "Error");
        model.addAttribute("messageString", e.getMessage());
        return "message";
    }

    /**
     * POST request handler for adding a single user.
     * @param request The request and form body.
     * @param model The session/page model.
     * @return message.html
     */
    @PostMapping("/add/user")
    public String addUser(@RequestParam Map<String, String> request, Model model) {
        // Set user attributes
        User newUser = new User();
        newUser.setId(UUID.randomUUID().toString());
        newUser.setUsername(request.get("username"));
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

    /**
     * POST request handler for adding a single device.
     * @param request The request and form body.
     * @param model The session/page model.
     * @return message.html
     */
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

    /**
     * POST request handler for adding a single group.
     * @param request The request and form body.
     * @param model The session/page model.
     * @return message.html
     */
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

    /**
     * POST request handler for adding a single rule.
     * @param request The request and form body.
     * @param model The session/page model.
     * @return message.html
     */
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
     * POST request handler for adding an email to PermittedEmails.
     * @param request The request and form body.
     * @param model The session/page model.
     * @return A redirect to the add admin page (updated to include the new email).
     */
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
     * Basic request handler for serving the group page when no group is specified.
     * @param model The session/page model.
     * @return groups.html
     */
    @GetMapping("/group")
    public String group(Model model) {
        model.addAttribute("blank", true);
        return "groups";
    }

    /**
     * GET request handler for returning the page populated with all the groups in the database.
     * @param model The session/page model.
     * @return groups.html
     * @throws SQLException Throws in case of database error, picked up by the SQLException handler.
     */
    @GetMapping("/groups")
    public String allGroups(Model model) throws SQLException {
        model.addAttribute("multi", true);
        Boolean found = false;
        List<UserGroup> userGroupList = webBackend.getUserGroups();
        if (!userGroupList.isEmpty()) {
            found = true;
        }
        model.addAttribute("found", found);
        model.addAttribute(userGroupList);
        return "groups";
    }

    /**
     * GET request handler for serving the groups page populated with a specified (id) group from the database.
     * @param id The target group ID.
     * @param model The session/page model.
     * @return groups.html
     * @throws SQLException Throws in case of database error, picked up by the SQLException handler.
     */
    @GetMapping("/group/{id}")
    public String groupSpecified(@PathVariable String id, Model model) throws SQLException {
        // Make sure group exists
        Boolean found = false;
        model.addAttribute("searchTerm", id);
        UserGroup userGroup = webBackend.getUserGroup(id);
        if (userGroup.getId().equals(id)) {
            found = true;
        }
        List<User> userList = new ArrayList<>();
        List<Rule> ruleList = new ArrayList<>();
        boolean gotUsers = false;
        boolean gotRules = false;
        if (found) {
            // Get group users
            userList = webBackend.getGroupUsers(id);
            if (!userList.isEmpty()) {
                gotUsers = true;
            }
            // Get group rules
            ruleList = webBackend.getGroupRules(id);
            if (!ruleList.isEmpty()) {
                gotRules = true;
            }
        }
        model.addAttribute("found", found);
        model.addAttribute("gotUsers", gotUsers);
        model.addAttribute("gotRules", gotRules);
        model.addAttribute("userList", userList);
        model.addAttribute("ruleList", ruleList);
        return "groups";
    }

    /**
     * Basic request handler for serving the rules page when no specific rule is specified.
     * @param model The session/page model.
     * @return rules.html
     */
    @GetMapping("/rule")
    public String rule(Model model) {
        model.addAttribute("blank", true);
        return "rules";
    }

    /**
     * GET request handler for returning the page populated with all the rules in the database.
     * @param model The session/page model.
     * @return rules.html
     * @throws SQLException Throws in case of database error, picked up by the SQLException handler.
     */
    @GetMapping("/rules")
    public String allRules(Model model) throws SQLException {
        model.addAttribute("multi", true);
        Boolean found = false;
        List<Rule> ruleList;
        ruleList = webBackend.getRules();
        if (!ruleList.isEmpty()) {
            found = true;
        }
        model.addAttribute("found", found);
        model.addAttribute(ruleList);
        return "rules";
    }

    /**
     * GET request handler for serving the rules page populated with a specified (id) rule from the database.
     * @param id The target rule ID.
     * @param model The session/page model.
     * @return rules.html
     * @throws SQLException Throws in case of database error, picked up by the SQLException handler.
     */
    @GetMapping("/rule/{id}")
    public String ruleSpecified(@PathVariable String id, Model model) throws SQLException {
        // Make sure rule exists
        Boolean found = false;
        model.addAttribute("searchTerm", id);
        Rule rule = webBackend.getRule(id);
        if (rule.getId().equals(id)) {
            found = true;
            model.addAttribute("thisRule", rule);
        }
        List<UserGroup> groupList = new ArrayList<>();
        List<Device> deviceList = new ArrayList<>();
        boolean gotGroups = false;
        boolean gotDevices = false;
        if (found) {
            // Get groups with this rule
            groupList = webBackend.getUserGroupsByRule(id);
            if (!groupList.isEmpty()) {
                gotGroups = true;
            }
            // Get devices with this rule
            deviceList = webBackend.getDevicesByRule(id);
            if (!deviceList.isEmpty()) {
                gotDevices = true;
            }
        }
        model.addAttribute("found", found);
        model.addAttribute("gotGroups", gotGroups);
        model.addAttribute("gotDevices", gotDevices);
        model.addAttribute("groupList", groupList);
        model.addAttribute("deviceList", deviceList);
        return "rules";
    }

    /**
     * POST request handler for deleting a user.
     * @param request The request and form body.
     * @param model The session/page model.
     * @return message.html
     */
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

    /**
     * POST request handler for deleting a device.
     * @param request The request and form body.
     * @param model The session/page model.
     * @return message.html
     */
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

    /**
     * POST request handler for deleting a group.
     * @param request The request and form body.
     * @param model The session/page model.
     * @return message.html
     */
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

    /**
     * POST request handler for deleting a rule.
     * @param request The request and form body.
     * @param model The session/page model.
     * @return message.html
     */
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

    /**
     * Basic request handler for the delete page when no entity is specified.
     * @param model The session/page model.
     * @return message.html
     */
    @GetMapping("/delete")
    public String delete(Model model) {
        model.addAttribute("messageType", "Delete");
        model.addAttribute("messageString", "To delete something, go to it's page, and " +
                "press the delete button (and selecting any relevant deletion options).");
        return "message";
    }

    /**
     * Basic request handler for the update page when no entity is specified.
     * @param model The session/page model.
     * @return update.html
     */
    @RequestMapping("/update")
    public String update(Model model) {
        model.addAttribute("blank", true);
        return "update";
    }

    /**
     * GET request handler for editing a type of entity e.g. user or device.
     * @param model The session/page model.
     * @param type The target type to edit.
     * @return message.html
     */
    @GetMapping("/update/{type}")
    public String updateType(Model model, @PathVariable String type) {
        model.addAttribute("messageType", "Update");
        model.addAttribute("messageString", "To update a " + type + ", go to it's page and " +
                "click the update button.");
        return "message";
    }

    /**
     * GET request handler for editing a type of entity e.g. user or device, specified by its id.
     * @param type The target type to edit.
     * @param id The target entity ID.
     * @param model The session/page model.
     * @return update.html
     */
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
            return updateError(model, e);
        }
        return "update";
    }

    /**
     * Depending on the type, add different attributes to the page.
     * @param type The target type.
     * @param model The session/page model.
     * @param id The target entity ID.
     * @throws SQLException Throws in case of database error, picked up by the SQLException handler.
     */
    private void switchOnType(@PathVariable String type, Model model, String id) throws SQLException {
        List<Rule> rules;
        switch (type) {
            case "user":
                List<UserGroup> groups;
                if (id != null) {
                    model.addAttribute("user", webBackend.getUser(id));
                }
                groups = webBackend.getUserGroups();
                model.addAttribute("groups", groups);
                break;
            case "group":
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

    /**
     * POST request handler for editing a user.
     * @param request The request and form body.
     * @param model The session/page model.
     * @return message.html
     */
    @PostMapping("/update/user")
    public String updateUser(@RequestParam Map<String, String> request, Model model) {
        // Set user attributes
        User user = new User();
        user.setId(request.get("id"));
        user.setUsername(request.get("username"));
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

    /**
     * POST request handler for editing a device.
     * @param request The request and form body.
     * @param model The session/page model.
     * @return message.html
     */
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

    /**
     * POST request handler for editing a group.
     * @param request The request and form body.
     * @param model The session/page model.
     * @return message.html
     */
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

    /**
     * POST request handler for editing a rule.
     * @param request The request and form body.
     * @param model The session/page model.
     * @return message.html
     */
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

    /**
     * GET request handler for viewing the profile information of the logged in user.
     * @param model The session/page model.
     * @return profile.html
     */
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

    /**
     * POST request handler for updating the profile information of the logged in user.
     * @param request The request and form body.
     * @param model The session/page model.
     * @return A redirect to the profile page (with updated info).
     */
    @PostMapping("/profile")
    public String updateProfile(@RequestParam Map<String, String> request, Model model) {
        String id = request.get("id");
        try {
            AdminUser adminUser = webBackend.getAdminUser(id);
            adminUser.setEmail(request.get("email"));
            adminUser.setName(request.get("username"));
            webBackend.updateAdmin(id, adminUser);
            if (!request.get("password").equals("")) {
                String encodedPassword = passwordEncoder.encode(request.get("password"));
                webBackend.updateAdminPassword(adminUser.getEmail(), encodedPassword);
                return "redirect:logout";
            }
        } catch (Exception e) {
            model.addAttribute("messageType", "Profile Updating Failed");
            model.addAttribute("messageString", e.getMessage());
            return "message";
        }
        return "redirect:profile";
    }

    /**
     * GET request handler for viewing the page of late or missing devices.
     * @param model The session/page model.
     * @return incidents.html
     */
    @GetMapping("/incidents")
    public String incidents(Model model) {
        List<Device> lateDevices;
        List<Device> oldLateDevices;
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

    /**
     * POST request handler for returning the device specified by id.
     * @param model The session/page model.
     * @param id The target device ID.
     * @return message.html
     */
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

    /**
     * POST request handler for deleting an email address from PermittedEmails.
     * @param request The request and form body.
     * @param model The session/page model.
     * @return A redirect to the add admin page (updated to not include the deleted email).
     */
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

    /**
     * An exception handler for SQLExceptions.
     * @param e The thrown SQLException.
     * @return The Model and View which will show the message page.
     */
    @ExceptionHandler(SQLException.class)
    public ModelAndView handleUnknownSQLException(Exception e) {
        e.printStackTrace();
        ModelAndView mav = new ModelAndView();
        mav.setViewName("message");
        mav.addObject("messageType", "Server Error");
        mav.addObject("messageString","Unknown server error, please try again!");
        return mav;
    }

}
