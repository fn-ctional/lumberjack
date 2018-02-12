package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class MainController extends WebMvcConfigurerAdapter {

    @Autowired
    private Backend backend;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }

    @RequestMapping(value={"", "/"})
    public String index() {
        return "templates/home.html";
    }

    @RequestMapping(value={"/about"})
    public String about() {
        return "templates/about.html";
    }

    @RequestMapping(value = {"/download"})
    public String download() {
        return "templates/download.html";
    }

    /**
     * Handler for taking out and returning device scans.
     * @param scan A JSON containing device and user strings.
         * @return An HTTP status code and body description of error or action performed.
     */
    @PatchMapping(value = "/devices", consumes = "application/json", produces = "text/plain")
    @ResponseBody
    public ResponseEntity changeDeviceState(@RequestBody Scan scan) {
        try {
            String result = backend.scanRecieved(scan);
            if (result.isEmpty()) throw new Exception();
            if (result.equals("Scan not recognised")) {
                return ResponseEntity.status(500).body("Scan not recognised");
            }
            if (result.equals("Failed to connect to database.")) {
                return ResponseEntity.status(500).body("Failed to connect to database.");
            }
            if (result.equals("Failed to connect to database.")) {
                //taken out vs put in
                return ResponseEntity.status(200).body("Device " + scan.getDeviceID() + " successfully by " + scan.getUserID() + ".");
            }
            if (result.equals("Failed to connect to database.")) {
                return ResponseEntity.status(403).body("User " + scan.getUserID() + " is not permitted to take out device " + scan.getDeviceID() + ".");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Unknown server error.");
        }

        //return ResponseEntity.status(403).body("Device has already been taken out.");
        //return ResponseEntity.status(403).body("User not recognised.");
        //return ResponseEntity.status(403).body("Device not recognised.");

        return ResponseEntity.status(500).body("Unknown server error.");
    }


    @GetMapping(value = "/devices/{id}", produces = "application/json")
    @ResponseBody
    public DeviceState checkIfDeviceOut(@PathVariable String id) {

        return null;
    }
}
