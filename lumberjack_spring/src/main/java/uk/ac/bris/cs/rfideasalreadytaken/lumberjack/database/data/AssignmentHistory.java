package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data;

import java.sql.Time;

public class AssignmentHistory {

    private int assignmentHistoryID;
    private String deviceID;
    private String userID;
    private java.sql.Date dateAssigned;
    private java.sql.Time timeAssigned;
    private java.sql.Date dateReturned;
    private java.sql.Time timeReturned;
    private boolean returnedOnTime;
    private String returnedByID;

    public AssignmentHistory(){};

    public AssignmentHistory(int assignmentHistoryID, String deviceID, String userID, java.sql.Date dateAssigned, java.sql.Time timeAssigned, java.sql.Date dateReturned, java.sql.Time timeReturned, boolean returnedOnTime, String returnedByID){
        this.assignmentHistoryID = assignmentHistoryID;
        this.deviceID = deviceID;
        this.userID = userID;
        this.dateAssigned = dateAssigned;
        this.timeAssigned = timeAssigned;
        this.dateReturned = dateReturned;
        this.timeReturned = timeReturned;
        this.returnedOnTime = returnedOnTime;
        this.returnedByID = returnedByID;
    }

    public int getAssignmentHistoryID() {
        return assignmentHistoryID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public String getUserID() {
        return userID;
    }

    public java.sql.Date getDateAssigned() {
        return dateAssigned;
    }

    public Time getTimeAssigned() {
        return timeAssigned;
    }

    public java.sql.Date getDateReturned() {
        return dateReturned;
    }

    public Time getTimeReturned() {
        return timeReturned;
    }

    public boolean isReturnedOnTime() {
        return returnedOnTime;
    }

    public String getReturnedByID() {
        return returnedByID;
    }

    public void setAssignmentHistoryID(int assignmentHistoryID) { this.assignmentHistoryID = assignmentHistoryID; }

    public void setDeviceID(String deviceID) { this.deviceID = deviceID; }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setDateAssigned(java.sql.Date dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public void setTimeAssigned(Time timeAssigned) {
        this.timeAssigned = timeAssigned;
    }

    public void setDateReturned(java.sql.Date dateReturned) {
        this.dateReturned = dateReturned;
    }

    public void setTimeReturned(Time timeReturned) {
        this.timeReturned = timeReturned;
    }

    public void setReturnedOnTime(boolean returnedOnTime) { this.returnedOnTime = returnedOnTime; }

    public void setReturnedByID(String returnedByID) {
        this.returnedByID = returnedByID;
    }
}
