package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data;

import java.util.Objects;

public class User {

    private String id;
    private String scanValue;
    private int deviceLimit;
    private int devicesRemoved;
    private boolean canRemove;

    public User(){}

    public User(String id, String scanValue, int deviceLimit, int devicesRemoved, boolean canRemove){
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

    public boolean canRemove() {
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

    public void setCanRemove(boolean canRemove) {
        this.canRemove = canRemove;
    }

    @Override
    public boolean equals(Object o) {
        if (getClass() != o.getClass()) return false;
        User u = (User) o;
        return Objects.equals(id, u.id) &&
                Objects.equals(scanValue, u.scanValue) &&
                deviceLimit == u.deviceLimit &&
                devicesRemoved == u.devicesRemoved &&
                canRemove == u.canRemove;
    }
}
