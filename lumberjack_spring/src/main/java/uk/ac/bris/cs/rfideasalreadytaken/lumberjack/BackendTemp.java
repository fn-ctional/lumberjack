package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class BackendTemp implements FromCardReader{

    private boolean userLoaded = false;
    private User currentUser = new User();
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

    private String userScanned(Scan scan){

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

    private String deviceScanned(Scan scan){

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

    private boolean isValidUser(Scan scan){
        //Query Users to see whether or not the scan is a user
        return true;
    }

    private boolean isValidDevice(Scan scan){
        //Query Devices to see whether or not the scan is a user
        return true;
    }

    private User loadUser(Scan scan){
        //Query Users to find the user corresponding to this scan and return them
        return new User();
    }

    private Device loadDevice(Scan scan){
        //Query Devices to find the device corresponding to this scan and return it
        return new Device();
    }

    private boolean canUserRemoveDevices(User user){
        //Query Users to see if the user is at their device limit or otherwise cannot remove devices and return result
        return true;
    }

    private boolean canDeviceBeRemoved(Device device){
        //Query Devices to see if the device is available and return result
        return true;
    }

    private boolean isDeviceCurrentlyOut(Device device){
        //Query Devices to see if the device is currently taken out and return result
        return true;
    }

    private boolean returnDevice(Device device, User user){
        //Load and then delete from Assignments the record of this device being removed
        //Check if the user who returned the device correlates to the one who removed it
        //Assume no user is the old user as they dont need to scan their UCard to return
        //if yes
            //Add to AssignmentHistory a record of the device being removed and returned by that user using addTakeOutToHistory
            //Update Users so that the user has removed 1 less device than before
            //Update Devices so that that device is no longer recorded as removed
        //if no
            //Add to AssignmentHistory a record of the device being removed by the old user and returned by the new user usign addTakeOutToHistory()
            //Update Users so that the previous user has removed 1 less device than before
            //Update Devices so the device is no longer recorded as being removed (takeOutDevice() will reverse this but it should be done in case takeOutDevice() fails)
            //Use takeOutDevice() to create a record of the device bign taken out by the new user
        return true;
    }

    private boolean takeOutDevice(Device device, User user){
        //Update Devices so the device is recorded as being removed
        //Update Users so that the user has removed 1 more device than before
        //Update Assignments so that there is a new record of the new user removing the device
        return true;
    }

    private boolean addTakeOutToHistory(DeviceAssignment assignment, User returningUser){
        //Add to AssignmentHistory a record of the device being removed and returned by that user
        return true;
    }

    public boolean resetDatabases() throws Exception{

        connectToDatabase();

        stmt.execute("DROP TABLE IF EXISTS AssignmentHistory");
        stmt.execute("DROP TABLE IF EXISTS Assignments");
        stmt.execute("DROP TABLE IF EXISTS Users");
        stmt.execute("DROP TABLE IF EXISTS Devices");

        stmt.execute("CREATE TABLE IF NOT EXISTS Users (" +
                "\nUserID varchar(100) NOT NULL," +
                "\nDeviceLimit int," +
                "\nDevicesRemoved int," +
                "\nCanRemove bit," +
                "\nPRIMARY KEY (UserID));");

        stmt.execute("CREATE TABLE IF NOT EXISTS Devices (" +
                "\nDeviceID varchar(100) NOT NULL," +
                "\nType varchar(100)," +
                "\nAvailable bit," +
                "\nCurrentlyAssigned bit," +
                "\nPRIMARY KEY (DeviceID));");

        stmt.execute("CREATE TABLE IF NOT EXISTS Assignments (" +
                "\nAssignmentID varchar(100) NOT NULL,\n" +
                "\nDeviceID varchar(100) NOT NULL," +
                "\nUserID varchar(100) NOT NULL," +
                "\nDateAssigned date," +
                "\nTimeAssigned time," +
                "\nPRIMARY KEY (AssignmentID)," +
                "\nCONSTRAINT FOREIGN KEY (DeviceID) REFERENCES Devices(DeviceID)," +
                "\nCONSTRAINT FOREIGN KEY (UserID) REFERENCES Users(UserID));");

        stmt.execute("CREATE TABLE IF NOT EXISTS AssignmentHistory (" +
                "\nAssignmentHistoryID varchar(100) NOT NULL,\n" +
                "\nDeviceID varchar(100) NOT NULL," +
                "\nUserID varchar(100) NOT NULL," +
                "\nDateAssigned date," +
                "\nTimeAssigned time," +
                "\nDateReturned date," +
                "\nTimeReturned time," +
                "\nTiemRemovedFor time," +
                "\nReturnedSuccessfully bit," +
                "\nReturnedBy varchar(100) NOT NULL," +
                "\nPRIMARY KEY (AssignmentHistoryID)," +
                "\nCONSTRAINT FOREIGN KEY (DeviceID) REFERENCES Devices(DeviceID)," +
                "\nCONSTRAINT FOREIGN KEY (UserID) REFERENCES Users(UserID));");

        return true;
    }
}
