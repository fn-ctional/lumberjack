package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.h2.engine.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader.*;

import java.sql.SQLException;

@Service
public class DatabaseTesting {

    @Autowired
    public DatabaseUtility databaseUtility;

    @Autowired
    public DatabaseRules databaseRules;

    @Autowired
    public DatabaseUserGroups databaseUserGroups;

    @Autowired
    public DatabaseUsers databaseUsers;

    @Autowired
    public DatabaseDevices databaseDevices;

    @Autowired
    public DatabaseAssignments databaseAssignments;

    @Autowired
    public CardReaderBackend databaseCardReader;

    public void insertTestCases() throws Exception {
        databaseUtility.resetDatabase();

        Rule rule = new Rule("ruleSet1", 20);
        databaseRules.insertIntoRules(rule);
        rule = new Rule("ruleSet2", 22);
        databaseRules.insertIntoRules(rule);

        UserGroup group = new UserGroup("groupOne");
        databaseUserGroups.insertIntoUserGroups(group);
        group = new UserGroup("groupTwo");
        databaseUserGroups.insertIntoUserGroups(group);

        GroupPermission permission = new GroupPermission("ruleSet1", "groupOne");
        databaseUserGroups.insertIntoGroupPermissions(permission);
        permission = new GroupPermission("ruleSet1", "groupTwo");
        databaseUserGroups.insertIntoGroupPermissions(permission);
        permission = new GroupPermission("ruleSet2", "groupOne");
        databaseUserGroups.insertIntoGroupPermissions(permission);

        User user = new User("Aidan9876", "1314831486", "ab98765", 2, 0, true, "groupOne");
        databaseUsers.insertIntoUsers(user);
        user = new User("Betty1248", "457436545", "bc12480",3, 1, true, "groupTwo");
        databaseUsers.insertIntoUsers(user);
        user = new User("Callum2468", "845584644", "cd24680",3, 0, false, "groupTwo");
        databaseUsers.insertIntoUsers(user);
        user = new User("Dorathy0369", "94648329837", "de03690",0, 0, true, "groupOne");
        databaseUsers.insertIntoUsers(user);

        Device device = new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1");
        databaseDevices.insertIntoDevices(device);
        device = new Device("laptop02", "23482364326842334", "laptop", true, true, "ruleSet2");
        databaseDevices.insertIntoDevices(device);
        device = new Device("laptop03", "93482364723648725", "laptop", false, false, "ruleSet1");
        databaseDevices.insertIntoDevices(device);
        device = new Device("camera01", "03457237295732925", "camera", true, false, "ruleSet2");
        databaseDevices.insertIntoDevices(device);

        Assignment assignment = new Assignment("laptop02", "Betty1248");
        databaseAssignments.insertIntoAssignments(assignment);

        assignment = new Assignment("laptop01", "Callum2468");
        databaseAssignments.insertIntoAssignmentHistory(assignment, "Aidan9876", true);
    }

    public void addTestUsers() throws SQLException {
        databaseUtility.resetUsers();

        User user = new User("Aidan9876", "1314831486", "ab98765", 2, 0, true, "groupOne");
        databaseUsers.insertIntoUsers(user);
        user = new User("Betty1248", "457436545", "bc12480",3, 1, true, "groupTwo");
        databaseUsers.insertIntoUsers(user);
        user = new User("Callum2468", "845584644", "cd24680",3, 0, false, "groupTwo");
        databaseUsers.insertIntoUsers(user);
        user = new User("Dorathy0369", "94648329837", "de03690",0, 0, true, "groupOne");
        databaseUsers.insertIntoUsers(user);
    }

    public void addTestDevices() throws SQLException {
        databaseUtility.resetDevices();

        Device device = new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1");
        databaseDevices.insertIntoDevices(device);
        device = new Device("laptop02", "23482364326842334", "laptop", true, true, "ruleSet2");
        databaseDevices.insertIntoDevices(device);
        device = new Device("laptop03", "93482364723648725", "laptop", false, false, "ruleSet1");
        databaseDevices.insertIntoDevices(device);
        device = new Device("camera01", "03457237295732925", "camera", true, false, "ruleSet2");
        databaseDevices.insertIntoDevices(device);
    }

    public void addTestUserGroups() throws SQLException {
        databaseUtility.resetUserGroups();

        UserGroup group = new UserGroup("groupOne");
        databaseUserGroups.insertIntoUserGroups(group);
        group = new UserGroup("groupTwo");
        databaseUserGroups.insertIntoUserGroups(group);
    }

    public void addTestRules() throws SQLException {
        databaseUtility.resetRules();

        Rule rule = new Rule("ruleSet1", 20);
        databaseRules.insertIntoRules(rule);
        rule = new Rule("ruleSet2", 22);
        databaseRules.insertIntoRules(rule);
    }

    public void addTestGroupPermissions() throws SQLException {
        databaseUtility.resetGroupPermissions();

        GroupPermission permission = new GroupPermission("ruleSet1", "groupOne");
        databaseUserGroups.insertIntoGroupPermissions(permission);
        permission = new GroupPermission("ruleSet1", "groupTwo");
        databaseUserGroups.insertIntoGroupPermissions(permission);
        permission = new GroupPermission("ruleSet2", "groupOne");
        databaseUserGroups.insertIntoGroupPermissions(permission);
    }
}

