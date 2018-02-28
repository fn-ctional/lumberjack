package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.ScanDTO;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DatabaseTests {

    @Autowired
    private Backend backend;

	@Test
    public void nicksOldTests() throws Exception {
        final Logger log = LoggerFactory.getLogger(LumberjackApplication.class);
		backend.resetDatabase();
		backend.insertTestCases();

		ScanDTO scanDTO = new ScanDTO();
		scanDTO.setUser("1314831486");
		scanDTO.setDevice("1314831486");

		log.info("ScanDTO User:");
		log.info(backend.scanReceived(scanDTO) + "\n");

		scanDTO.setUser("36109839730967812");
		scanDTO.setDevice("36109839730967812");

		log.info("ScanDTO User:");
		log.info("ScanDTO Device not taken out:");
		log.info(backend.scanReceived(scanDTO) + "\n");

		scanDTO.setUser("457436545");
		scanDTO.setDevice("457436545");

		log.info("ScanDTO User:");
		log.info(backend.scanReceived(scanDTO) + "\n");

		scanDTO.setUser("23482364326842334");
		scanDTO.setDevice("23482364326842334");

		log.info("ScanDTO User:");
		log.info("ScanDTO Device not taken out:");
		log.info(backend.scanReceived(scanDTO) + "\n");
		log.info(backend.scanReceived(scanDTO) + "\n");
	}


}
