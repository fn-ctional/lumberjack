package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import java.util.Date;

public class DeviceAssignments {

    private String deviceID;
    private String userID;
    private Date dateAssigned;

    public String getDeviceID() {

        return deviceID;
    }

    public String getUserID() {
        return userID;
    }

    public Date getDateAssigned() {
        return dateAssigned;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setDateAssigned(Date dateAssigned) {
        this.dateAssigned = dateAssigned;
    }
}
