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

		java.sql.Date date = java.sql.Date.valueOf("2018-02-10");
		log.info(date.toString());

		backend.resetDatabase();
		backend.insertTestCases();

		log.info("End");
		return;
	}
}
