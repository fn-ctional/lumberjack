package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader;

import org.junit.Before;
import org.springframework.test.context.TestPropertySource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.User;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader.data.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(SpringRunner.class)
@TestPropertySource(locations = "file:${user.dir}/config/testdatabase.properties")
@SpringBootTest
public class CardReaderBackendTests {

    @Autowired
    private DatabaseTesting databaseTesting;

	@Test
    public void testRemoveDevice() throws Exception {
        databaseTesting.addValidScanData();

		ScanDTO scan = new ScanDTO();
		scan.setUser("1314831486");
		scan.setDevice("36109839730967812");
		int removed = databaseTesting.databaseUsers.loadUser(scan).getDevicesRemoved();

		ScanReturn result = databaseTesting.databaseCardReader.scanReceived(scan);

		User user = databaseTesting.databaseUsers.loadUser(scan);
		Device device = databaseTesting.databaseDevices.loadDevice(scan);
		Assignment assignment = databaseTesting.databaseAssignments.loadAssignment(device);

		assertEquals(result,ScanReturn.SUCCESSREMOVAL);
		assertEquals(device.isCurrentlyAssigned(),true);
		assertEquals(user.getDevicesRemoved(),removed+1);
		assertEquals(assignment.getDeviceID(),device.getId());
		assertEquals(assignment.getUserID(),user.getId());
	}

	@Test
	public void testReturnDeviceByCorrectUser() throws Exception {
        databaseTesting.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("457436545");
		scan.setDevice("23482364326842334");
		int removed = databaseTesting.databaseUsers.loadUser(scan).getDevicesRemoved();

		ScanReturn result = databaseTesting.databaseCardReader.scanReceived(scan);

		User user = databaseTesting.databaseUsers.loadUser(scan);
		Device device = databaseTesting.databaseDevices.loadDevice(scan);
		Assignment assignment = databaseTesting.databaseAssignments.loadAssignment(device);
		AssignmentHistory history = databaseTesting.databaseAssignments.loadAssignmentHistory(device);

		assertEquals(result,ScanReturn.SUCCESSRETURN);
		assertEquals(device.isCurrentlyAssigned(),false);
		assertEquals(user.getDevicesRemoved(),removed-1);
		assertNull(assignment);
		assertEquals(history.getUserID(),user.getId());
		assertEquals(history.getDeviceID(),device.getId());
	}

	@Test
	public void testReturnAndRemoveDeviceByNewUser() throws Exception {
    	databaseTesting.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("1314831486");
		scan.setDevice("23482364326842334");
		int removedNew = databaseTesting.databaseUsers.loadUser(scan).getDevicesRemoved();
		int removedOld = databaseTesting.databaseUsers.loadUser("Betty1248").getDevicesRemoved();

		ScanReturn result = databaseTesting.databaseCardReader.scanReceived(scan);

		User newUser = databaseTesting.databaseUsers.loadUser(scan);
		Device device = databaseTesting.databaseDevices.loadDevice(scan);
		Assignment assignment = databaseTesting.databaseAssignments.loadAssignment(device);
		AssignmentHistory history = databaseTesting.databaseAssignments.loadAssignmentHistory(device);
		User oldUser = databaseTesting.databaseUsers.loadUser(history.getUserID());

		assertEquals(result,ScanReturn.SUCCESSRETURNANDREMOVAL);
		assertEquals(device.isCurrentlyAssigned(),true);
		assertEquals(newUser.getDevicesRemoved(),removedNew+1);
		assertEquals(assignment.getDeviceID(),device.getId());
		assertEquals(assignment.getUserID(),newUser.getId());
		assertEquals(oldUser.getDevicesRemoved(),removedOld-1);
		assertEquals(history.getUserID(),oldUser.getId());
		assertEquals(history.getDeviceID(),device.getId());
	}

	@Test
	public void testInvalidUser() throws Exception {
        databaseTesting.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("123456789");
		scan.setDevice("23482364326842334");

		ScanReturn result = databaseTesting.databaseCardReader.scanReceived(scan);

		assertEquals(result, ScanReturn.FAILUSERNOTRECOGNISED);
	}

	@Test
	public void testUserGroupAndDeviceRulesetNotCompatable() throws Exception {
        databaseTesting.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("457436545");
		scan.setDevice("03457237295732925");

		ScanReturn result = databaseTesting.databaseCardReader.scanReceived(scan);

		assertEquals(result, ScanReturn.FAILUSERGROUPRULESETNOTCOMPATABLE);
	}

	@Test
	public void testInvalidDevice() throws Exception {
        databaseTesting.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("457436545");
		scan.setDevice("012345678910");

		ScanReturn result = databaseTesting.databaseCardReader.scanReceived(scan);

		assertEquals(result, ScanReturn.FAILDEVICENOTRECOGNISED);
	}

	@Test
	public void testUserAtDeviceLimit() throws Exception {
        databaseTesting.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("94648329837");
		scan.setDevice("03457237295732925");

		ScanReturn result = databaseTesting.databaseCardReader.scanReceived(scan);

		assertEquals(result, ScanReturn.FAILUSERATDEVICELIMIT);
	}

	@Test
	public void testUserNotPermittedToRemove() throws Exception {
        databaseTesting.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("845584644");
		scan.setDevice("03457237295732925");

		ScanReturn result = databaseTesting.databaseCardReader.scanReceived(scan);

		assertEquals(result, ScanReturn.FAILUSERNORPERMITTEDTOREMOVE);
	}

	@Test
	public void testDeviceNotAvailable() throws Exception {
        databaseTesting.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("457436545");
		scan.setDevice("93482364723648725");

		ScanReturn result = databaseTesting.databaseCardReader.scanReceived(scan);

		assertEquals(result, ScanReturn.FAILDEVICEUNAVIALABLE);
	}



}
