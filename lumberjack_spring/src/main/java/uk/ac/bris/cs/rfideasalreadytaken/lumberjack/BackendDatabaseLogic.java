package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.ScanDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BackendDatabaseLogic extends BackendDatabaseManipulation{

    protected boolean canUserRemoveDevices(User user) throws Exception{
        try{
        if(user.canRemove()){
            return true;
        }
        return false;
        }
        catch (Exception e){return false;}
    }

    protected boolean isUserAtDeviceLimit(User user){
        try{
        if(user.getDeviceLimit() == user.getDevicesRemoved()){
            return true;
        }
        return false;
    }
        catch (Exception e){return false;}
    }

    protected boolean canDeviceBeRemoved(Device device) throws Exception{
        try{
        if(device.isAvailable()){
            return true;
        }
        return false;
    }
        catch (Exception e){return false;}
    }

    protected boolean isDeviceCurrentlyOut(Device device) throws Exception{
        try{
        if(device.isCurrentlyAssigned()){
            return true;
        }
        return false;
        }
        catch (Exception e){return false;}
    }

    protected boolean isValidUser(ScanDTO scanDTO) throws Exception{
        try{
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM Users WHERE ScanValue = ?");
        stmt.setString(1, scanDTO.getUser());
        ResultSet rs = stmt.executeQuery();
        return rs.next();
        }
        catch (Exception e){return false;}
    }

    protected boolean isValidDevice(ScanDTO scanDTO) throws Exception{
        try{
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM Devices WHERE ScanValue = ?");
        stmt.setString(1, scanDTO.getDevice());
        ResultSet rs = stmt.executeQuery();
        return rs.next();
        }
        catch (Exception e){return false;}
    }

    protected boolean canUserGroupRemoveDevice(Device device, User user) throws Exception{
        try{
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM GroupPermissions WHERE UserGroupID = ? AND RuleID = ?;");
            stmt.setString(1, user.getGroupId());
            stmt.setString(2, device.getRuleID());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
        catch (Exception e){return false;}
    }

    protected boolean adminUserExists(String email) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Admins WHERE Email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isEmailPermitted(String email) {
       try {
          PreparedStatement stmt1 = conn.prepareStatement("SELECT * FROM PermittedEmails ");
           ResultSet rs1 = stmt1.executeQuery();
           if (!rs1.next()) return true;

          PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM PermittedEmails WHERE Email = ?");
           stmt2.setString(1, email);
           ResultSet rs2 = stmt2.executeQuery();
           return rs2.next();
       } catch (Exception e) {
           return false;
       }
    }
}
