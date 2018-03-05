package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;

import java.util.ArrayList;

public class BackendFromFrontEnd extends BackendFromCardReader implements FromFrontEnd {

    public boolean insertUser(User user) throws Exception{
        return true;
    }

    public boolean insertUsers(ArrayList<User> users) throws Exception{
        return true;
    }

    public boolean deleteUser(User user) throws Exception{
        return true;
    }

    public boolean resetUsers() throws Exception{
        return true;
    }

    public User getUser(String userID) throws Exception{
        return new User();
    }

    public ArrayList<User> getUsers() throws Exception{
        return new ArrayList<>();
    }

    public boolean editUser(String userID, User newValues) throws Exception
    {
        return true;
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
