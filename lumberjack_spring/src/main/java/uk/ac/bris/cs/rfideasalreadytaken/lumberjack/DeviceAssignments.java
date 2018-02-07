package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import java.sql.Time;
import java.util.Date;

public class DeviceAssignments {

    private String deviceID;
    private String userID;
    private java.sql.Date dateAssigned;
    private java.sql.Time timeAssigned;

    public String getDeviceID() {

        return deviceID;
    }

    public String getUserID() {
        return userID;
    }

    public Date getDateAssigned() {
        return dateAssigned;
    }

    public Date getTimeAssigned() {
        return timeAssigned;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setDateAssigned(java.sql.Date dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public void setDateAssigned(java.sql.Time timeAssigned) {
        this.timeAssigned = timeAssigned;
    }
}
