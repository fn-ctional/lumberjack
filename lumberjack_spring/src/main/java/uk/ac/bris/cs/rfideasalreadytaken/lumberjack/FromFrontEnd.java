package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import java.util.Date;
import java.util.List;

public interface FromFrontEnd {

    List<Device> getDevicesOut();

    void setUserMaxDevices(User user, int max);

    void setDeadline(Device device, Date date);

    void setDeviceType(Device device, String type);

    void getStatus(Device device);
}
