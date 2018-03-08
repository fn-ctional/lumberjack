package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data;

import java.sql.Time;
import java.util.Date;
import java.util.Calendar;


public class Assignment {

    private int id;
    private String deviceID;
    private String userID;
    private java.sql.Date dateAssigned;
    private java.sql.Time timeAssigned;

    public Assignment(){};

    public Assignment(String deviceID, String userID){

        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        java.sql.Time time = new java.sql.Time(Calendar.getInstance().getTime().getTime());

        this.id = 0;
        this.deviceID = deviceID;
        this.userID = userID;
        this.dateAssigned = date;
        this.timeAssigned = time;
    }

    public int getId() { return id; }

    public String getDeviceID() { return deviceID; }

    public String getUserID() {
        return userID;
    }

    public java.sql.Date getDateAssigned() {
        return dateAssigned;
    }

    public java.sql.Time getTimeAssigned() {
        return timeAssigned;
    }

    public void setAssignmentID(int id) {
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

    public void setTimeAssigned(java.sql.Time timeAssigned) { this.timeAssigned = timeAssigned; }
}
