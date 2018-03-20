package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.ArrayList;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.DatabaseTesting;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.NotFoundException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.WebBackend;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

@WebAppConfiguration
@SpringBootTest
@TestPropertySource({ "classpath:${envTarget:config/testdatabase}.properties" })
@RunWith(SpringRunner.class)
public class FrontEndDatabaseManagerTests {

    @Autowired
    private DatabaseTesting databaseUtility;

    @Autowired
    private WebBackend frontEndDatabaseManager;

    @Before
    public void setupDatabase() throws Exception {
        databaseUtility.insertTestCases();
    }

    /*

    @Test
    public void testInsertUser() throws Exception {
        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        frontEndDatabaseManager.insertUser(testUser);

        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()), testUser);
    }

    @Test
    public void testInsertUsers() throws Exception {

        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user_1");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        User testUser2 = new User();
        testUser2.setCanRemove(true);
        testUser2.setDeviceLimit(1);
        testUser2.setDevicesRemoved(0);
        testUser2.setId("test_user_2");
        testUser2.setGroupId("groupOne");
        testUser2.setScanValue("98765");

        List<User> list = new ArrayList<>();
        list.add(testUser);
        list.add(testUser2);

        frontEndDatabaseManager.insertUsers(list);
        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()), testUser);
        assertEquals(frontEndDatabaseManager.getUser(testUser2.getId()), testUser2);
    }

    @Test
    public void testGetUserNotInDatabase() throws Exception {
        User testUser = new User();
        testUser.setId("definitely_not_in_there_test_user");
        assertNull(frontEndDatabaseManager.getUser(testUser.getId()));
    }

    @Test
    public void testGetUser() throws Exception {
        User testUser = new User("Aidan9876", "1314831486", 2, 0, true, "groupOne");
        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()),testUser);
    }

    @Test
    public void testGetDeviceNotInDatabase() throws Exception {
        Device testDevice = new Device();
        testDevice.setId("definitely_not_in_there_test_device");
        assertNull(frontEndDatabaseManager.getDevice(testDevice.getId()));
    }

    @Test
    public void testGetDevice() throws Exception {
        Device testDevice = new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1");
        assertEquals(frontEndDatabaseManager.getDevice(testDevice.getId()),testDevice);
    }

    @Test
    public void testInsertDevice() throws Exception {
        Device testDevice = new Device();
        testDevice.setType("laptop");
        testDevice.setAvailable(true);
        testDevice.setCurrentlyAssigned(false);
        testDevice.setRuleID("ruleSet1");
        testDevice.setScanValue("12345");
        testDevice.setId("test_device_1");

        frontEndDatabaseManager.insertDevice(testDevice);

        assertEquals(frontEndDatabaseManager.getDevice(testDevice.getId()), testDevice);
    }

    */
}
