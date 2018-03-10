package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.ScanDTO;

public interface FromCardReader {

    ScanReturn scanReceived(ScanDTO scanDTO) throws Exception;

}
