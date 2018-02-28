package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.ScanDTO;

@SpringBootApplication
public class LumberjackApplication implements CommandLineRunner{

	private static final Logger log = LoggerFactory.getLogger(LumberjackApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(LumberjackApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {

		Backend backend = new Backend();
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
