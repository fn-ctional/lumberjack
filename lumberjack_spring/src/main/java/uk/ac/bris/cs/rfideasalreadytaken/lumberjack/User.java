package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public class User {

    private String id;
    private String scanValue;
    private int deviceLimit;
    private int devicesRemoved;
    private int canRemove;

    public User(String id, String scanValue, int deviceLimit, int devicesRemoved, int canRemove){
        this.id = id;
        this.scanValue = scanValue;
        this.deviceLimit = deviceLimit;
        this.devicesRemoved = devicesRemoved;
        this.canRemove = canRemove;
    }

    public String getId() {
        return id;
    }

    public String getScanValue() {
        return scanValue;
    }

    public int getDeviceLimit() {
        return deviceLimit;
    }

    public int getDevicesRemoved() {
        return devicesRemoved;
    }

    public int canRemove() {
        return canRemove;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setScanValue(String scanValue) {
        this.scanValue = scanValue;
    }

    public void setDeviceLimit(int deviceLimit) {
        this.deviceLimit = deviceLimit;
    }

    public void setDevicesRemoved(int devicesRemoved) {
        this.devicesRemoved = devicesRemoved;
    }

    public void setCanRemove(int canRemove) {
        this.canRemove = canRemove;
    }
}
