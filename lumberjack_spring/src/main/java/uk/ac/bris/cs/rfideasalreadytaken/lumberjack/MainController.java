package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;

@Controller
public class MainController extends WebMvcConfigurerAdapter {

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

    @RequestMapping(value = "/devices", method = RequestMethod.PATCH, consumes = "application/json", produces = "text/plain")
    @ResponseBody
    public ResponseEntity changeDeviceState(@RequestBody String scanJSON) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Scan scan = mapper.readValue(scanJSON, Scan.class);


            boolean successful = true;
            if (successful) {
                return ResponseEntity.status(200).body("Device " + scan.getDeviceID() + " successfully by " + scan.getUserID() + ".");
            }
            else if (!successful) { //for example
                return ResponseEntity.status(403).body("User " + scan.getUserID() + " is not permitted to take out device " + scan.getDeviceID() + ".");
            }


        } catch (IOException e) {
            return ResponseEntity.status(400).body("Bad request.");
        }
        // call changeDeviceState in model (which might exist in master, not sure)

        //if unsuccessful:
        //return ResponseEntity.status(403).body("Device has already been taken out.");
        //return ResponseEntity.status(403).body("User not recognised.");
        //return ResponseEntity.status(403).body("Device not recognised.");
        return ResponseEntity.status(500).body("Unknown server error.");
    }

    @RequestMapping(value = "/devices", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity checkDeviceOut() {

        // call changeDeviceState in model (which might exist in master, not sure)

        //if unsuccessful:
        //return ResponseEntity.status(403).body("Device has already been taken out.");
        //return ResponseEntity.status(403).body("User not recognised.");
        //return ResponseEntity.status(403).body("Device not recognised.");
        return ResponseEntity.status(500).body("Unknown server error.");
    }
}
