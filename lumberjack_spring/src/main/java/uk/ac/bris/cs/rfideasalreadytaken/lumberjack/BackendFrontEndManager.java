package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;

import javax.validation.constraints.Null;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BackendFrontEndManager extends BackendDatabaseLoading implements FromFrontEnd {

    public boolean insertUser(User user) throws Exception{
        try {
            insertIntoUsers(user);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public boolean insertUsers(ArrayList<User> users) throws Exception{

        try {
            for(int i = 0; i < users.size(); i++) {
                insertUser(users.get(i));
            }
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public boolean removeUser(User user) throws Exception{
        try {
            deleteFromUsers(user.getId());
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public boolean resetUsers() throws Exception{
        try {
            ArrayList<User> users = getUsers();
            for(int i = 0; i < users.size(); i++) {
                deleteFromUsers(users.get(i).getId());
            }
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public User getUser(String userID) throws Exception{
        try {
            User user = getUser(userID);
            return user;
        }
        catch (Exception e){
            return new User();
        }
    }

    public ArrayList<User> getUsers() throws Exception{
        try {
            ArrayList<User> users = new ArrayList<User>();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users");
            ResultSet rs = stmt.executeQuery();

            rs.last();
            int total = rs.getRow();
            rs.beforeFirst();

            while(rs.next()) {
                users.add(loadUserFromResultSet(rs));
            }

            return users;
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }

    public boolean editUser(String userID, User newValue) throws Exception {
        try {
            updateUser(userID, newValue);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }





    public void deleteDevice(Device device){
        //boolean ignore = deleteFromDevices(device.getId());
    }

    public void insertDevice(Device device){
        //boolean ignore = insertIntoDevices(device);
    }

    public void setDeviceType(Device device, String type){
        device.setType(type);
    }

    public void setUserMaxDevices(User user, int max){
        user.setDeviceLimit(max);
    }
}
