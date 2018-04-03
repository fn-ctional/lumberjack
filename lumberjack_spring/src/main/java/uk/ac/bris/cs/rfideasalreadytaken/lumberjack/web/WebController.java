package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
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
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.UserGroup;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.FileUploadException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.data.DevicesCSVDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.User;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.data.UsersCSVDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        model.addAttribute("name", name);
        return "dashboard";
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
     * CSV file upload POST mapping. CSV must include headers that match user object.
     * Any missing columns will be filled in with default values.
     * @param csv CSV file including headers that match the user object.
     *            Headers are non-capitalised and separated by spaces.
     * @param model
     * @return The message page detailing the success or error of the upload.
     */
    @PostMapping(value = "/add/user/CSV", consumes = "text/csv", produces = "text/plain")
    public String addUsersCSV(@RequestParam MultipartFile csv, Model model) throws FileUploadException, SQLException {
        List<User> newUsers = parseUserCSV(csv);

        webBackend.insertUsers(newUsers);
        model.addAttribute("messageType", "Successful Upload");
        model.addAttribute("messageString", "New users successfully added!");

        return "CSVUploaded";
    }

    private List<User> parseUserCSV(MultipartFile csv) throws FileUploadException {
        Iterable<CSVRecord> records = multipartFileToRecords(csv);

        List<User> newUsers = new ArrayList<>();
        for (CSVRecord record : records) {
            User newUser = new User();

            newUser.setScanValue(record.get("scan value"));
            try {
                newUser.setDeviceLimit(Integer.parseInt(record.get("device limit")));
            } catch (NumberFormatException e) {
                newUser.setDeviceLimit(0);
            }
            try {
                newUser.setDevicesRemoved(Integer.parseInt(record.get("devices removed")));
            } catch (NumberFormatException e) {
                newUser.setDevicesRemoved(0);
            }
            newUser.setCanRemove(Boolean.parseBoolean(record.get("can remove")));
            newUser.setGroupId(record.get("group id"));
            newUser.setId(UUID.randomUUID().toString());

            newUsers.add(newUser);
        }

        return newUsers;
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
            List<Device> newDevices = parseDeviceCSV(csv);

            webBackend.insertDevices(newDevices);
            model.addAttribute("messageType", "Successful Upload");
            model.addAttribute("messageString", "New devices successfully added!");

        return "CSVUploaded";
    }

    private List<Device> parseDeviceCSV(MultipartFile csv) throws FileUploadException {
        Iterable<CSVRecord> records = multipartFileToRecords(csv);


        List<Device> newDevices = new ArrayList<>();
        for (CSVRecord record : records) {
            Device newDevice = new Device();

            newDevice.setScanValue(record.get("scan value"));
            newDevice.setAvailable(Boolean.parseBoolean(record.get("can remove")));
            newDevice.setRuleID(record.get("rule id"));
            newDevice.setCurrentlyAssigned(Boolean.parseBoolean(record.get("can remove")));
            newDevice.setType(record.get("scan value"));
            newDevice.setId(UUID.randomUUID().toString());

        newDevices.add(newDevice);
        }

        return newDevices;
    }

    private Iterable<CSVRecord> multipartFileToRecords(MultipartFile csv) throws FileUploadException {
        try {
            File file = new File(csv.getOriginalFilename());
            csv.transferTo(file);
            Reader in = new FileReader(file);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader(HEADERS)
                    .withFirstRecordAsHeader()
                    .parse(in);
            return records;
        } catch (IOException e) {
            throw new FileUploadException();
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
    // TODO
    @GetMapping("/group/{id}")
    public String groupSpecified(@PathVariable String id, Model model) {
        List<UserGroup> groupList = new ArrayList<>();
        Boolean found = false;
        model.addAttribute("searchTerm", id);
        try {
            UserGroup userGroup = webBackend.getUserGroup(id);
            if (userGroup.getId().equals(id)) {
                groupList.add(userGroup);
                found = true;
            }
        } catch (Exception e) {
            System.out.println("SQL Error");
        }
        model.addAttribute("found", found);
        model.addAttribute(groupList);
        return "groups";
    }

}
