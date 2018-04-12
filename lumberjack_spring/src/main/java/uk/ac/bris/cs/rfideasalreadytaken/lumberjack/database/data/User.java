package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data;

import java.util.Objects;

public class User {

    private String id;
    private String scanValue;
    private int deviceLimit;
    private int devicesRemoved;
    private Boolean canRemove;
    private String groupID;

    public User(){
    }

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

    public Boolean canRemove() {
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

    public void setCanRemove(Boolean canRemove) {
        this.canRemove = canRemove;
    }

    public void setGroupId(String groupID) { this.groupID = groupID; }

    @Override
    public boolean equals(Object o) {
        if (getClass() != o.getClass()) return false;
        User u = (User) o;
        return Objects.equals(id, u.id) &&
                Objects.equals(scanValue, u.scanValue) &&
                deviceLimit == u.deviceLimit &&
                devicesRemoved == u.devicesRemoved &&
                Objects.equals(groupID, u.groupID) &&
                canRemove == u.canRemove;
    }
}
