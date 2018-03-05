package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;

public class BackendFromFrontEnd extends BackendFromCardReader implements FromFrontEnd {

    public void deleteDevice(Device device){
        //boolean ignore = deleteFromDevices(device.getId());
    }

    public void insertDevice(Device device){
        //boolean ignore = insertIntoDevices(device);
    }

    public void insertUser(User user){
        //boolean ignore = insertIntoUsers(user);
    }

    public void setDeviceType(Device device, String type){
        device.setType(type);
    }

    public void setUserMaxDevices(User user, int max){
        user.setDeviceLimit(max);
    }

    public void deleteUser(User user) throws Exception{
        boolean ignore = deleteFromUsers(user.getId());
    }
}
