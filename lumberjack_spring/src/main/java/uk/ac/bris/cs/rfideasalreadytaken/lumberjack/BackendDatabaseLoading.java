package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BackendDatabaseLoading extends BackendDatabaseLogic {

    protected User loadUser(ScanDTO scanDTO) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE ScanValue = ?");
        stmt.setString(1, scanDTO.getUser());
        ResultSet rs = stmt.executeQuery();
        User user = loadUserFromResultSet(rs);
        return user;
    }

    protected Device loadDevice(ScanDTO scanDTO) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Devices WHERE ScanValue = ?");
        stmt.setString(1, scanDTO.getDevice());
        ResultSet rs = stmt.executeQuery();
        Device device = loadDeviceFromResultSet(rs);
        return device;
    }

    protected User loadUserFromResultSet(ResultSet rs) throws Exception{
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

    protected Device loadDeviceFromResultSet(ResultSet rs) throws Exception{
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

    protected Assignment loadAssignmentFromResultSet(ResultSet rs) throws Exception{
        if(rs.next()){
            Assignment assignment = new Assignment();
            assignment.setDeviceID(rs.getString("DeviceID"));
            assignment.setUserID(rs.getString("UserID"));
            assignment.setDateAssigned(rs.getDate("DateAssigned"));
            assignment.setAssignmentID(rs.getInt("id"));
            assignment.setTimeAssigned(rs.getTime("TimeAssigned"));
            return assignment;
        }
        return null;
    }

    protected AssignmentHistory loadAssignmentHistoryFromResultSet(ResultSet rs) throws Exception{
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
}
