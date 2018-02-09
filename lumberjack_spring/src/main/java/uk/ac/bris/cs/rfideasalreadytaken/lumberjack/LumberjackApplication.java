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

		log.info("Start");

		Backend backend = new Backend();

		backend.resetDatabase();
		backend.insertTestCases();

		Scan scan = new Scan("Aidan9876", "");
		//log.info(String.valueOf(backend.isValidUser(scan)));
		scan = new Scan("Betty1248", "");
		//log.info(String.valueOf(backend.isValidUser(scan)));
		scan = new Scan("junkno1", "");
		//log.info(String.valueOf(backend.isValidUser(scan)));
		scan = new Scan("othershite", "");
		//log.info(String.valueOf(backend.isValidUser(scan)));

		log.info("End");
		return;
	}
}
