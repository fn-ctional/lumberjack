package uk.ac.bris.cs.rfideasalreadytaken.rfid_pi_web;

public class Scan {

    private String deviceID;
    private String userID;
    private boolean success;

    public Scan(String userID, String deviceID, boolean success) {
       this.userID = userID;
       this.deviceID = deviceID;
       success = success;
    }

    public Scan(boolean success) {
       success = success;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isSuccess() {
        return success;
    }
}
