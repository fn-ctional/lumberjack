package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public interface FromCardReader{

    String scanReceived(Scan scan) throws Exception;
}
