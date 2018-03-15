package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;
import org.springframework.test.context.TestPropertySource;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.ScanDTO;

import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(SpringRunner.class)
@TestPropertySource({ "classpath:${envTarget:config/testdatabase}.properties" })
@SpringBootTest
public class BackendFromCardReaderTests {

    @Autowired
    private BackendCardReaderManager backend;

	@Test
    public void testRemoveDevice() throws Exception {

		backend.resetDatabase();
		backend.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("1314831486");
		scan.setDevice("36109839730967812");
		int removed = backend.loadUser(scan).getDevicesRemoved();

		ScanReturn result = backend.scanReceived(scan);

		User user = backend.loadUser(scan);
		Device device = backend.loadDevice(scan);
		Assignment assignment = backend.loadAssignment(device);

		assertEquals(result,ScanReturn.SUCCESSREMOVAL);
		assertEquals(device.isCurrentlyAssigned(),true);
		assertEquals(user.getDevicesRemoved(),removed+1);
		assertEquals(assignment.getDeviceID(),device.getId());
		assertEquals(assignment.getUserID(),user.getId());
	}

	@Test
	public void testReturnDeviceByCorrectUser() throws Exception {

		backend.resetDatabase();
		backend.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("457436545");
		scan.setDevice("23482364326842334");
		int removed = backend.loadUser(scan).getDevicesRemoved();

		ScanReturn result = backend.scanReceived(scan);

		User user = backend.loadUser(scan);
		Device device = backend.loadDevice(scan);
		Assignment assignment = backend.loadAssignment(device);
		AssignmentHistory history = backend.loadAssignmentHistory(device);

		assertEquals(result,ScanReturn.SUCCESSRETURN);
		assertEquals(device.isCurrentlyAssigned(),false);
		assertEquals(user.getDevicesRemoved(),removed-1);
		assertNull(assignment);
		assertEquals(history.getUserID(),user.getId());
		assertEquals(history.getDeviceID(),device.getId());
	}

	@Test
	public void testReturnAndRemoveDeviceByNewUser() throws Exception {

		backend.resetDatabase();
		backend.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("1314831486");
		scan.setDevice("23482364326842334");
		int removedNew = backend.loadUser(scan).getDevicesRemoved();
		int removedOld = backend.loadUser("Betty1248").getDevicesRemoved();

		ScanReturn result = backend.scanReceived(scan);

		User newUser = backend.loadUser(scan);
		Device device = backend.loadDevice(scan);
		Assignment assignment = backend.loadAssignment(device);
		AssignmentHistory history = backend.loadAssignmentHistory(device);
		User oldUser = backend.loadUser(history.getUserID());

		assertEquals(result,ScanReturn.SUCCESSRETURNANDREMOVAL);
		assertEquals(device.isCurrentlyAssigned(),true);
		assertEquals(newUser.getDevicesRemoved(),removedNew+1);
		assertEquals(assignment.getDeviceID(),device.getId());
		assertEquals(assignment.getUserID(),newUser.getId());
		assertEquals(oldUser.getDevicesRemoved(),removedOld-1);
		assertEquals(history.getUserID(),oldUser.getId());
		assertEquals(history.getDeviceID(),device.getId());
	}


}
