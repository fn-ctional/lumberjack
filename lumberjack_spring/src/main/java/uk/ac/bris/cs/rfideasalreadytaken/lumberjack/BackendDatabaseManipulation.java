package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Assignment;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Rule;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;

import java.sql.PreparedStatement;

public class BackendDatabaseManipulation extends BackendDatabaseConnection{

    public boolean resetDatabase() throws Exception{

        connectToDatabase();

        stmt.execute("DROP TABLE IF EXISTS AssignmentHistory");
        stmt.execute("DROP TABLE IF EXISTS Assignments");
        stmt.execute("DROP TABLE IF EXISTS Users");
        stmt.execute("DROP TABLE IF EXISTS Devices");
        stmt.execute("DROP TABLE IF EXISTS Rules");

        stmt.execute("CREATE TABLE IF NOT EXISTS Rules (" +
                "\nid varchar(100)," +
                "\nPRIMARY KEY (id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS Users (" +
                "\nid varchar(100)," +
                "\nScanValue varchar(100) NOT NULL UNIQUE," +
                "\nDeviceLimit int," +
                "\nDevicesRemoved int," +
                "\nCanRemove bit," +
                "\nPRIMARY KEY (id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS Devices (" +
                "\nid varchar(100)," +
                "\nScanValue varchar(100) NOT NULL UNIQUE," +
                "\nType varchar(100)," +
                "\nAvailable bit," +
                "\nCurrentlyAssigned bit," +
                "\nRuleID varchar(100)," +
                "\nCONSTRAINT FOREIGN KEY (RuleID) REFERENCES Rules(id)," +
                "\nPRIMARY KEY (id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS Assignments (" +
                "\nid int AUTO_INCREMENT,\n" +
                "\nDeviceID varchar(100) NOT NULL," +
                "\nUserID varchar(100) NOT NULL," +
                "\nDateAssigned DATE," +
                "\nTimeAssigned TIME," +
                "\nPRIMARY KEY (id)," +
                "\nCONSTRAINT FOREIGN KEY (DeviceID) REFERENCES Devices(id)," +
                "\nCONSTRAINT FOREIGN KEY (UserID) REFERENCES Users(id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS AssignmentHistory (" +
                "\nid int AUTO_INCREMENT,\n" +
                "\nDeviceID varchar(100) NOT NULL," +
                "\nUserID varchar(100) NOT NULL," +
                "\nDateAssigned DATE," +
                "\nTimeAssigned TIME," +
                "\nDateReturned DATE," +
                "\nTimeReturned TIME," +
                "\nTimeRemovedFor TIME," +
                "\nReturnedSuccessfully bit," +
                "\nReturnedBy varchar(100) NOT NULL," +
                "\nPRIMARY KEY (id))");

        return true;
    }

    public boolean insertTestCases() throws Exception{

        resetDatabase();

        Rule rule = new Rule("ruleSet1");
        insertIntoRules(rule);
        rule = new Rule("ruleSet2");
        insertIntoRules(rule);

        User user = new User("Aidan9876", "1314831486", 2, 0, true);
        insertIntoUsers(user);
        user = new User("Betty1248", "457436545", 1, 1, true);
        insertIntoUsers(user);
        user = new User("Callum2468", "845584644", 3, 0, false);
        insertIntoUsers(user);
        user = new User("Dorathy0369", "94648329837", 1, 0, true);
        insertIntoUsers(user);

        Device device = new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1");
        insertIntoDevices(device);
        device = new Device("laptop02", "23482364326842334", "laptop", true, true, "ruleSet2");
        insertIntoDevices(device);
        device = new Device("laptop03", "93482364723648725", "laptop", false, false, "ruleSet1");
        insertIntoDevices(device);
        device = new Device("camera01", "03457237295732925", "camera", true, false, "ruleSet2");
        insertIntoDevices(device);

        java.sql.Date date = java.sql.Date.valueOf("2018-02-10");
        java.sql.Time time = java.sql.Time.valueOf("14:45:20");

        Assignment assignment = new Assignment("laptop02", "Betty1248", date,time);
        insertIntoAssignments(assignment);

        date = java.sql.Date.valueOf("2018-02-09");
        time = java.sql.Time.valueOf("09:32:13");

        assignment = new Assignment("laptop01", "Callum2468", date,time);
        insertIntoAssignmentHistory(assignment, "Aidan9876");

        return true;
    }

    protected boolean insertIntoRules(Rule rule) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Rules (id)" +
                "VALUES (?)");
        stmt.setString(1, rule.getId());
        stmt.execute();
        return true;
    }

    protected boolean deleteFromRules(String ruleID) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Rules WHERE id = ?");
        stmt.setString(1, ruleID);
        stmt.execute();
        return true;
    }

    protected boolean insertIntoDevices(Device device) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Devices (id, ScanValue, Type, Available, CurrentlyAssigned, RuleID) VALUES (?,?,?,?,?,?)");
        stmt.setString(1, device.getId());
        stmt.setString(2, device.getScanValue());
        stmt.setString(3, device.getType());
        stmt.setBoolean(4, device.isAvailable());
        stmt.setBoolean(5, device.isCurrentlyAssigned());
        stmt.setString(6, device.getRuleID());
        stmt.execute();
        return true;
    }

    protected boolean deleteFromDevices(String deviceID) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Devices WHERE id = ?");
        stmt.setString(1, deviceID);
        stmt.execute();
        return true;
    }

    protected boolean insertIntoUsers(User user) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Users (id, ScanValue, DeviceLimit, DevicesRemoved, CanRemove)" +
                "VALUES (?,?,?,?,?)");
        stmt.setString(1, user.getId());
        stmt.setString(2, user.getScanValue());
        stmt.setInt(3, user.getDeviceLimit());
        stmt.setInt(4, user.getDevicesRemoved());
        stmt.setBoolean(5, user.canRemove());
        stmt.execute();
        return true;
    }

    protected boolean updateUser(String userID, User user) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("UPDATE Users SET id = ?, ScanValue = ?, DeviceLimit = ?, DevicesRemoved = ?, CanRemove = ? " +
                "WHERE id = ?");
        stmt.setString(1, user.getId());
        stmt.setString(2, user.getScanValue());
        stmt.setInt(3, user.getDeviceLimit());
        stmt.setInt(4, user.getDevicesRemoved());
        stmt.setBoolean(5, user.canRemove());
        stmt.setString(6, userID);
        stmt.execute();
        return true;
    }

    protected boolean deleteFromUsers(String userID) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Users WHERE id = ?");
        stmt.setString(1, userID);
        stmt.execute();
        return true;
    }

    protected boolean insertIntoAssignments(Assignment assignment) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Assignments (DeviceID, UserID, DateAssigned, TimeAssigned)\n" +
                "VALUES (?,?,?,?)");
        stmt.setString(1, assignment.getDeviceID());
        stmt.setString(2, assignment.getUserID());
        stmt.setDate(3, assignment.getDateAssigned());
        stmt.setTime(4, assignment.getTimeAssigned());
        stmt.execute();
        return true;
    }

    protected boolean deleteFromAssignments(String assignmentID) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Assignments WHERE id = ?");
        stmt.setString(1, assignmentID);
        stmt.execute();
        return true;
    }

    //TODO get current date and time and calculate time removed for
    protected boolean insertIntoAssignmentHistory(Assignment assignment, String returningUserID) throws Exception{

        boolean returnedSuccessfully = false;
        if(assignment.getUserID().equals(returningUserID)){
            returnedSuccessfully = true;
        }

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO AssignmentHistory (DeviceID, UserID, DateAssigned, TimeAssigned, DateReturned, TimeReturned, TimeRemovedFor, ReturnedSuccessfully, ReturnedBy)" +
                "VALUES (?,?,?,?,?,?,?,?,?)");

        stmt.setString(1, assignment.getDeviceID());
        stmt.setString(2, assignment.getUserID());
        stmt.setDate(3, assignment.getDateAssigned());
        stmt.setTime(4, assignment.getTimeAssigned());
        stmt.setDate(5, assignment.getDateAssigned());
        stmt.setTime(6, assignment.getTimeAssigned());
        stmt.setTime(7, assignment.getTimeAssigned());
        stmt.setBoolean(8, returnedSuccessfully);
        stmt.setString(9,returningUserID);

        stmt.execute();
        return true;
    }

    protected boolean deleteFromAssignmentHistory(String assignmentHistoryID) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM AssignmentHistory WHERE id = ?");
        stmt.setString(1, assignmentHistoryID);
        stmt.execute();
        return true;
    }
}
