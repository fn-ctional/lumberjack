package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public class DeviceState {

    private String device;
    private boolean busy;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}
