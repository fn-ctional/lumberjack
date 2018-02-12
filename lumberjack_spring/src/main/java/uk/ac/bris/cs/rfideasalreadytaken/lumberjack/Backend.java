package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

@Service
public class Backend implements FromCardReader{

    private boolean userLoaded = false;
    private User currentUser = new User("","",0,0,false);
    private boolean connected = false;
    private Connection conn = null;
    private Statement stmt = null;

    public String scanRecieved(Scan scan) throws Exception{

        if(connectToDatabase()) {

            if (isValidUser(scan)) {
                return userScanned(scan);
            }
            else if (isValidDevice(scan)) {
                return deviceScanned(scan);
            }

            return "Scan not recognised";
        }
        else{
            return "Failed to connect to Database";
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

    private String userScanned(Scan scan) throws Exception{

        User loadedUser = loadUser(scan);

        if(canUserRemoveDevices(loadedUser)){
            currentUser = loadedUser;
            userLoaded = true;
            return "User loaded sucessfully";
        }
        else{
            return "User cannot remove devices";
        }
    }

    private String deviceScanned(Scan scan) throws Exception{

        Device loadedDevice = loadDevice(scan);

        if(canDeviceBeRemoved(loadedDevice)){

            if(!userLoaded){
                return "No user has been scanned";
            }

            if(isDeviceCurrentlyOut(loadedDevice)){

                if(returnDevice(loadedDevice, currentUser)){
                    return "Device returned successfully";
                }
                else{
                    return "Error returning device";
                }
            }
            else{
                if(takeOutDevice(loadedDevice, currentUser)){
                    return "Device taken out successfully";
                }
                else{
                    return "Error taking out device";
                }
            }
        }
        else{
            return "Device cannot be taken out";
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
        stmt.setString(1, scan.getUserID());
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    //TODO switch scan value to be correct thing
    private boolean isValidDevice(Scan scan) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM Devices WHERE ScanValue = ?");
        stmt.setString(1, scan.getUserID());
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    private User loadUser(Scan scan) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT id, ScanValue, DeviceLimit, DevicesRemoved, CanRemove FROM Users WHERE ScanValue = ?");
        stmt.setString(1, scan.getUserID());
        ResultSet rs = stmt.executeQuery();
        User user = loadUserFromResultSet(rs);
        return user;
    }

    private Device loadDevice(Scan scan) throws Exception{
        //Query Devices to find the device corresponding to this scan and return it
        return null;
    }

    public boolean canUserRemoveDevices(User user) throws Exception{
        if(user.canRemove()){
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

    private boolean returnDevice(Device device, User user) throws Exception{
        //Load and then delete from Assignments the record of this device being removed
        //Check if the user who returned the device correlates to the one who removed it
        //Assume no user is the old user as they dont need to scan their UCard to return
        //if yes
            //Add to AssignmentHistory a record of the device being removed and returned by that user using addTakeOutToHistory
            //Update Users so that the user has removed 1 less device than before
            //Update Devices so that that device is no longer recorded as removed
        //if no
            //Add to AssignmentHistory a record of the device being removed by the old user and returned by the new user usign insertIntoAssignmentHistory()
            //Update Users so that the previous user has removed 1 less device than before
            //Update Devices so the device is no longer recorded as being removed (takeOutDevice() will reverse this but it should be done in case takeOutDevice() fails)
            //Use takeOutDevice() to create a record of the device bign taken out by the new user
        return true;
    }

    private boolean takeOutDevice(Device device, User user) throws Exception{
        //Update Devices so the device is recorded as being removed
        //Update Users so that the user has removed 1 more device than before
        //Update Assignments so that there is a new record of the new user removing the device
        return true;
    }

    public boolean insertIntoDevices(Device device) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Devices (id, scanValue, Type, Available, CurrentlyAssigned) VALUES (?,?,?,?,?)");
        stmt.setString(1, device.getId());
        stmt.setString(2, device.getScanValue());
        stmt.setString(3, device.getType());
        stmt.setBoolean(4, device.isAvailable());
        stmt.setBoolean(5, device.isCurrentlyAssigned());
        stmt.execute();
        return true;
    }

    private boolean insertIntoUsers(User user) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Users (id, scanValue, DeviceLimit, DevicesRemoved, CanRemove)" +
                "VALUES (?,?,?,?,?)");
        stmt.setString(1, user.getId());
        stmt.setString(2, user.getScanValue());
        stmt.setInt(3, user.getDeviceLimit());
        stmt.setInt(4, user.getDevicesRemoved());
        stmt.setBoolean(5, user.canRemove());
        stmt.execute();
        return true;
    }

    private boolean insertIntoAssignments(Assignment assignment) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Assignments (id, DeviceID, UserID, DateAssigned, TimeAssigned)\n" +
                "VALUES (?,?,?,?,?)");
        stmt.setString(1, assignment.getId());
        stmt.setString(2, assignment.getDeviceID());
        stmt.setString(3, assignment.getUserID());
        stmt.setDate(4, assignment.getDateAssigned());
        stmt.setTime(5, assignment.getTimeAssigned());
        stmt.execute();
        return true;
    }

    //TODO get current date and time and calculate time removed for
    private boolean insertIntoAssignmentHistory(Assignment assignment, String returningUserID) throws Exception{

        boolean returnedSuccessfully = false;
        if(assignment.getUserID() == returningUserID){
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
        return true;
    }

    public boolean resetDatabase() throws Exception{

        connectToDatabase();

        stmt.execute("DROP TABLE IF EXISTS AssignmentHistory");
        stmt.execute("DROP TABLE IF EXISTS Assignments");
        stmt.execute("DROP TABLE IF EXISTS Users");
        stmt.execute("DROP TABLE IF EXISTS Devices");

        stmt.execute("CREATE TABLE IF NOT EXISTS Users (" +
                "\nid varchar(100) NOT NULL," +
                "\nScanValue varchar(100) NOT NULL UNIQUE," +
                "\nDeviceLimit int," +
                "\nDevicesRemoved int," +
                "\nCanRemove bit," +
                "\nPRIMARY KEY (id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS Devices (" +
                "\nid varchar(100) NOT NULL," +
                "\nScanValue varchar(100) NOT NULL UNIQUE," +
                "\nType varchar(100)," +
                "\nAvailable bit," +
                "\nCurrentlyAssigned bit," +
                "\nPRIMARY KEY (id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS Assignments (" +
                "\nid varchar(100) NOT NULL,\n" +
                "\nDeviceID varchar(100) NOT NULL," +
                "\nUserID varchar(100) NOT NULL," +
                "\nDateAssigned DATE," +
                "\nTimeAssigned TIME," +
                "\nPRIMARY KEY (id)," +
                "\nCONSTRAINT FOREIGN KEY (DeviceID) REFERENCES Devices(id)," +
                "\nCONSTRAINT FOREIGN KEY (UserID) REFERENCES Users(id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS AssignmentHistory (" +
                "\nid int NOT NULL AUTO_INCREMENT,\n" +
                "\nDeviceID varchar(100) NOT NULL," +
                "\nUserID varchar(100) NOT NULL," +
                "\nDateAssigned DATE," +
                "\nTimeAssigned TIME," +
                "\nDateReturned DATE," +
                "\nTimeReturned TIME," +
                "\nTimeRemovedFor time," +
                "\nReturnedSuccessfully bit," +
                "\nReturnedBy varchar(100) NOT NULL," +
                "\nPRIMARY KEY (id)," +
                "\nCONSTRAINT FOREIGN KEY (DeviceID) REFERENCES Devices(id)," +
                "\nCONSTRAINT FOREIGN KEY (UserID) REFERENCES Users(id));");

        return true;
    }

    public boolean insertTestCases() throws Exception{

        resetDatabase();

        User user = new User("Aidan9876", "scanValueU1", 2, 0, true);
        insertIntoUsers(user);
        user = new User("Betty1248", "scanValueU2", 1, 1, true);
        insertIntoUsers(user);
        user = new User("Callum2468", "scanValueU3", 3, 0, false);
        insertIntoUsers(user);
        user = new User("Dorathy0369", "scanValueU4", 1, 0, true);
        insertIntoUsers(user);

        Device device = new Device("laptop01", "scanValueD1", "laptop", true, false);
        insertIntoDevices(device);
        device = new Device("laptop02", "scanValueD2", "laptop", true, true);
        insertIntoDevices(device);
        device = new Device("laptop03", "scanValueD3", "laptop", false, false);
        insertIntoDevices(device);
        device = new Device("camera01", "scanValueD4", "camera", true, false);
        insertIntoDevices(device);

        java.sql.Date date = java.sql.Date.valueOf("2018-02-10");
        java.sql.Time time = java.sql.Time.valueOf("14:45:20");

        Assignment assignment = new Assignment("002", "laptop02", "Betty1248", date,time);
        insertIntoAssignments(assignment);

        date = java.sql.Date.valueOf("2018-02-09");
        time = java.sql.Time.valueOf("09:32:13");

        assignment = new Assignment("001", "laptop01", "Callum2468", date,time);
        insertIntoAssignmentHistory(assignment, "Aidan9876");

        return true;
    }
}
