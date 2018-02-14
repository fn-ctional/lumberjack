package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class FromFrontEnd extends Backend {


    void setUserMaxDevices(User user, int max){
        user.deviceLimit = max;
    }

    void setDeadline(Device device, Date date);

    void setDeviceType(Device device, String type){
        device.type = type;
    }

    boolean getAvailable(Device device){
        return device.available;

    }

    boolean getCurrentlyAssigned(Device device){
      return device.currentlyAssigned;

    }

    boolean studentTakeLaptop(Scan scan) {
        String result = takeOutDevice(scan.getDevice(), scan.getUser());
        if (result == successRemoval) return true;
        else return false;
    }
