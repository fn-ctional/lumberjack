package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public interface FromCardReader{

    String scanRecieved(Scan scan) throws Exception;
}
