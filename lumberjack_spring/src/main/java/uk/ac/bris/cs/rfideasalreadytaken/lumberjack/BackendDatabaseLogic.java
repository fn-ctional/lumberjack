package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.ScanDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BackendDatabaseLogic extends BackendDatabaseManipulation{

    protected boolean canUserRemoveDevices(User user) throws Exception{
        if(user.canRemove()){
            return true;
        }
        return false;
    }

    protected boolean isUserAtDeviceLimit(User user){
        if(user.getDeviceLimit() == user.getDevicesRemoved()){
            return true;
        }
        return false;
    }

    protected boolean canDeviceBeRemoved(Device device) throws Exception{
        if(device.isAvailable()){
            return true;
        }
        return false;
    }

    protected boolean isDeviceCurrentlyOut(Device device) throws Exception{
        if(device.isCurrentlyAssigned()){
            return true;
        }
        return false;
    }

    protected boolean isValidUser(ScanDTO scanDTO) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM Users WHERE ScanValue = ?");
        stmt.setString(1, scanDTO.getUser());
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    protected boolean isValidDevice(ScanDTO scanDTO) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM Devices WHERE ScanValue = ?");
        stmt.setString(1, scanDTO.getDevice());
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }
}
