package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data;

import java.util.Objects;

public class Device {

    private String id;
    private String scanValue;
    private String type;
    private boolean available;
    private boolean currentlyAssigned;
    private String ruleID;

    public Device(){};

    public Device(String id, String scanValue, String type, boolean available, boolean currentlyAssigned, String ruleID){
        this.id = id;
        this.scanValue = scanValue;
        this.type = type;
        this.available = available;
        this.currentlyAssigned = currentlyAssigned;
        this.ruleID = ruleID;
    }

    public String getId() {
        return id;
    }

    public String getScanValue() {
        return scanValue;
    }

    public String getType() {
        return type;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isCurrentlyAssigned() {
        return currentlyAssigned;
    }

    public String getRuleID() {
        return ruleID;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setScanValue(String scanValue) {
        this.scanValue = scanValue;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setCurrentlyAssigned(boolean currentlyAssigned) {
        this.currentlyAssigned = currentlyAssigned;
    }

    public void setRuleID(String ruleID) {
        this.ruleID = ruleID;
    }

    @Override
    public boolean equals(Object o) {
        if (getClass() != o.getClass()) return false;
        Device d = (Device) o;
        return Objects.equals(id, d.id) &&
                Objects.equals(scanValue, d.scanValue) &&
                Objects.equals(type , d.type) &&
                available == d.available &&
                Objects.equals(ruleID , d.ruleID) &&
                currentlyAssigned == d.currentlyAssigned;
    }
}
