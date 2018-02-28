package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

public interface FromCardReader{

    ScanReturn scanRecieved(Scan scan) throws Exception;
}
