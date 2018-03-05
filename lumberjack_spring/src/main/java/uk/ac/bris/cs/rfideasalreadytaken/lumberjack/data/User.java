package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data;

public class User {

    private String id;
    private String scanValue;
    private int deviceLimit;
    private int devicesRemoved;
    private boolean canRemove;
    private String groupID;

    public User(){}

    public User(String id, String scanValue, int deviceLimit, int devicesRemoved, boolean canRemove, String groupID){
        this.id = id;
        this.scanValue = scanValue;
        this.deviceLimit = deviceLimit;
        this.devicesRemoved = devicesRemoved;
        this.canRemove = canRemove;
        this.groupID = groupID;
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

    public boolean canRemove() {
        return canRemove;
    }

    public String getGroupId() {
        return groupID;
    }

    public void setId(String id) { this.id = id; }

    public void setScanValue(String scanValue) {
        this.scanValue = scanValue;
    }

    public void setDeviceLimit(int deviceLimit) {
        this.deviceLimit = deviceLimit;
    }

    public void setDevicesRemoved(int devicesRemoved) {
        this.devicesRemoved = devicesRemoved;
    }

    public void setCanRemove(boolean canRemove) {
        this.canRemove = canRemove;
    }

    public void setGroupId(String groupID) { this.groupID = groupID; }
}
