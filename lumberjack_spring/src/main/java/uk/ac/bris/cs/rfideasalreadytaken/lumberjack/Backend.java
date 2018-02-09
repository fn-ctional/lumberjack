package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Backend implements FromCardReader{

    private boolean userLoaded = false;
    private User currentUser = new User("","",0,0,0);
    private boolean connected = false;
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;

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

    //TODO switch scan value to be correct thing
    private boolean isValidUser(Scan scan) throws Exception{
        rs = stmt.executeQuery("SELECT id FROM Users WHERE id = \"" + scan.getUserID() + "\"");
        return rs.next();
    }

    //TODO switch scan value to be correct thing
    private boolean isValidDevice(Scan scan) throws Exception{
        rs = stmt.executeQuery("SELECT id FROM Devices WHERE id = \"" + scan.getUserID() + "\"");
        return rs.next();
    }

    private User loadUser(Scan scan) throws Exception{
        //Query Users to find the user corresponding to this scan and return them
        return null;
    }

    private Device loadDevice(Scan scan) throws Exception{
        //Query Devices to find the device corresponding to this scan and return it
        return null;
    }

    public boolean canUserRemoveDevices(User user) throws Exception{
        if(user.canRemove() > 0){
            return true;
        }
        return false;
    }

    private boolean canDeviceBeRemoved(Device device) throws Exception{
        if(device.isAvailable() > 0){
            return true;
        }
        return false;
    }

    private boolean isDeviceCurrentlyOut(Device device) throws Exception{
        if(device.isCurrentlyAssigned() > 0){
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

    private boolean insertIntoDevices(Device device) throws Exception{
        stmt.execute("INSERT INTO Devices (id, scanValue, Type, Available, CurrentlyAssigned)\n" +
                "VALUES (\"" + device.getId() + "\", \"" + device.getScanValue() + "\", \"" + device.getType() +
                "\"," + String.valueOf(device.isAvailable()) + "," + String.valueOf(device.isCurrentlyAssigned()) + ")");
        return true;
    }

    private boolean insertIntoUsers(User user) throws Exception{
        stmt.execute("INSERT INTO Users (id, scanValue, DeviceLimit, DevicesRemoved, CanRemove)\n" +
                "VALUES (\"" + user.getId() + "\", \"" + user.getScanValue() + "\", " + String.valueOf(user.getDeviceLimit()) +
                "," + String.valueOf(user.getDevicesRemoved()) + "," + String.valueOf(user.canRemove()) + ")");
        return true;
    }

    private boolean insertIntoAssignments(Assignment assignment) throws Exception{
        stmt.execute("INSERT INTO Assignments (id, DeviceID, UserID, DateAssigned, TimeAssigned)\n" +
                "VALUES (\"" + assignment.getId() + "\", \"" + assignment.getDeviceID() + "\", \"" + assignment.getUserID() +
                "\", '" + assignment.getDateAssigned().toString() + "' , '" + assignment.getTimeAssigned().toString() + "')");
        return true;
    }

    //TODO get current date and time and calculate time removed for
    private boolean insertIntoAssignmentHistory(Assignment assignment, String returningUserID) throws Exception{

        int returnedSuccessfully = 0;
        if(assignment.getUserID() == returningUserID){
            returnedSuccessfully = 1;
        }

        stmt.execute("INSERT INTO AssignmentHistory (DeviceID, UserID, DateAssigned, TimeAssigned, DateReturned, TimeReturned, TimeRemovedFor, ReturnedSuccessfully, ReturnedBy)\n" +
                "VALUES (\"" + assignment.getDeviceID() + "\", \"" + assignment.getUserID() +
                "\", '" + assignment.getDateAssigned().toString() + "' , '" + assignment.getTimeAssigned().toString() +
                "', '" + assignment.getDateAssigned().toString() + "' , '" + assignment.getTimeAssigned().toString() +
                "', '" + assignment.getTimeAssigned().toString() + "' , " + returnedSuccessfully +
                ",\""+ returningUserID + "\")");
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

        User user = new User("Aidan9876", "scanValueU1", 2, 0, 1);
        insertIntoUsers(user);
        user = new User("Betty1248", "scanValueU2", 1, 1, 1);
        insertIntoUsers(user);
        user = new User("Callum2468", "scanValueU3", 3, 0, 0);
        insertIntoUsers(user);
        user = new User("Dorathy0369", "scanValueU4", 1, 0, 1);
        insertIntoUsers(user);

        Device device = new Device("laptop01", "scanValueD1", "laptop", 1, 0);
        insertIntoDevices(device);
        device = new Device("laptop02", "scanValueD2", "laptop", 1, 1);
        insertIntoDevices(device);
        device = new Device("laptop03", "scanValueD3", "laptop", 0, 0);
        insertIntoDevices(device);
        device = new Device("camera01", "scanValueD4", "camera", 1, 0);
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
