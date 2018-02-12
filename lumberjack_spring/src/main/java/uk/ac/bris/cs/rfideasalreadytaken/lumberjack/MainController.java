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


    /*
    //This is complicated so I'm going to do a simpler get request first.

    @PatchMapping(value = "/devices", consumes = "application/json", produces = "text/plain")
    @ResponseBody
    public ResponseEntity changeDeviceState(@RequestBody Scan scan) {

            boolean successful = true;
            if (successful) {
                return ResponseEntity.status(200).body("Device " + scan.getDeviceID() + " successfully by " + scan.getUserID() + ".");
            }
            else if (!successful) { //for example
                return ResponseEntity.status(403).body("User " + scan.getUserID() + " is not permitted to take out device " + scan.getDeviceID() + ".");
            }

        // call changeDeviceState in model (which might exist in master, not sure)

        //if unsuccessful:
        //return ResponseEntity.status(403).body("Device has already been taken out.");
        //return ResponseEntity.status(403).body("User not recognised.");
        //return ResponseEntity.status(403).body("Device not recognised.");
        return ResponseEntity.status(500).body("Unknown server error.");
    }*/

    //@Autowired
    //private BackendTemp backendTemp;

    @GetMapping(value = "/devices", produces = "application/json")
    @ResponseBody
    public DeviceState checkIfDeviceOut() {
        User adam = new User("420","hello",10,0,true);
        try {
            DeviceState deviceState = new DeviceState();
            deviceState.setBusy(backend.canUserRemoveDevices(adam));
            return deviceState;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
