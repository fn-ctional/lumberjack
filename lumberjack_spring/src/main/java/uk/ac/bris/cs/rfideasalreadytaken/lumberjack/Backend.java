package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.stereotype.Service;

import java.sql.*;

//Unite testing Database

//rename existing tables
//reset databases
//load test cases
//run function to test
//check resutl is now correct
//delete tables
//rename old tables

@Service
public class Backend implements FromCardReader{

    private User currentUser = new User("","",0,0,false);
    private boolean connected = false;
    private Connection conn = null;
    private Statement stmt = null;

    public ScanReturn scanRecieved(Scan scan) throws Exception{

        if(connectToDatabase()) {

            ScanReturn result;

            if (isValidUser(scan)) {

                result = userScanned(scan);
                if(result == ScanReturn.SUCCESSUSERLOADED) {

                    if (isValidDevice(scan)) {

                        result = deviceScanned(scan);
                        return result;
                    }
                    else {
                        return ScanReturn.FAILDEVICENOTRECOGNISED;
                    }
                }
                else {
                    return result;
                }
            }
            else {
                return ScanReturn.FAILUSERNOTRECOGNISED;
            }
        }
        else{
            return ScanReturn.ERRORCONNECTIONFAILED;
        }
    }

    private boolean connectToDatabase() throws Exception{

        if(connected == false){
            try {
                MysqlDataSource dataSource = new MysqlDataSource();

                dataSource.setServerName("129.150.119.251");
                dataSource.setPortNumber(3306);
                dataSource.setDatabaseName("LumberjackDatabase");
                dataSource.setUser("lumberjack");
                dataSource.setPassword("Lumberjack1#");
                dataSource.setConnectTimeout(5000);

                conn = dataSource.getConnection();

                stmt = conn.createStatement();

                connected = true;

            }catch(com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                return false;
            }
        }

        return true;
    }

    private ScanReturn userScanned(Scan scan) throws Exception{

        try {
            User loadedUser = loadUser(scan);
            currentUser = loadedUser;
            return ScanReturn.SUCCESSUSERLOADED;
        }
        catch(Exception e){
            return ScanReturn.ERRORUSERNOTLOADED;
        }
    }

    private ScanReturn deviceScanned(Scan scan) throws Exception{

        Device loadedDevice;

        try {
            loadedDevice = loadDevice(scan);
        }
        catch(Exception e){
            return ScanReturn.ERRORDEVICENOTLOADED;
        }

        try {
            if (canDeviceBeRemoved(loadedDevice)) {

                if (isDeviceCurrentlyOut(loadedDevice)) {

                    return returnDevice(loadedDevice, currentUser);
                }
                else {

                    if (isUserAtDeviceLimit(currentUser)) {
                        return ScanReturn.FAILUSERATDEVICELIMIT;
                    }
                    else if (!canUserRemoveDevices(currentUser)) {
                        return ScanReturn.FAILUSERNORPERMITTEDTOREMOVE;
                    }

                    return takeOutDevice(loadedDevice, currentUser);
                }
            }
            else {
                return ScanReturn.FAILDEVICEUNAVIALABLE;
            }
        }
        catch(Exception e){
            return ScanReturn.ERRORDEVICEHANDLINGFAILED;
        }
    }

    private User loadUserFromResultSet(ResultSet rs) throws Exception{
        if(rs.next()){
            User user = new User();
            user.setCanRemove(rs.getBoolean("CanRemove"));
            user.setDeviceLimit(rs.getInt("DeviceLimit"));
            user.setDevicesRemoved(rs.getInt("DevicesRemoved"));
            user.setId(rs.getString("id"));
            user.setScanValue(rs.getString("ScanValue"));
            return user;
        }
        return null;
    }

    private Device loadDeviceFromResultSet(ResultSet rs) throws Exception{
        if(rs.next()){
            Device device = new Device();
            device.setAvailable(rs.getBoolean("Available"));
            device.setCurrentlyAssigned(rs.getBoolean("CurrentlyAssigned"));
            device.setType(rs.getString("Type"));
            device.setId(rs.getString("id"));
            device.setScanValue(rs.getString("ScanValue"));
            return device;
        }
        return null;
    }

    private Assignment loadAssignmentFromResultSet(ResultSet rs) throws Exception{
        if(rs.next()){
            Assignment assignment = new Assignment();
            assignment.setDeviceID(rs.getString("DeviceID"));
            assignment.setUserID(rs.getString("UserID"));
            assignment.setDateAssigned(rs.getDate("DateAssigned"));
            assignment.setAssignmentID(rs.getString("id"));
            assignment.setTimeAssigned(rs.getTime("TimeAssigned"));
            return assignment;
        }
        return null;
    }

    private AssignmentHistory loadAssignmentHistoryFromResultSet(ResultSet rs) throws Exception{
        if(rs.next()){
            AssignmentHistory assignmentHistory = new AssignmentHistory();
            assignmentHistory.setAssignmentHistoryID(rs.getString("id"));
            assignmentHistory.setDeviceID(rs.getString("DeviceID"));
            assignmentHistory.setUserID(rs.getString("UserID"));
            assignmentHistory.setDateAssigned(rs.getDate("DateAssigned"));
            assignmentHistory.setTimeAssigned(rs.getTime("TimeAssigned"));
            assignmentHistory.setDateReturned(rs.getDate("DateReturned"));
            assignmentHistory.setTimeReturned(rs.getTime("TimeReturned"));
            assignmentHistory.setTimeRemovedFor(rs.getTime("TimeRemovedFor"));
            assignmentHistory.setReturnedSuccessfully(rs.getBoolean("ReturnedSuccessfully"));
            assignmentHistory.setReturnedByID(rs.getString("ReturnedBy"));
            return assignmentHistory;
        }
        return null;
    }

    //TODO switch scan value to be correct thing
    private boolean isValidUser(Scan scan) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM Users WHERE ScanValue = ?");
        stmt.setString(1, scan.getUser());
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    //TODO switch scan value to be correct thing
    private boolean isValidDevice(Scan scan) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM Devices WHERE ScanValue = ?");
        stmt.setString(1, scan.getDevice());
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    //TODO switch scan value to be correct thing
    private User loadUser(Scan scan) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE ScanValue = ?");
        stmt.setString(1, scan.getUser());
        ResultSet rs = stmt.executeQuery();
        User user = loadUserFromResultSet(rs);
        return user;
    }

    //TODO switch scan value to be correct thing
    private Device loadDevice(Scan scan) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Devices WHERE ScanValue = ?");
        stmt.setString(1, scan.getDevice());
        ResultSet rs = stmt.executeQuery();
        Device device = loadDeviceFromResultSet(rs);
        return device;
    }

    public boolean canUserRemoveDevices(User user) throws Exception{
        if(user.canRemove()){
            return true;
        }
        return false;
    }

    public boolean isUserAtDeviceLimit(User user){
        if(user.getDeviceLimit() == user.getDevicesRemoved()){
            return true;
        }
        return false;
    }

    private boolean canDeviceBeRemoved(Device device) throws Exception{
        if(device.isAvailable()){
            return true;
        }
        return false;
    }

    private boolean isDeviceCurrentlyOut(Device device) throws Exception{
        if(device.isCurrentlyAssigned()){
            return true;
        }
        return false;
    }

    private ScanReturn returnDevice(Device device, User returningUser) throws Exception{

        try{
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Assignments WHERE DeviceID = ?");
            stmt.setString(1, device.getId());
            ResultSet rs = stmt.executeQuery();
            Assignment assignment = loadAssignmentFromResultSet(rs);

            deleteFromAssignments(assignment.getId());

            insertIntoAssignmentHistory(assignment,returningUser.getId());

            int removed = returningUser.getDevicesRemoved()-1;
            PreparedStatement stmt3 = conn.prepareStatement("UPDATE Users SET DevicesRemoved = ? WHERE id = ?");
            stmt3.setInt(1, removed);
            stmt3.setString(2, assignment.getUserID());
            stmt3.execute();

            PreparedStatement stmt4 = conn.prepareStatement("UPDATE Devices SET CurrentlyAssigned = false WHERE id = ?");
            stmt4.setString(1, device.getId());
            stmt4.execute();

            if(!returningUser.getId().equals(assignment.getUserID())){

                takeOutDevice(device, returningUser);
                return ScanReturn.SUCCESSRETURNANDREMOVAL;
            }

            return ScanReturn.SUCCESSRETURN;
        }
        catch(Exception e) {
            return ScanReturn.ERRORRETURNFAILED;
        }
    }

    //TODO get current date and time and calculate time removed for
    public ScanReturn takeOutDevice(Device device, User user) throws Exception{
        try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE Devices SET CurrentlyAssigned = true WHERE id = ?");
            stmt.setString(1, device.getId());
            stmt.execute();

            int removed = user.getDevicesRemoved() + 1;
            PreparedStatement stmt3 = conn.prepareStatement("UPDATE Users SET DevicesRemoved = ? WHERE id = ?");
            stmt3.setInt(1, removed);
            stmt3.setString(2, user.getId());
            stmt3.execute();

            Date date = Date.valueOf("2018-02-10");
            Time time = Time.valueOf("14:45:20");
            Assignment assignment = new Assignment(device.getId(), user.getId(), date, time);
            insertIntoAssignments(assignment);
            return ScanReturn.SUCCESSREMOVAL;
        }
        catch(Exception e) {
            return ScanReturn.ERRORREMOVALFAILED;
        }
    }

    private boolean insertIntoRules(Rule rule) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Rules (id)" +
                "VALUES (?)");
        stmt.setString(1, rule.getId());
        stmt.execute();
        return true;
    }

    private boolean deleteFromRules(String ruleID) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Rules WHERE id = ?");
        stmt.setString(1, ruleID);
        stmt.execute();
        return true;
    }

    private boolean insertIntoDevices(Device device) throws Exception{
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

    void insertDevice(Device device){
        //boolean ignore = insertIntoDevices(device);
    }


    private boolean deleteFromDevices(String deviceID) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Devices WHERE id = ?");
        stmt.setString(1, deviceID);
        stmt.execute();
        return true;
    }

    void deleteDevice(Device device){
        //boolean ignore = deleteFromDevices(device.getId());
    }

    private boolean insertIntoUsers(User user) throws Exception{
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

    private boolean deleteFromUsers(String userID) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Users WHERE id = ?");
        stmt.setString(1, userID);
        stmt.execute();
        return true;
    }

    void deleteUser(User user) throws Exception{
        boolean ignore = deleteFromUsers(user.getId());
    }

    void insertUser(User user){
        //boolean ignore = insertIntoUsers(user);
    }

    void setDeviceType(Device device, String type){
        device.setType(type);
    }

    void setUserMaxDevices(User user, int max){
        user.setDeviceLimit(max);
    }

    private boolean insertIntoAssignments(Assignment assignment) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Assignments (DeviceID, UserID, DateAssigned, TimeAssigned)\n" +
                "VALUES (?,?,?,?)");
        stmt.setString(1, assignment.getDeviceID());
        stmt.setString(2, assignment.getUserID());
        stmt.setDate(3, assignment.getDateAssigned());
        stmt.setTime(4, assignment.getTimeAssigned());
        stmt.execute();
        return true;
    }

    private boolean deleteFromAssignments(String assignmentID) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Assignments WHERE id = ?");
        stmt.setString(1, assignmentID);
        stmt.execute();
        return true;
    }

    //TODO get current date and time and calculate time removed for
    private boolean insertIntoAssignmentHistory(Assignment assignment, String returningUserID) throws Exception{

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

    private boolean deleteFromAssignmentHistory(String assignmentHistoryID) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM AssignmentHistory WHERE id = ?");
        stmt.setString(1, assignmentHistoryID);
        stmt.execute();
        return true;
    }

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
                "\nPRIMARY KEY (id)," +
                "\nCONSTRAINT FOREIGN KEY (DeviceID) REFERENCES Devices(id)," +
                "\nCONSTRAINT FOREIGN KEY (UserID) REFERENCES Users(id));");

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
}
