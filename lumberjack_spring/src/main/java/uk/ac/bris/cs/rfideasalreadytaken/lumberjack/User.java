package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public class User {

    private String id;
    private int deviceLimit;
    private int devicesRemoved;
    private boolean canRemove;

    public String getId() {
        return id;
    }

    public int getDeviceLimit() {
        return deviceLimit;
    }

    public int getDevicesRemoved() {
        return devicesRemoved;
    }

    public boolean getCanRemove() {
        return canRemove;
    }

    public void setId(String id) {
        this.id = id;
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
}
