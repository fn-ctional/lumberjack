package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public interface FromCardReader {

    boolean takeOutDevice(Device device, User user);

    boolean returnDevice(Device device, User user);
    
}
