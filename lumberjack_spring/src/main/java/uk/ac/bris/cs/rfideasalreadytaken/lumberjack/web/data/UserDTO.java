package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.data;

public class UserDTO {

    private String scanValue;
    private int deviceLimit;
    private Boolean canRemove;
    private String groupID;

    public UserDTO() {}

    public String getScanValue() {
        return scanValue;
    }

    public void setScanValue(String scanValue) {
        this.scanValue = scanValue;
    }

    public int getDeviceLimit() {
        return deviceLimit;
    }

    public void setDeviceLimit(int deviceLimit) {
        this.deviceLimit = deviceLimit;
    }

    public Boolean getCanRemove() {
        return canRemove;
    }

    public void setCanRemove(Boolean canRemove) {
        this.canRemove = canRemove;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

}
