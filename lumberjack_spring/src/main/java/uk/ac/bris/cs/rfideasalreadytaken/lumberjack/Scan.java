package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public class Scan {

    private String user;
    private String device;

    public String getUserID() {
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
