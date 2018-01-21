package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public interface FromCardReader {

    /*
    boolean userLoaded = false;
    User currentUser = new User();
     */

    String somethingScanned(/*Scan scan*/);/*{

        if(timePassed() > 30 seconds){
            userLoaded = false
        }

         if(validUser(scan)){

            User loadedUser = loadUser(scan)

            if(userCanRemoveDevices(loadedUser)){
                currentUser = loadUser(scan)
                userLoaded = true
                return "user loaded sucessfully"
            }
            else{
                return "error user cannot remove devices"
            }
        }

        if(validDevice(scan)){

            Device loadedDevice = loadDevice(scan)

            if(deviceCanBeRemoved(loadedDevice)){

                if(!userLoaded){
                    return "error no user has been scanned"
                }

                if(deviceCurrentlyOut(loadedDevice)){

                    if(returnDevice(loadedDevice, currentUser)){
                        return "device returned successfully"
                    }
                    else{
                        return "error returning device"
                    }
                }
                else{
                    if(takeOutDevice(loadedDevice, currentUser)){
                        return "device taken out successfully"
                    }
                    else{
                        return "error taking out device"
                    }
                }
            }
            else{
                return "error device cannot be taken out"
            }
        }

        return "error scan not recognised"
    }*/

    boolean takeOutDevice(Device device, User user);

    boolean returnDevice(Device device, User user);

}
