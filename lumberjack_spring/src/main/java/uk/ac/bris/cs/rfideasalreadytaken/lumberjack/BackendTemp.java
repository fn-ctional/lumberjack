package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public class BackendTemp implements FromCardReader{

    private boolean userLoaded = false;
    private User currentUser = new User();

    public String scanRecieved(Scan scan){

        if(isValidUser(scan)){
            return userScanned(scan);
        }

        if(isValidDevice(scan)){
            return deviceScanned(scan);
        }

        return "Scan not recognised";
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
        //Database Stuff
        return true;
    }

    private boolean takeOutDevice(Device device, User user){
        //Database Stuff
        return true;
    }
}
