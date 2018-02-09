package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public class Device {

    private String id;
    private String scanValue;
    private String type;
    private int available;
    private int currentlyAssigned;

    public Device(String id, String scanValue, String type, int available, int currentlyAssigned){
        this.id = id;
        this.scanValue = scanValue;
        this.type = type;
        this.available = available;
        this.currentlyAssigned = currentlyAssigned;
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

    public int isAvailable() {
        return available;
    }

    public int isCurrentlyAssigned() {
        return currentlyAssigned;
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

    public void setAvailable(int available) {
        this.available = available;
    }

    public void setCurrentlyAssigned(int currentlyAssigned) {
        this.currentlyAssigned = currentlyAssigned;
    }
}
