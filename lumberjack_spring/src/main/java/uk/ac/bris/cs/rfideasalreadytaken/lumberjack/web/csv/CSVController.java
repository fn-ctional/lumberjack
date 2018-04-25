package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.csv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.User;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.FileDownloadException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.FileUploadException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.WebBackend;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Controller
public class CSVController {

    @Autowired
    private CSVBackend csvBackend;

    @Autowired
    private WebBackend webBackend;

    /**
     * CSV file upload POST mapping. CSV must include headers that match user object.
     * Any missing columns will be filled in with default values.
     * @param file CSV file including headers that match the user object.
     *            Headers are non-capitalised and separated by spaces.
     * @param model The session/page model.
     * @return The message page detailing the success or error of the upload.
     * @throws FileUploadException Thrown in case of a parsing error.
     * @throws SQLException Thrown in case of a database error.
     */
    @PostMapping(value = "/add/user/CSV")
    public String addUsersCSV(@RequestParam("file") MultipartFile file, Model model) throws FileUploadException, SQLException {
        List<User> newUsers = csvBackend.parseUserCSV(file);

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
     * @throws FileUploadException Thrown in case of a parsing error.
     * @throws SQLException Thrown in case of a database error.
     */
    @PostMapping(value = "/add/device/CSV")
    public String addDevicesCSV(@RequestParam("file") MultipartFile file, Model model) throws FileUploadException, SQLException {
            List<Device> newDevices = csvBackend.parseDeviceCSV(file);

            webBackend.insertDevices(newDevices);
            model.addAttribute("messageType", "Successful Upload");
            model.addAttribute("messageString", "New devices successfully added!");

        return "message";
    }

    /**
     * Returns a CSV file containing all users.
     * @param response The http response.
     * @throws FileDownloadException Thrown in case of a parsing error.
     * @throws SQLException Thrown in case of a database error.
     */
    @GetMapping(value = "/csv/users", produces = "text/csv")
    public void getUsersCSV(HttpServletResponse response) throws FileDownloadException, SQLException {
        try {
            String csv = csvBackend.getUsersCSV();
            response.getWriter().append(csv);
        } catch (IOException e) {
            throw new FileDownloadException();
        }
    }

    /**
     * Returns a CSV file containing all devices.
     * @param response The http response.
     * @throws FileDownloadException Thrown in case of a parsing error.
     * @throws SQLException Thrown in case of a database error.
     */
    @GetMapping(value = "/csv/devices", produces = "text/csv")
    public void getDevicesCSV(HttpServletResponse response) throws FileDownloadException, SQLException {
        try {
            String csv = csvBackend.getDevicesCSV();
            response.getWriter().append(csv);
        } catch (IOException e) {
            throw new FileDownloadException();
        }
    }

    /**
     * Returns generic error message for any SQLException.
     * @param e Exception that was thrown.
     * @return Generic error page asking user to try again.
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

    /**
     * Exception handler that catches FileUploadException and returns file upload error page.
     * @return An error page asking user to check formatting and try again.
     */
    @ExceptionHandler(FileUploadException.class)
    public ModelAndView handleUploadError() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("message");
        mav.addObject("messageType", "Failed Upload");
        mav.addObject("messageString","Unknown CSV upload error. Please try again and check the CSV is correctly formatted!");
        return mav;
    }

    /**
     * Exception handler that catches FileDownloadException and returns file download error page.
     * @return An error page asking user to try again.
     */
    @ExceptionHandler(FileDownloadException.class)
    public ModelAndView handleDownloadError() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("message");
        mav.addObject("messageType", "Failed Download");
        mav.addObject("messageString","Unknown CSV download error, please try again!");
        return mav;
    }

}
