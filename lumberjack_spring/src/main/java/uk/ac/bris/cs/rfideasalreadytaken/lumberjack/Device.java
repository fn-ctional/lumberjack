package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public class Device {

    private String id;
    private String scanValue;
    private String type;
    private boolean available;
    private boolean currentlyAssigned;

    public Device(String id, String scanValue, String type, boolean available, boolean currentlyAssigned){
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

    public boolean isAvailable() {
        return available;
    }

    public boolean isCurrentlyAssigned() {
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

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setCurrentlyAssigned(boolean currentlyAssigned) {
        this.currentlyAssigned = currentlyAssigned;
    }
}
