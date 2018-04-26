package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader.data.ScanDTO;

@Controller
public class CardReaderController extends WebMvcConfigurerAdapter {

    @Autowired
    private CardReaderBackend cardReaderBackend;

    /**
     * Handler for taking out and returning device scans.
     *
     * @param scanDTO A JSON containing device and user strings.
     * @return An HTTP status code and body description of error or action performed.
     */
    @PatchMapping(value = "/rpi", consumes = "application/json", produces = "text/plain")
    @ResponseBody
    public ResponseEntity changeDeviceState(@RequestBody ScanDTO scanDTO) {
        try {
            ScanReturn result = cardReaderBackend.scanReceived(scanDTO);
            switch (result) {
                case SUCCESSRETURN:
                    return ResponseEntity.status(200).body("Device " + scanDTO.getDevice() + " successfully returned by " + scanDTO.getUser() + ".");
                case SUCCESSREMOVAL:
                    return ResponseEntity.status(200).body("Device " + scanDTO.getDevice() + " successfully taken out by " + scanDTO.getUser() + ".");
                case SUCCESSRETURNANDREMOVAL:
                    return ResponseEntity.status(200).body("Device " + scanDTO.getDevice() + " successfully returned and taken out by " + scanDTO.getUser() + ".");
                case FAILUSERNOTRECOGNISED:
                    return ResponseEntity.status(403).body("User not recognised.");
                case FAILDEVICENOTRECOGNISED:
                    return ResponseEntity.status(403).body("Device not recognised.");
                case FAILUSERATDEVICELIMIT:
                    return ResponseEntity.status(403).body("User " + scanDTO.getUser() + " is at their limit of removable devices.");
                case FAILUSERNORPERMITTEDTOREMOVE:
                    return ResponseEntity.status(403).body("User " + scanDTO.getUser() + " is not permitted to remove.");
                case FAILDEVICEUNAVIALABLE:
                    return ResponseEntity.status(403).body("Device " + scanDTO.getDevice() + " can not be taken out.");
                case ERRORCONNECTIONFAILED:
                    return ResponseEntity.status(500).body("Error connecting to database.");
                case ERRORUSERNOTLOADED:
                    return ResponseEntity.status(403).body("User " + scanDTO.getUser() + " not recognised.");
                case ERRORDEVICENOTLOADED:
                    return ResponseEntity.status(403).body("Device " + scanDTO.getDevice() + " not recognised.");
                case ERRORDEVICEHANDLINGFAILED:
                    return ResponseEntity.status(500).body("Error handling device return or takeout.");
                case ERRORRETURNFAILED:
                    return ResponseEntity.status(500).body("Error returning device.");
                case ERRORREMOVALFAILED:
                    return ResponseEntity.status(500).body("Error taking out device.");
                default:
                    throw new Exception();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Unknown server error.");
        }
    }
}
