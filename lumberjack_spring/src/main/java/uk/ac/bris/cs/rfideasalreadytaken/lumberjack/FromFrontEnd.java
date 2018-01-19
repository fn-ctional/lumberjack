package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FromFrontEnd {

    Map<Device,User> getDevicesOut();

    void setUserMaxDevices(User user, int max);

    void setDeadline(Device device, Date date);

    void setDeviceType(Device device, String type);

    void getStatus(Device device);
}

