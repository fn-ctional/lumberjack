package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public class User {

    private String id;
    private int deviceLimit;

    public String getId() {
        return id;
    }

    public int getDeviceLimit() {
        return deviceLimit;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setId(int deviceLimit) {
        this.deviceLimit = deviceLimit;
    }
}
