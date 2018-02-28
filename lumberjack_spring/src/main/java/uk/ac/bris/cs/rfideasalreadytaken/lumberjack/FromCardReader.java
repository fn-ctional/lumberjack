package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public interface FromCardReader{

    ScanReturn scanReceived(Scan scan) throws Exception;

}
