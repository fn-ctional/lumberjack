package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.test.context.TestPropertySource;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader.CardReaderBackend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.User;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader.data.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(SpringRunner.class)
@TestPropertySource({ "classpath:${envTarget:config/testdatabase}.properties" })
@SpringBootTest
public class BackendFromCardReaderTests {

    @Autowired
    private DatabaseTesting database;

	/*

	@Test
    public void testRemoveDevice() throws Exception {

		database.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("1314831486");
		scan.setDevice("36109839730967812");
		int removed = database.databaseUsers.loadUser(scan).getDevicesRemoved();

		ScanReturn result = database.databaseCardReader.scanReceived(scan);

		User user = database.databaseUsers.loadUser(scan);
		Device device = database.databaseDevices.loadDevice(scan);
		Assignment assignment = database.databaseAssignments.loadAssignment(device);

		assertEquals(result,ScanReturn.SUCCESSREMOVAL);
		assertEquals(device.isCurrentlyAssigned(),true);
		assertEquals(user.getDevicesRemoved(),removed+1);
		assertEquals(assignment.getDeviceID(),device.getId());
		assertEquals(assignment.getUserID(),user.getId());
	}

	@Test
	public void testReturnDeviceByCorrectUser() throws Exception {

        database.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("457436545");
		scan.setDevice("23482364326842334");
		int removed = database.databaseUsers.loadUser(scan).getDevicesRemoved();

		ScanReturn result = database.databaseCardReader.scanReceived(scan);

		User user = database.databaseUsers.loadUser(scan);
		Device device = database.databaseDevices.loadDevice(scan);
		Assignment assignment = database.databaseAssignments.loadAssignment(device);
		AssignmentHistory history = database.databaseAssignments.loadAssignmentHistory(device);

		assertEquals(result,ScanReturn.SUCCESSRETURN);
		assertEquals(device.isCurrentlyAssigned(),false);
		assertEquals(user.getDevicesRemoved(),removed-1);
		assertNull(assignment);
		assertEquals(history.getUserID(),user.getId());
		assertEquals(history.getDeviceID(),device.getId());
	}

	@Test
	public void testReturnAndRemoveDeviceByNewUser() throws Exception {

        database.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("1314831486");
		scan.setDevice("23482364326842334");
		int removedNew = database.databaseUsers.loadUser(scan).getDevicesRemoved();
		int removedOld = database.databaseUsers.loadUser("Betty1248").getDevicesRemoved();

		ScanReturn result = database.databaseCardReader.scanReceived(scan);

		User newUser = database.databaseUsers.loadUser(scan);
		Device device = database.databaseDevices.loadDevice(scan);
		Assignment assignment = database.databaseAssignments.loadAssignment(device);
		AssignmentHistory history = database.databaseAssignments.loadAssignmentHistory(device);
		User oldUser = database.databaseUsers.loadUser(history.getUserID());

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

        database.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("123456789");
		scan.setDevice("23482364326842334");

		ScanReturn result = database.databaseCardReader.scanReceived(scan);

		assertEquals(result, ScanReturn.FAILUSERNOTRECOGNISED);
	}

	@Test
	public void testUserGroupAndDeviceRulesetNotCompatable() throws Exception {

        database.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("457436545");
		scan.setDevice("03457237295732925");

		ScanReturn result = database.databaseCardReader.scanReceived(scan);

		assertEquals(result, ScanReturn.FAILUSERGROUPRULESETNOTCOMPATABLE);
	}

	@Test
	public void testInvalidDevice() throws Exception {

        database.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("457436545");
		scan.setDevice("012345678910");

		ScanReturn result = database.databaseCardReader.scanReceived(scan);

		assertEquals(result, ScanReturn.FAILDEVICENOTRECOGNISED);
	}

	@Test
	public void testUserAtDeviceLimit() throws Exception {

        database.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("94648329837");
		scan.setDevice("03457237295732925");

		ScanReturn result = database.databaseCardReader.scanReceived(scan);

		assertEquals(result, ScanReturn.FAILUSERATDEVICELIMIT);
	}

	@Test
	public void testUserNotPermittedToRemove() throws Exception {

        database.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("845584644");
		scan.setDevice("03457237295732925");

		ScanReturn result = database.databaseCardReader.scanReceived(scan);

		assertEquals(result, ScanReturn.FAILUSERNORPERMITTEDTOREMOVE);
	}

	@Test
	public void testDeviceNotAvailable() throws Exception {

        database.insertTestCases();

		ScanDTO scan = new ScanDTO();
		scan.setUser("457436545");
		scan.setDevice("93482364723648725");

		ScanReturn result = database.databaseCardReader.scanReceived(scan);

		assertEquals(result, ScanReturn.FAILDEVICEUNAVIALABLE);
	}


	*/

}
