package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader.data.ScanDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Assignment;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Service
public class CardReaderBackend implements FromCardReader {

    @Autowired
    private DatabaseConnection databaseConnection;

    @Autowired
    private DatabaseUsers databaseUsers;

    @Autowired
    private DatabaseDevices databaseDevices;

    @Autowired
    private DatabaseAssignments databaseAssignments;

    @Autowired
    private DatabaseUserGroups databaseUserGroups;

    private User currentUser = new User("", "", 0, 0, false, "");

    public ScanReturn scanReceived(ScanDTO scanDTO) throws SQLException {

        ScanReturn result;

        if (databaseUsers.isValidUser(scanDTO)) {

            result = handleUser(scanDTO);
            if (result == ScanReturn.SUCCESSUSERLOADED) {

                if (databaseDevices.isValidDevice(scanDTO)) {

                    result = handleDevice(scanDTO);
                    return result;
                } else {
                    return ScanReturn.FAILDEVICENOTRECOGNISED;
                }
            } else {
                return result;
            }
        } else {
            return ScanReturn.FAILUSERNOTRECOGNISED;
        }
    }

    private ScanReturn handleUser(ScanDTO scanDTO) throws SQLException {

        try {
            currentUser = databaseUsers.loadUser(scanDTO);
            return ScanReturn.SUCCESSUSERLOADED;
        } catch (Exception e) {
            return ScanReturn.ERRORUSERNOTLOADED;
        }
    }

    private ScanReturn handleDevice(ScanDTO scanDTO) throws SQLException {

        Device loadedDevice;

        try {
            loadedDevice = databaseDevices.loadDevice(scanDTO);
        } catch (Exception e) {
            return ScanReturn.ERRORDEVICENOTLOADED;
        }

        try {
            if (databaseDevices.isDeviceCurrentlyOut(loadedDevice)) {

                return returnDevice(loadedDevice, currentUser);
            } else {

                ScanReturn value = canDeviceBeRemoved(currentUser, loadedDevice);

                if (value != ScanReturn.SUCCESSSCANREMOVE) {
                    return value;
                }

                return takeOutDevice(loadedDevice, currentUser);
            }
        } catch (Exception e) {
            return ScanReturn.ERRORDEVICEHANDLINGFAILED;
        }
    }

    private ScanReturn returnDevice(Device device, User returningUser) throws SQLException {

        try {
            PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM Assignments WHERE DeviceID = ?");
            stmt.setString(1, device.getId());
            ResultSet rs = stmt.executeQuery();
            Assignment assignment = databaseAssignments.loadAssignmentFromResultSet(rs);

            databaseAssignments.deleteFromAssignments(assignment.getId());

            boolean returnedOnTime = databaseAssignments.checkIfReturnedOnTime(assignment);

            databaseAssignments.insertIntoAssignmentHistory(assignment, returningUser.getId(), returnedOnTime);

            int removed = databaseUsers.loadUser(assignment.getUserID()).getDevicesRemoved() - 1;
            PreparedStatement stmt3 = databaseConnection.getConnection().prepareStatement("UPDATE Users SET DevicesRemoved = ? WHERE id = ?");
            stmt3.setInt(1, removed);
            stmt3.setString(2, assignment.getUserID());
            stmt3.execute();

            PreparedStatement stmt4 = databaseConnection.getConnection().prepareStatement("UPDATE Devices SET CurrentlyAssigned = false WHERE id = ?");
            stmt4.setString(1, device.getId());
            stmt4.execute();

            if (!returningUser.getId().equals(assignment.getUserID())) {

                ScanReturn value = canDeviceBeRemoved(returningUser, device);

                if (value != ScanReturn.SUCCESSSCANREMOVE) {
                    return ScanReturn.SUCCESSRETURN;
                }

                takeOutDevice(device, returningUser);
                return ScanReturn.SUCCESSRETURNANDREMOVAL;
            }

            return ScanReturn.SUCCESSRETURN;
        } catch (Exception e) {
            return ScanReturn.ERRORRETURNFAILED;
        }
    }

    private ScanReturn takeOutDevice(Device device, User user) throws SQLException {
        try {
            PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("UPDATE Devices SET CurrentlyAssigned = true WHERE id = ?");
            stmt.setString(1, device.getId());
            stmt.execute();

            int removed = user.getDevicesRemoved() + 1;
            PreparedStatement stmt3 = databaseConnection.getConnection().prepareStatement("UPDATE Users SET DevicesRemoved = ? WHERE id = ?");
            stmt3.setInt(1, removed);
            stmt3.setString(2, user.getId());
            stmt3.execute();

            Assignment assignment = new Assignment(device.getId(), user.getId());
            databaseAssignments.insertIntoAssignments(assignment);
            return ScanReturn.SUCCESSREMOVAL;
        } catch (Exception e) {
            return ScanReturn.ERRORREMOVALFAILED;
        }
    }

    private ScanReturn canDeviceBeRemoved(User user, Device device) throws SQLException {

        if (databaseUsers.isUserAtDeviceLimit(user)) {
            return ScanReturn.FAILUSERATDEVICELIMIT;
        } else if (!databaseUsers.canUserRemoveDevices(user)) {
            return ScanReturn.FAILUSERNORPERMITTEDTOREMOVE;
        } else if (!databaseDevices.canDeviceBeRemoved(device)) {
            return ScanReturn.FAILDEVICEUNAVIALABLE;
        } else if (!databaseUserGroups.canUserGroupRemoveDevice(device, user)) {
            return ScanReturn.FAILUSERGROUPRULESETNOTCOMPATABLE;
        }

        return ScanReturn.SUCCESSSCANREMOVE;
    }


}
