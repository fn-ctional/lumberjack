package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public class Scan {

    private String user;
    private String device;

    //TODO: Refactored for now to conform with REST API but I think UserID is more representative of what it is
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user= user;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
