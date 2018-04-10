package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
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
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.data.DevicesCSVDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.data.UsersCSVDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static java.util.Calendar.*;
import static org.springframework.security.config.Elements.HEADERS;

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
        // Get stats for the graphs
        List<Integer> takeouts = new ArrayList<>();
        List<Integer> returns  = new ArrayList<>();
        int available = 0, taken = 0, other = 0;
        try {
            available = webBackend.getAvailableCount();
            taken     = webBackend.getTakenCount();
            other     = webBackend.getOtherCount();
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
        // Add attributes
        model.addAttribute(takeouts);
        model.addAttribute(returns);
        model.addAttribute("available", available);
        model.addAttribute("taken", taken);
        model.addAttribute("other", other);
        model.addAttribute("name", name);
        return "dashboard";
    }

    @RequestMapping("/test")
    public String test(Model model) {
        model.addAttribute("messageType", "Test");
        List<Integer> takeouts = new ArrayList<>();
        try {
            takeouts = webBackend.getRecentTakeouts(9);
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
        model.addAttribute("messageString", takeouts);
        return "message";
    }

    /**
     * Basic request handler for serving the users page when no specific user is specified.
     * @param model
     * @return
     */
    @GetMapping("/user")
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
     * CSV file upload POST mapping. CSV must include headers that match user object.
     * Any missing columns will be filled in with default values.
     * @param csv CSV file including headers that match the user object.
     *            Headers are non-capitalised and separated by spaces.
     * @param model
     * @return The message page detailing the success or error of the upload.
     */
    @PostMapping(value = "/add/user/CSV", consumes = "text/csv", produces = "text/plain")
    public String addUsersCSV(@RequestParam MultipartFile csv, Model model) throws FileUploadException, SQLException {
        List<User> newUsers = webBackend.parseUserCSV(csv);

        webBackend.insertUsers(newUsers);
        model.addAttribute("messageType", "Successful Upload");
        model.addAttribute("messageString", "New users successfully added!");

        return "message";
    }

    /**
     * CSV file upload POST mapping. CSV must include headers that match device object.
     * Any missing columns will be filled in with default values.
     * @param csv CSV file including headers that match the user object.
     *            Headers are non-capitalised and separated by spaces.
     * @param model
     * @return The message page detailing the success or error of the upload.
     */
    @PostMapping(value = "/add/device/CSV", consumes = "text/csv", produces = "text/plain")
    public String addDevicesCSV(@RequestParam MultipartFile csv, Model model) throws FileUploadException, SQLException {
            List<Device> newDevices = webBackend.parseDeviceCSV(csv);

            webBackend.insertDevices(newDevices);
            model.addAttribute("messageType", "Successful Upload");
            model.addAttribute("messageString", "New devices successfully added!");

        return "message";
    }

    @GetMapping(value = "/CSV/users", produces = "text/csv")
    public void getUsersCSV(HttpServletResponse response) throws FileDownloadException {
        try {
            String csv = webBackend.getUsersCSV();
            response.getWriter().append(csv);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            throw new FileDownloadException();
        }
    }

    @GetMapping(value = "/CSV/devices", produces = "text/csv")
    public void getDevicesCSV(HttpServletResponse response) throws FileDownloadException {
        try {
            String csv = webBackend.getDevicesCSV();
            response.getWriter().append(csv);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            throw new FileDownloadException();
        }
    }

    //TODO: Not sure if this works
    @ExceptionHandler(FileUploadException.class)
    public ModelAndView handleUploadError() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("message");
        mav.addObject("messageType", "Failed Upload");
        mav.addObject("messageString","Unknown CSV upload error, please try again!");
        return mav;
    }

    //TODO: Not sure if this works
    @ExceptionHandler(FileUploadException.class)
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

}
