package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.ArrayList;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.DatabaseTesting;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.WebBackend;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.*;

import static org.junit.Assert.*;

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
    public void testGetUsers() throws Exception {
        List<User> users = frontEndDatabaseManager.getUsers();
        assertTrue(users.contains(new User("Aidan9876", "1314831486", 2, 0, true, "groupOne")));
        assertTrue(users.contains(new User("Betty1248", "457436545", 3, 1, true, "groupTwo")));
        assertTrue(users.contains(new User("Callum2468", "845584644", 3, 0, false, "groupTwo")));
        assertTrue(users.contains(new User("Dorathy0369", "94648329837", 0, 0, true, "groupOne")));
    }

    @Test
    public void testEditUser() throws Exception {
        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        frontEndDatabaseManager.editUser("Aidan9876",testUser);

        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()), testUser);
    }

    @Test
    public void testChangeUserGroup() throws Exception {
        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        frontEndDatabaseManager.insertUser(testUser);
        frontEndDatabaseManager.changeUserGroup(testUser,new UserGroup("groupTwo"));

        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()).getGroupId(), "groupTwo");
    }

    @Test
    public void testSetUserMaxDevices() throws Exception {
        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        frontEndDatabaseManager.insertUser(testUser);
        frontEndDatabaseManager.setUserMaxDevices(testUser,2);

        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()).getDeviceLimit(), 2);
    }

    @Test
    public void testChangeUsersGroup() throws Exception {
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
        frontEndDatabaseManager.changeUsersGroup(list,new UserGroup("groupTwo"));

        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()).getGroupId(), "groupTwo");
        assertEquals(frontEndDatabaseManager.getUser(testUser2.getId()).getGroupId(), "groupTwo");
    }

    @Test
    public void testDeleteUser() throws Exception {
        User testUser = new User("Aidan9876", "1314831486", 2, 0, true, "groupOne");
        frontEndDatabaseManager.deleteUser(testUser.getId());
        assertNull(frontEndDatabaseManager.getUser(testUser.getId()));
    }

    @Test
    public void testResetUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User("Betty1248", "457436545", 3, 1, true, "groupTwo"));
        frontEndDatabaseManager.resetUsers();
        assertEquals(frontEndDatabaseManager.getUsers(),users);
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

    @Test
    public void testInsertDevices() throws Exception {

        Device testDevice = new Device();
        testDevice.setType("laptop");
        testDevice.setAvailable(true);
        testDevice.setCurrentlyAssigned(false);
        testDevice.setRuleID("ruleSet1");
        testDevice.setScanValue("12345");
        testDevice.setId("test_device_1");

        Device testDevice2 = new Device();
        testDevice2.setType("laptop");
        testDevice2.setAvailable(true);
        testDevice2.setCurrentlyAssigned(false);
        testDevice2.setRuleID("ruleSet1");
        testDevice2.setScanValue("98765");
        testDevice2.setId("test_device_2");

        List<Device> list = new ArrayList<>();
        list.add(testDevice);
        list.add(testDevice2);

        frontEndDatabaseManager.insertDevices(list);
        assertEquals(frontEndDatabaseManager.getDevice(testDevice.getId()), testDevice);
        assertEquals(frontEndDatabaseManager.getDevice(testDevice2.getId()), testDevice2);
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
    public void testGetDevices() throws Exception {
        List<Device> obtainedDevices = frontEndDatabaseManager.getDevices();
        assertTrue(obtainedDevices.contains(new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1")));
        assertTrue(obtainedDevices.contains(new Device("laptop02", "23482364326842334", "laptop", true, true, "ruleSet2")));
        assertTrue(obtainedDevices.contains(new Device("laptop03", "93482364723648725", "laptop", false, false, "ruleSet1")));
        assertTrue(obtainedDevices.contains(new Device("camera01", "03457237295732925", "camera", true, false, "ruleSet2")));
    }

    @Test
    public void testDeleteDevice() throws Exception {
        Device testDevice = new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1");
        frontEndDatabaseManager.deleteDevice(testDevice.getId());
        assertNull(frontEndDatabaseManager.getDevice(testDevice.getId()));
    }

    @Test
    public void testResetDevices() throws Exception {
        List<Device> devices = new ArrayList<>();
        devices.add(new Device("laptop02", "23482364326842334", "laptop", true, true, "ruleSet2"));
        frontEndDatabaseManager.resetDevices();
        assertEquals(frontEndDatabaseManager.getDevices(),devices);
    }

    @Test
    public void testEditDevice() throws Exception {
        Device testDevice = new Device();
        testDevice.setType("laptop");
        testDevice.setAvailable(true);
        testDevice.setCurrentlyAssigned(false);
        testDevice.setRuleID("ruleSet1");
        testDevice.setScanValue("12345");
        testDevice.setId("test_device_1");

        frontEndDatabaseManager.editDevice("laptop01",testDevice);

        assertEquals(frontEndDatabaseManager.getDevice(testDevice.getId()), testDevice);
    }

    @Test
    public void testSetDeviceType() throws Exception {
        Device testDevice = new Device();
        testDevice.setType("laptop");
        testDevice.setAvailable(true);
        testDevice.setCurrentlyAssigned(false);
        testDevice.setRuleID("ruleSet1");
        testDevice.setScanValue("12345");
        testDevice.setId("test_device_1");

        frontEndDatabaseManager.insertDevice(testDevice);
        frontEndDatabaseManager.setDeviceType(testDevice,"tablet");

        assertEquals(frontEndDatabaseManager.getDevice(testDevice.getId()).getType(), "tablet");
    }

    @Test
    public void testGetUserGroup() throws Exception {
        UserGroup testUserGroup = new UserGroup("groupOne");
        assertEquals(frontEndDatabaseManager.getUserGroup(testUserGroup.getId()),testUserGroup);
    }

    @Test
    public void testGetUserGroups() throws Exception {
        List<UserGroup> obtainedUserGroups = frontEndDatabaseManager.getUserGroups();
        assertTrue(obtainedUserGroups.contains(new UserGroup("groupOne")));
        assertTrue(obtainedUserGroups.contains(new UserGroup("groupTwo")));
    }

    @Test
    public void testDeleteUserGroup() throws Exception {
        UserGroup testUserGroup = new UserGroup("test_group_1");
        frontEndDatabaseManager.insertUserGroup(testUserGroup);
        frontEndDatabaseManager.deleteUserGroup(testUserGroup.getId());
        assertNull(frontEndDatabaseManager.getUserGroup(testUserGroup.getId()));
    }

    @Test
    public void testInsertUserGroup() throws Exception {
        UserGroup testUserGroup = new UserGroup("test_group_1");
        frontEndDatabaseManager.insertUserGroup(testUserGroup);
        assertEquals(frontEndDatabaseManager.getUserGroup(testUserGroup.getId()), testUserGroup);
    }

    @Test
    public void testGetRule() throws Exception {
        Rule testRule = new Rule("ruleSet1",20);
        assertEquals(frontEndDatabaseManager.getRule(testRule.getId()),testRule);
    }

    @Test
    public void testGetRules() throws Exception {
        List<Rule> obtainedRules = frontEndDatabaseManager.getRules();
        assertTrue(obtainedRules.contains(new Rule("ruleSet1",20)));
        assertTrue(obtainedRules.contains(new Rule("ruleSet2",22)));
    }

    @Test
    public void testDeleteRule() throws Exception {
        Rule testRule = new Rule("test_rule_1",10);
        frontEndDatabaseManager.insertRule(testRule);
        frontEndDatabaseManager.deleteRule(testRule.getId());
        assertNull(frontEndDatabaseManager.getRule(testRule.getId()));
    }

    @Test
    public void testInsertRule() throws Exception {
        Rule testRule = new Rule("test_rule_1",10);
        frontEndDatabaseManager.insertRule(testRule);
        assertEquals(frontEndDatabaseManager.getRule(testRule.getId()), testRule);
    }

    @Test
    public void testGetGroupPermissionFromRuleAndGroup() throws Exception {
        GroupPermission testGroupPermission = new GroupPermission("ruleSet1","groupOne");
        assertEquals(frontEndDatabaseManager.getGroupPermission(testGroupPermission.getRuleID(), testGroupPermission.getUserGroupID()),testGroupPermission);
    }

    @Test
    public void testGetGroupPermission() throws Exception {
        GroupPermission testGroupPermission = new GroupPermission("ruleSet1","groupOne");
        testGroupPermission = frontEndDatabaseManager.getGroupPermission(testGroupPermission.getRuleID(),testGroupPermission.getUserGroupID());
        assertEquals(frontEndDatabaseManager.getGroupPermission(testGroupPermission.getId()),testGroupPermission);
    }

    @Test
    public void testGetGroupPermissions() throws Exception {
        List<GroupPermission> obtainedGroupPermissions = frontEndDatabaseManager.getGroupPermissions();
        assertTrue(obtainedGroupPermissions.contains(new GroupPermission("ruleSet1","groupOne")));
        assertTrue(obtainedGroupPermissions.contains(new GroupPermission("ruleSet1","groupTwo")));
        assertTrue(obtainedGroupPermissions.contains(new GroupPermission("ruleSet2","groupOne")));
    }

    @Test
    public void testDeleteGroupPermission() throws Exception {
        GroupPermission testGroupPermission = new GroupPermission("ruleSet1","groupTwo");
        testGroupPermission = frontEndDatabaseManager.getGroupPermission(testGroupPermission.getRuleID(), testGroupPermission.getUserGroupID());
        frontEndDatabaseManager.deleteGroupPermission(testGroupPermission.getId());
        assertNull(frontEndDatabaseManager.getGroupPermission(testGroupPermission.getRuleID(),testGroupPermission.getUserGroupID()));
    }

    @Test
    public void testInsertGroupPermission() throws Exception {
        GroupPermission testGroupPermission = new GroupPermission("ruleSet2","groupTwo");
        frontEndDatabaseManager.insertGroupPermission(testGroupPermission);
        assertEquals(frontEndDatabaseManager.getGroupPermission(testGroupPermission.getRuleID(),testGroupPermission.getUserGroupID()), testGroupPermission);
    }

    @Test
    public void testLoadAssignmentHistoryFromUserID() throws Exception {
        List<AssignmentHistory> history = frontEndDatabaseManager.getUserAssignmentHistory("Callum2468");

        assertEquals(history.get(0).getUserID(), "Callum2468");
        assertEquals(history.get(0).getDeviceID(), "laptop01");
        assertEquals(history.get(0).getReturnedByID(), "Aidan9876");
    }

    @Test
    public void testLoadAssignmentHistoryFromDeviceID() throws Exception {
        List<AssignmentHistory> history = frontEndDatabaseManager.getDeviceAssignmentHistory("laptop01");

        assertEquals(history.get(0).getUserID(), "Callum2468");
        assertEquals(history.get(0).getDeviceID(), "laptop01");
        assertEquals(history.get(0).getReturnedByID(), "Aidan9876");
    }


}
