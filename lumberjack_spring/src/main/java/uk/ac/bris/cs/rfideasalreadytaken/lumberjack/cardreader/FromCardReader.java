package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader.data.ScanDTO;

public interface FromCardReader {

    ScanReturn scanReceived(ScanDTO scanDTO) throws Exception;

}
