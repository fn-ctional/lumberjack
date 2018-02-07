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

            //stmt.execute("CREATE TABLE IF NOT EXISTS USERS (\nID integer PRIMARY KEY);");
            //stmt.execute("DROP TABLE IF EXISTS USERS");
            //rs = stmt.executeQuery("SELECT ID FROM USERS");

            connected = true;
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
        //Database Stuff
        return true;
    }

    private boolean isValidDevice(Scan scan){
        //Database Stuff
        return true;
    }

    private User loadUser(Scan scan){
        //Database Stuff
        return new User();
    }

    private Device loadDevice(Scan scan){
        //Database Stuff
        return new Device();
    }

    private boolean canUserRemoveDevices(User user){
        //Database Stuff
        return true;
    }

    private boolean canDeviceBeRemoved(Device device){
        //Database Stuff
        return true;
    }

    private boolean isDeviceCurrentlyOut(Device device){
        //Database Stuff
        return true;
    }

    private boolean returnDevice(Device device, User user){
        //If user is not the one who took out the device rembe to take out the device for the new user
        //Database Stuff
        return true;
    }

    private boolean takeOutDevice(Device device, User user){
        //Database Stuff
        return true;
    }

    private boolean addTakeOutToHistory(DeviceAssignment assignment){
        //Database Stuff
        return true;
    }
}
