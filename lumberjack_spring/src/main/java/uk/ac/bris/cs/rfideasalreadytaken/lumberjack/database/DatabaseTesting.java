package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.h2.engine.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.*;

@Service
public class DatabaseTesting {

    @Autowired
    private DatabaseUtility databaseUtility;

    @Autowired
    private DatabaseRules databaseRules;

    @Autowired
    private DatabaseUserGroups databaseUserGroups;

    @Autowired
    private DatabaseUsers databaseUsers;

    @Autowired
    private DatabaseDevices databaseDevices;

    @Autowired
    private DatabaseAssignments databaseAssignments;


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

        User user = new User("Aidan9876", "1314831486", 2, 0, true, "groupOne");
        databaseUsers.insertIntoUsers(user);
        user = new User("Betty1248", "457436545", 3, 1, true, "groupTwo");
        databaseUsers.insertIntoUsers(user);
        user = new User("Callum2468", "845584644", 3, 0, false, "groupTwo");
        databaseUsers.insertIntoUsers(user);
        user = new User("Dorathy0369", "94648329837", 0, 0, true, "groupOne");
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
}
