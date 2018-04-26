package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader.data.ScanDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.WebBackend;

import java.sql.SQLException;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@TestPropertySource(locations = "file:${user.dir}/config/testdatabase.properties")
@SpringBootTest
public class CardReaderControllerTest {

    @Autowired
    private CardReaderController cardReaderController;

    @Autowired
    private DatabaseTesting databaseTesting;

    @Autowired
    private WebBackend webBackend;

    @Test
    public void testValidTakeOut() throws SQLException {
        databaseTesting.addValidScanData();

        ScanDTO validScan = new ScanDTO();
        validScan.setUser("1314831486");
        validScan.setDevice("36109839730967812");
        ResponseEntity responseEntity = cardReaderController.changeDeviceState(validScan);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals(responseEntity.getBody(),"Device " + validScan.getDevice() + " successfully taken out by " + validScan.getUser() + ".");
        assertEquals(webBackend.getUser("user01").getDevicesRemoved(), 1);
        assertTrue(webBackend.getDevice("laptop01").isCurrentlyAssigned());
        assertFalse(webBackend.getDeviceAssignments("laptop01").isEmpty());
        assertEquals(webBackend.getDeviceAssignments("laptop01").get(0).getUserID(), "user01");
        assertEquals(webBackend.getDeviceAssignments("laptop01").get(0).getDeviceID(), "laptop01");
    }

    @Test
    public void testMissingUserDataRequest() throws SQLException {
        databaseTesting.addValidScanData();

        ScanDTO validScan = new ScanDTO();
        validScan.setDevice("36109839730967812");
        ResponseEntity responseEntity = cardReaderController.changeDeviceState(validScan);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        assertEquals(responseEntity.getBody(),"User not recognised.");
        assertEquals(webBackend.getUser("user01").getDevicesRemoved(), 0);
        assertFalse(webBackend.getDevice("laptop01").isCurrentlyAssigned());
        assertTrue(webBackend.getDeviceAssignments("laptop01").isEmpty());
    }

    @Test
    public void testMissingDeviceDataRequest() throws SQLException {
        databaseTesting.addValidScanData();

        ScanDTO validScan = new ScanDTO();
        validScan.setUser("1314831486");
        ResponseEntity responseEntity = cardReaderController.changeDeviceState(validScan);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        assertEquals(responseEntity.getBody(),"Device not recognised.");
        assertEquals(webBackend.getUser("user01").getDevicesRemoved(), 0);
        assertFalse(webBackend.getDevice("laptop01").isCurrentlyAssigned());
        assertTrue(webBackend.getDeviceAssignments("laptop01").isEmpty());
    }

    @Test
    public void testBadDeviceRequest() throws SQLException {
        databaseTesting.addValidScanData();

        ScanDTO validScan = new ScanDTO();
        validScan.setUser("1314831486");
        validScan.setDevice("not_here");
        ResponseEntity responseEntity = cardReaderController.changeDeviceState(validScan);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        assertEquals(responseEntity.getBody(),"Device not recognised.");
        assertEquals(webBackend.getUser("user01").getDevicesRemoved(), 0);
        assertFalse(webBackend.getDevice("laptop01").isCurrentlyAssigned());
        assertTrue(webBackend.getDeviceAssignments("laptop01").isEmpty());
    }

    @Test
    public void testBadUserRequest() throws SQLException {
        databaseTesting.addValidScanData();

        ScanDTO validScan = new ScanDTO();
        validScan.setUser("not_here");
        validScan.setDevice("36109839730967812");
        ResponseEntity responseEntity = cardReaderController.changeDeviceState(validScan);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        assertEquals(responseEntity.getBody(),"User not recognised.");
        assertEquals(webBackend.getUser("user01").getDevicesRemoved(), 0);
        assertFalse(webBackend.getDevice("laptop01").isCurrentlyAssigned());
        assertTrue(webBackend.getDeviceAssignments("laptop01").isEmpty());
    }

    @Test
    public void testValidReturn() throws SQLException {
        databaseTesting.addReturnScanData();

        ScanDTO validScan = new ScanDTO();
        validScan.setUser("1314831486");
        validScan.setDevice("36109839730967812");
        ResponseEntity responseEntity = cardReaderController.changeDeviceState(validScan);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals(responseEntity.getBody(),"Device " + validScan.getDevice() + " successfully returned by " + validScan.getUser() + ".");
        assertEquals(webBackend.getUser("user01").getDevicesRemoved(), 0);
        assertFalse(webBackend.getDevice("laptop01").isCurrentlyAssigned());
        assertTrue(webBackend.getDeviceAssignments("laptop01").isEmpty());
    }

    @Test
    public void testMissingUserReturnRequest() throws SQLException {
        databaseTesting.addMissingUserReturnScanData();

        ScanDTO validScan = new ScanDTO();
        validScan.setUser("1314831486");
        validScan.setDevice("36109839730967812");
        ResponseEntity responseEntity = cardReaderController.changeDeviceState(validScan);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        assertEquals(responseEntity.getBody(),"User not recognised.");
        assertTrue(webBackend.getDevice("laptop01").isCurrentlyAssigned());
    }

    @Test
    public void testMissingDeviceReturnRequest() throws SQLException {
        databaseTesting.addMissingDeviceReturnScanData();

        ScanDTO validScan = new ScanDTO();
        validScan.setUser("1314831486");
        validScan.setDevice("36109839730967812");
        ResponseEntity responseEntity = cardReaderController.changeDeviceState(validScan);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        assertEquals(responseEntity.getBody(),"Device not recognised.");
        assertEquals(webBackend.getUser("user01").getDevicesRemoved(), 1);
    }
}