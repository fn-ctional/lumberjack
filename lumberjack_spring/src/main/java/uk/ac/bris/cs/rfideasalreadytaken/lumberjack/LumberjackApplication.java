package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class LumberjackApplication implements CommandLineRunner{

	private static final Logger log = LoggerFactory.getLogger(LumberjackApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(LumberjackApplication.class, args);
		return;
	}

	@Override
	public void run(String... strings) throws Exception {

		Backend backend = new Backend();
		backend.resetDatabase();
		backend.insertTestCases();

		Scan scan = new Scan();
		scan.setUser("1314831486");
		scan.setDevice("1314831486");

		log.info("Scan User:");
		log.info(backend.scanRecieved(scan) + "\n");

		scan.setUser("36109839730967812");
		scan.setDevice("36109839730967812");

		log.info("Scan Device not taken out:");
		log.info(backend.scanRecieved(scan) + "\n");

		scan.setUser("457436545");
		scan.setDevice("457436545");

		log.info("Scan User:");
		log.info(backend.scanRecieved(scan) + "\n");

		scan.setUser("23482364326842334");
		scan.setDevice("23482364326842334");

		log.info("Scan Device not taken out:");
		log.info(backend.scanRecieved(scan) + "\n");

		return;
	}
}
