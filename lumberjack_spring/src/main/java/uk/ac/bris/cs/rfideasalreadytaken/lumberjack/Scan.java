package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public class Scan {

    private final String userID;
    private final String deviceID;

    Scan(String userID, String deviceID) {
        this.userID = userID;
        this.deviceID = deviceID;
    }

    public String getUserID() {
        return userID;
    }

    public String getDeviceID() {
        return deviceID;
    }
}
