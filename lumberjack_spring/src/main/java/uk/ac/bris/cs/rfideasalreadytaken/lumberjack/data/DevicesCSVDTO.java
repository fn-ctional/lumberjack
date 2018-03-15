package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data;

import java.util.List;

public class DevicesCSVDTO {

    private List<String> scanValue;

    private List<String> type;

    private List<Boolean> available;

    private List<Boolean> currentlyAssigned;

    private List<String> ruleID;

    public List<String> getScanValue() {
        return scanValue;
    }

    public void setScanValue(List<String> scanValue) {
        this.scanValue = scanValue;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public List<Boolean> getAvailable() {
        return available;
    }

    public void setAvailable(List<Boolean> available) {
        this.available = available;
    }

    public List<Boolean> getCurrentlyAssigned() {
        return currentlyAssigned;
    }

    public void setCurrentlyAssigned(List<Boolean> currentlyAssigned) {
        this.currentlyAssigned = currentlyAssigned;
    }

    public List<String> getRuleID() {
        return ruleID;
    }

    public void setRuleID(List<String> ruleID) {
        this.ruleID = ruleID;
    }
}
