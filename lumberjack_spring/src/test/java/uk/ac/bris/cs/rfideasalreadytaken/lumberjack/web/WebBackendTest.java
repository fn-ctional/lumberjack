package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.WebBackend;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.*;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "file:${user.dir}/config/testdatabase.properties")
public class WebBackendTest {

    @Autowired
    private DatabaseTesting databaseTesting;

    @Autowired
    private DatabaseUtility databaseUtility;

    @Autowired
    private WebBackend webBackend;

    @Test
    public void testInsertUser() throws Exception {
        databaseUtility.resetUsers();

        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        webBackend.insertUser(testUser);

        assertEquals(webBackend.getUser(testUser.getId()), testUser);
    }

    @Test
    public void testInsertUsers() throws Exception {
        databaseUtility.resetUsers();

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

        webBackend.insertUsers(list);
        assertEquals(webBackend.getUser(testUser.getId()), testUser);
        assertEquals(webBackend.getUser(testUser2.getId()), testUser2);
    }

    @Test
    public void testGetUserNotInDatabase() throws Exception {
        databaseUtility.resetUsers();

        User testUser = new User();
        testUser.setId("definitely_not_in_there_test_user");
        assertNull(webBackend.getUser(testUser.getId()));
    }

    @Test
    public void testGetUser() throws Exception {
        databaseTesting.addTestUsers();

        User testUser = new User("Aidan9876", "1314831486", "ab98765", 2, 0, true, "groupOne");
        assertEquals(webBackend.getUser(testUser.getId()), testUser);
    }

    @Test
    public void testGetUsers() throws Exception {
        databaseTesting.addTestUsers();

        List<User> users = webBackend.getUsers();
        assertTrue(users.contains(new User("Aidan9876", "1314831486", "ab98765", 2, 0, true, "groupOne")));
        assertTrue(users.contains(new User("Betty1248", "457436545","bc12480", 3, 1, true, "groupTwo")));
        assertTrue(users.contains(new User("Callum2468", "845584644", "cd24680",3, 0, false, "groupTwo")));
        assertTrue(users.contains(new User("Dorathy0369", "94648329837", "de03690", 0, 0, true, "groupOne")));
    }

    @Test
    public void testEditUser() throws Exception {
        databaseTesting.addTestUsers();

        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        webBackend.editUser("Aidan9876", testUser);

        assertEquals(webBackend.getUser(testUser.getId()), testUser);
    }

    @Test
    public void testChangeUserGroup() throws Exception {
        databaseTesting.addTestUsers();

        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        webBackend.insertUser(testUser);
        webBackend.changeUserGroup(testUser, new UserGroup("groupTwo"));

        assertEquals(webBackend.getUser(testUser.getId()).getGroupId(), "groupTwo");
    }

    @Test
    public void testSetUserMaxDevices() throws Exception {
        databaseTesting.addTestUsers();

        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        webBackend.insertUser(testUser);
        webBackend.setUserMaxDevices(testUser, 2);

        assertEquals(webBackend.getUser(testUser.getId()).getDeviceLimit(), 2);
    }

    @Test
    public void testChangeUsersGroup() throws Exception {
        databaseTesting.addTestUsers();

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

        webBackend.insertUsers(list);
        webBackend.changeUsersGroup(list, new UserGroup("groupTwo"));

        assertEquals(webBackend.getUser(testUser.getId()).getGroupId(), "groupTwo");
        assertEquals(webBackend.getUser(testUser2.getId()).getGroupId(), "groupTwo");
    }

    @Test
    public void testDeleteUser() throws Exception {
        databaseTesting.addTestUsers();

        User testUser = new User("Aidan9876", "1314831486", "ab98765",2, 0, true, "groupOne");
        webBackend.deleteUser(testUser.getId());
        assertNull(webBackend.getUser(testUser.getId()));
    }

    /**
     * Testing functionality of reset users.
     * Users with assignments should not be removed as this violates foreign key constraints.
     * @throws SQLException In the case of a database error.
     */
    @Test
    public void testResetUsers() throws SQLException {
        databaseTesting.insertTestCases();

        List<User> users = new ArrayList<>();
        users.add(new User("Betty1248", "457436545", "bc12480",3, 1, true, "groupTwo"));
        webBackend.resetUsers();
        assertEquals(webBackend.getUsers(), users);
    }

    @Test
    public void testInsertDevice() throws Exception {
        databaseUtility.resetDevices();

        Device testDevice = new Device();
        testDevice.setType("laptop");
        testDevice.setAvailable(true);
        testDevice.setCurrentlyAssigned(false);
        testDevice.setRuleID("ruleSet1");
        testDevice.setScanValue("12345");
        testDevice.setId("test_device_1");

        webBackend.insertDevice(testDevice);

        assertEquals(webBackend.getDevice(testDevice.getId()), testDevice);
    }

    @Test
    public void testInsertDevices() throws Exception {
        databaseUtility.resetDevices();

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

        webBackend.insertDevices(list);
        assertEquals(webBackend.getDevice(testDevice.getId()), testDevice);
        assertEquals(webBackend.getDevice(testDevice2.getId()), testDevice2);
    }

    @Test
    public void testGetDeviceNotInDatabase() throws Exception {
        databaseUtility.resetDevices();

        Device testDevice = new Device();
        testDevice.setId("definitely_not_in_there_test_device");
        assertNull(webBackend.getDevice(testDevice.getId()));
    }

    @Test
    public void testGetDevice() throws Exception {
        databaseTesting.addTestDevices();

        Device testDevice = new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1");
        assertEquals(webBackend.getDevice(testDevice.getId()), testDevice);
    }

    @Test
    public void testGetDevices() throws Exception {
        databaseTesting.addTestDevices();

        List<Device> obtainedDevices = webBackend.getDevices();
        assertTrue(obtainedDevices.contains(new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1")));
        assertTrue(obtainedDevices.contains(new Device("laptop02", "23482364326842334", "laptop", true, true, "ruleSet2")));
        assertTrue(obtainedDevices.contains(new Device("laptop03", "93482364723648725", "laptop", false, false, "ruleSet1")));
        assertTrue(obtainedDevices.contains(new Device("camera01", "03457237295732925", "camera", true, false, "ruleSet2")));
    }

    @Test
    public void testDeleteDevice() throws Exception {
        databaseTesting.addTestDevices();

        Device testDevice = new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1");
        webBackend.deleteDevice(testDevice.getId());
        assertNull(webBackend.getDevice(testDevice.getId()));
    }

     /**
     * Testing functionality of reset devices.
     * Devices with assignments should not be removed as this violates foreign key constraints.
     * @throws SQLException In the case of a database error.
     */
    @Test
    public void testResetDevices() throws Exception {
        databaseTesting.insertTestCases();

        List<Device> devices = new ArrayList<>();
        devices.add(new Device("laptop02", "23482364326842334", "laptop", true, true, "ruleSet2"));
        webBackend.resetDevices();
        assertEquals(webBackend.getDevices(), devices);
    }

    @Test
    public void testEditDevice() throws Exception {
        databaseTesting.addTestDevices();

        Device testDevice = new Device();
        testDevice.setType("laptop");
        testDevice.setAvailable(true);
        testDevice.setCurrentlyAssigned(false);
        testDevice.setRuleID("ruleSet1");
        testDevice.setScanValue("12345");
        testDevice.setId("test_device_1");

        webBackend.editDevice("laptop01", testDevice);

        assertEquals(webBackend.getDevice(testDevice.getId()), testDevice);
    }

    @Test
    public void testSetDeviceType() throws Exception {
        databaseTesting.addTestDevices();

        Device testDevice = new Device();
        testDevice.setType("laptop");
        testDevice.setAvailable(true);
        testDevice.setCurrentlyAssigned(false);
        testDevice.setRuleID("ruleSet1");
        testDevice.setScanValue("12345");
        testDevice.setId("test_device_1");

        webBackend.insertDevice(testDevice);
        webBackend.setDeviceType(testDevice, "tablet");

        assertEquals(webBackend.getDevice(testDevice.getId()).getType(), "tablet");
    }

    @Test
    public void testGetUserGroup() throws Exception {
        databaseTesting.addTestUserGroups();

        UserGroup testUserGroup = new UserGroup("groupOne");
        assertEquals(webBackend.getUserGroup(testUserGroup.getId()), testUserGroup);
    }

    @Test
    public void testGetUserGroups() throws Exception {
        databaseTesting.addTestUserGroups();

        List<UserGroup> obtainedUserGroups = webBackend.getUserGroups();
        assertTrue(obtainedUserGroups.contains(new UserGroup("groupOne")));
        assertTrue(obtainedUserGroups.contains(new UserGroup("groupTwo")));
    }

    @Test
    public void testDeleteUserGroup() throws Exception {
        databaseTesting.addTestUserGroups();

        UserGroup testUserGroup = new UserGroup("test_group_1");
        webBackend.insertUserGroup(testUserGroup);
        webBackend.deleteUserGroup(testUserGroup.getId());
        assertNull(webBackend.getUserGroup(testUserGroup.getId()));
    }

    @Test
    public void testInsertUserGroup() throws Exception {
        databaseUtility.resetUserGroups();

        UserGroup testUserGroup = new UserGroup("test_group_1");
        webBackend.insertUserGroup(testUserGroup);
        assertEquals(webBackend.getUserGroup(testUserGroup.getId()), testUserGroup);
    }

    @Test
    public void testGetRule() throws Exception {
        databaseTesting.addTestRules();

        Rule testRule = new Rule("ruleSet1", 20);
        assertEquals(webBackend.getRule(testRule.getId()), testRule);
    }

    @Test
    public void testGetRules() throws Exception {
        databaseTesting.addTestRules();

        List<Rule> obtainedRules = webBackend.getRules();
        assertTrue(obtainedRules.contains(new Rule("ruleSet1", 20)));
        assertTrue(obtainedRules.contains(new Rule("ruleSet2", 22)));
    }

    @Test
    public void testDeleteRule() throws Exception {
        databaseTesting.addTestRules();

        Rule testRule = new Rule("test_rule_1", 10);
        webBackend.insertRule(testRule);
        webBackend.deleteRule(testRule.getId());
        assertNull(webBackend.getRule(testRule.getId()));
    }

    @Test
    public void testInsertRule() throws Exception {
        databaseUtility.resetRules();

        Rule testRule = new Rule("test_rule_1", 10);
        webBackend.insertRule(testRule);
        assertEquals(webBackend.getRule(testRule.getId()), testRule);
    }

    @Test
    public void testGetGroupPermissionFromRuleAndGroup() throws Exception {
        databaseTesting.addTestGroupPermissions();

        GroupPermission testGroupPermission = new GroupPermission("ruleSet1", "groupOne");
        assertEquals(webBackend.getGroupPermission(testGroupPermission.getRuleID(), testGroupPermission.getUserGroupID()), testGroupPermission);
    }

    @Test
    public void testGetGroupPermission() throws Exception {
        databaseTesting.addTestGroupPermissions();

        GroupPermission testGroupPermission = new GroupPermission("ruleSet1", "groupOne");
        testGroupPermission = webBackend.getGroupPermission(testGroupPermission.getRuleID(), testGroupPermission.getUserGroupID());
        assertEquals(webBackend.getGroupPermission(testGroupPermission.getId()), testGroupPermission);
    }

    @Test
    public void testGetGroupPermissions() throws Exception {
        databaseTesting.addTestGroupPermissions();

        List<GroupPermission> obtainedGroupPermissions = webBackend.getGroupPermissions();
        assertTrue(obtainedGroupPermissions.contains(new GroupPermission("ruleSet1", "groupOne")));
        assertTrue(obtainedGroupPermissions.contains(new GroupPermission("ruleSet1", "groupTwo")));
        assertTrue(obtainedGroupPermissions.contains(new GroupPermission("ruleSet2", "groupOne")));
    }

    @Test
    public void testDeleteGroupPermission() throws Exception {
        databaseTesting.addTestGroupPermissions();

        GroupPermission testGroupPermission = new GroupPermission("ruleSet1", "groupTwo");
        testGroupPermission = webBackend.getGroupPermission(testGroupPermission.getRuleID(), testGroupPermission.getUserGroupID());
        webBackend.deleteGroupPermission(testGroupPermission.getId());
        assertNull(webBackend.getGroupPermission(testGroupPermission.getRuleID(), testGroupPermission.getUserGroupID()));
    }

    @Test
    public void testInsertGroupPermission() throws Exception {
        databaseUtility.resetGroupPermissions();

        GroupPermission testGroupPermission = new GroupPermission("ruleSet2", "groupTwo");
        webBackend.insertGroupPermission(testGroupPermission);
        assertEquals(webBackend.getGroupPermission(testGroupPermission.getRuleID(), testGroupPermission.getUserGroupID()), testGroupPermission);
    }

    @Test
    public void testLoadAssignmentHistoryFromUserID() throws Exception {
        databaseTesting.insertTestCases();
        List<AssignmentHistory> history = webBackend.getUserAssignmentHistory("Callum2468");

        assertEquals(history.get(0).getUserID(), "Callum2468");
        assertEquals(history.get(0).getDeviceID(), "laptop01");
        assertEquals(history.get(0).getReturnedByID(), "Aidan9876");
    }

    @Test
    public void testLoadAssignmentHistoryFromDeviceID() throws Exception {
        databaseTesting.insertTestCases();
        List<AssignmentHistory> history = webBackend.getDeviceAssignmentHistory("laptop01");

        assertEquals(history.get(0).getUserID(), "Callum2468");
        assertEquals(history.get(0).getDeviceID(), "laptop01");
        assertEquals(history.get(0).getReturnedByID(), "Aidan9876");
    }

    @Test
    public void testGetRecentTakeouts() throws Exception {
        databaseTesting.insertTestCases();
        List<Integer> takeouts = webBackend.getRecentTakeouts(9);
        Integer amount = 0;

        for (int i = 0; i != 8; i++) {
            assertEquals(amount, takeouts.get(i));
        }

        amount = 2;
        assertEquals(amount, takeouts.get(8));
    }

    @Test
    public void testGetRecentReturns() throws Exception {
        databaseTesting.insertTestCases();
        List<Integer> returns = webBackend.getRecentReturns(9);
        Integer amount = 0;

        for (int i = 0; i != 8; i++) {
            assertEquals(amount, returns.get(i));
        }

        amount = 1;
        assertEquals(amount, returns.get(8));
    }

    @Test
    public void testDeletePermissions() throws Exception {
        databaseTesting.insertTestCases();

        List<GroupPermission> permissions = new ArrayList<>();
        permissions.add(new GroupPermission("ruleSet1", "groupOne"));

        webBackend.deletePermissions(permissions);

        assertNull(webBackend.getGroupPermission("ruleSet1", "groupOne"));
    }

    @Test
    public void testGroupHasRule() throws Exception {
        databaseTesting.insertTestCases();
        assertTrue(webBackend.groupHasRule("groupOne", "ruleSet1"));
        assertFalse(webBackend.groupHasRule("groupTwo", "ruleSet2"));
    }

    @Test
    public void testDeletePermissionsByRule() throws Exception {
        databaseTesting.insertTestCases();
        webBackend.deletePermissionsByRule("ruleSet1");
        assertNull(webBackend.getGroupPermission("ruleSet1", "groupOne"));
        assertNull(webBackend.getGroupPermission("ruleSet1", "groupTwo"));
    }

    @Test
    public void testDeletePermissionsByGroup() throws Exception {
        databaseTesting.insertTestCases();
        webBackend.deletePermissionsByGroup("groupOne");
        assertNull(webBackend.getGroupPermission("ruleSet1", "groupOne"));
        assertNull(webBackend.getGroupPermission("ruleSet2", "groupOne"));
    }

    @Test
    public void testRemoveRuleFromDevices() throws Exception {
        databaseTesting.insertTestCases();
        webBackend.removeRuleFromDevices("ruleSet1");
        List<Device> devices = webBackend.getDevicesByRule("ruleSet1");
        assertEquals(devices.size(), 0);
    }

    @Test
    public void testRemoveGroupFromUsers() throws Exception {
        databaseTesting.insertTestCases();
        webBackend.removeRuleFromDevices("groupOne");
        List<User> users = webBackend.getUsers();

        for (int i = 0; i < users.size(); i++) {
            assertNotEquals(users.get(i), "groupOne");
        }
    }

    @Test
    public void testDeleteAssignmentHistoryByDevice() throws Exception {
        databaseTesting.insertTestCases();
        webBackend.deleteAssignmentHistoryByDevice("laptop01");
        List<AssignmentHistory> history = webBackend.getDeviceAssignmentHistory("laptop01");
        assertEquals(history.size(), 0);
    }

    @Test
    public void testDeviceIsOut() throws Exception {
        databaseTesting.insertTestCases();
        assertTrue(webBackend.deviceIsOut("laptop02"));
        assertFalse(webBackend.deviceIsOut("laptop01"));
    }

    @Test
    public void testDeleteAssignmentHistoryByUser() throws Exception {
        databaseTesting.insertTestCases();
        webBackend.deleteAssignmentHistoryByUser("Betty1248");
        List<AssignmentHistory> history = webBackend.getUserAssignmentHistory("Betty1248");
        assertEquals(history.size(), 0);
    }

    @Test
    public void testUserHasOutstandingDevices() throws Exception {
        databaseTesting.insertTestCases();
        assertTrue(webBackend.userHasOutstandingDevices("Betty1248"));
        assertFalse(webBackend.userHasOutstandingDevices("Aidan9876"));
    }

    @Test
    public void testGetDevicesByRule() throws Exception {
        databaseTesting.insertTestCases();
        List<Device> obtainedDevices = webBackend.getDevicesByRule("ruleSet1");
        assertTrue(obtainedDevices.contains(new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1")));
        assertTrue(obtainedDevices.contains(new Device("laptop03", "93482364723648725", "laptop", false, false, "ruleSet1")));
    }

    @Test
    public void testGetUserGroupsByRule() throws Exception {
        databaseTesting.insertTestCases();
        List<UserGroup> obtainedGroups = webBackend.getUserGroupsByRule("ruleSet1");
        assertTrue(obtainedGroups.contains(new UserGroup("groupOne")));
        assertTrue(obtainedGroups.contains(new UserGroup("groupTwo")));
    }

    @Test
    public void testGetGroupUsers() throws Exception {
        databaseTesting.insertTestCases();
        List<User> users = webBackend.getGroupUsers("groupOne");
        assertTrue(users.contains(new User("Aidan9876", "1314831486", "ab98765",2, 0, true, "groupOne")));
        assertTrue(users.contains(new User("Dorathy0369", "94648329837", "de03690",0, 0, true, "groupOne")));
    }

    @Test
    public void testGetGroupRules() throws Exception {
        databaseTesting.insertTestCases();
        List<Rule> obtainedRules = webBackend.getGroupRules("groupOne");
        assertTrue(obtainedRules.contains(new Rule("ruleSet1", 20)));
        assertTrue(obtainedRules.contains(new Rule("ruleSet2", 22)));
    }

    @Test
    public void testGetAvailableCount() throws Exception {
        databaseTesting.insertTestCases();
        assertEquals(webBackend.getAvailableCount(), 2);
    }

    @Test
    public void testGetTakenCount() throws Exception {
        databaseTesting.insertTestCases();
        assertEquals(webBackend.getTakenCount(), 1);
    }

    @Test
    public void testGetOtherCount() throws Exception {
        databaseTesting.insertTestCases();
        assertEquals(webBackend.getOtherCount(), 1);
    }

    @Test
    public void testGetTimes() throws Exception {
        databaseTesting.insertTestCases();
        List<String> times = webBackend.getTimes(3);
        assertEquals(times.size(), 3);
    }

    @Test
    public void testUpdateRule() throws Exception {
        databaseTesting.insertTestCases();
        Rule rule = new Rule("ruleSet1", 8);
        webBackend.updateRule(rule);
        assertEquals(rule.getMaximumRemovalTime(), 8);
    }



    @Test
    public void testGetCurrentlyLateDevices() throws Exception {
        databaseTesting.insertTestCases();
        List<Device> obtainedDevices = webBackend.getCurrentlyLateDevices();
        assertEquals(obtainedDevices.size(),0);
    }

    @Test
    public void testGetPreviouslyLateDevices() throws Exception {
        databaseTesting.insertTestCases();
        List<Device> obtainedDevices = webBackend.getPreviouslyLateDevices();
        assertEquals(obtainedDevices.size(),0);
    }

}
