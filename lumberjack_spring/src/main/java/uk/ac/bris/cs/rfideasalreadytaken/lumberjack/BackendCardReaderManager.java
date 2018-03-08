package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.*;

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
public class BackendCardReaderManager extends BackendDatabaseLoading implements FromCardReader{

    private User currentUser = new User("","",0,0,false,"");

    public ScanReturn scanReceived(ScanDTO scanDTO) throws Exception{

        if(connectToDatabase()) {

            ScanReturn result;

            if (isValidUser(scanDTO)) {

                result = userScanned(scanDTO);
                if(result == ScanReturn.SUCCESSUSERLOADED) {

                    if (isValidDevice(scanDTO)) {

                        result = deviceScanned(scanDTO);
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

    private ScanReturn userScanned(ScanDTO scanDTO) throws Exception{

        try {
            User loadedUser = loadUser(scanDTO);
            currentUser = loadedUser;
            return ScanReturn.SUCCESSUSERLOADED;
        }
        catch(Exception e){
            return ScanReturn.ERRORUSERNOTLOADED;
        }
    }

    private ScanReturn deviceScanned(ScanDTO scanDTO) throws Exception{

        Device loadedDevice;

        try {
            loadedDevice = loadDevice(scanDTO);
        }
        catch(Exception e){
            return ScanReturn.ERRORDEVICENOTLOADED;
        }

        try {
            if (isDeviceCurrentlyOut(loadedDevice)) {

                return returnDevice(loadedDevice, currentUser);
            }
            else {

                ScanReturn value = canDeviceBeRemoved(currentUser, loadedDevice);

                if(value != ScanReturn.SUCCESSSCANREMOVE){
                    return value;
                }

                return takeOutDevice(loadedDevice, currentUser);
            }
        }
        catch(Exception e){
            return ScanReturn.ERRORDEVICEHANDLINGFAILED;
        }
    }

    //TODO get current date and time and calculate time removed for
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

                ScanReturn value = canDeviceBeRemoved(returningUser, device);

                if(value != ScanReturn.SUCCESSSCANREMOVE){
                    return ScanReturn.SUCCESSRETURN;
                }

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
    private ScanReturn takeOutDevice(Device device, User user) throws Exception{
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

    private ScanReturn canDeviceBeRemoved(User user, Device device) throws Exception{

        if (isUserAtDeviceLimit(user)) {
            return ScanReturn.FAILUSERATDEVICELIMIT;
        }
        else if (!canUserRemoveDevices(user)) {
            return ScanReturn.FAILUSERNORPERMITTEDTOREMOVE;
        }
        else if(!canDeviceBeRemoved(device)){
            return ScanReturn.FAILDEVICEUNAVIALABLE;
        }
        else if(!canUserGroupRemoveDevice(device, user)){
            return ScanReturn.FAILUSERGROUPRULESETNOTCOMPATABLE;
        }

        return ScanReturn.SUCCESSSCANREMOVE;
    }



}
