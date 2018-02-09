package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import java.sql.Time;
import java.util.Date;

public class Assignment {

    private String id;
    private String deviceID;
    private String userID;
    private java.sql.Date dateAssigned;
    private int timeAssigned;

    public Assignment(String id, String deviceID, String userID, java.sql.Date dateAssigned, int timeAssigned){
        this.id = id;
        this.deviceID = deviceID;
        this.userID = userID;
        this.dateAssigned = dateAssigned;
        this.timeAssigned = timeAssigned;
    }

    public String getId() { return id; }

    public String getDeviceID() { return deviceID; }

    public String getUserID() {
        return userID;
    }

    public java.sql.Date getDateAssigned() {
        return dateAssigned;
    }

    public int getTimeAssigned() {
        return timeAssigned;
    }

    public void setAssignmentID(String id) {
        this.id = id;
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

    public void setTimeAssigned(int timeAssigned) { this.timeAssigned = timeAssigned; }
}
