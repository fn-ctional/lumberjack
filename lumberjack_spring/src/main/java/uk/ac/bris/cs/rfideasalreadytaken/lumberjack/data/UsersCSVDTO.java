package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data;

import java.util.List;

public class UsersCSVDTO {

    private List<String> scanValue;

    private List<Integer> deviceLimit;

    private List<Integer> devicesRemoved;

    private List<Boolean> canRemove;

    private List<String> groupID;

    public List<String> getScanValue() {
        return scanValue;
    }

    public void setScanValue(List<String> scanValue) {
        this.scanValue = scanValue;
    }

    public List<Integer> getDeviceLimit() {
        return deviceLimit;
    }

    public void setDeviceLimit(List<Integer> deviceLimit) {
        this.deviceLimit = deviceLimit;
    }

    public List<Boolean> getCanRemove() {
        return canRemove;
    }

    public void setCanRemove(List<Boolean> canRemove) {
        this.canRemove = canRemove;
    }

    public List<String> getGroupID() {
        return groupID;
    }

    public void setGroupID(List<String> groupID) {
        this.groupID = groupID;
    }

    public List<Integer> getDevicesRemoved() {
        return devicesRemoved;
    }

    public void setDevicesRemoved(List<Integer> devicesRemoved) {
        this.devicesRemoved = devicesRemoved;
    }
}
