package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public class Device {

    private String id;
    private String type;
    private boolean available;

    public String getID() {
        return id;
    }

    public String getType() {
        return type;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
