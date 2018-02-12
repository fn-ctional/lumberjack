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

		Device device = new Device("sfysgdf", "scanVsdgsdgalueD1", "ladsfsdfptop", true, true);
		backend.insertIntoDevices((device));
		//Scan scan = new Scan("scanValueU1", "");
		//User user = backend.loadUser(scan);
		//log.info(user.getId() + " | " + user.getScanValue());
		//scan = new Scan("scanValueU4", "");
		//user = backend.loadUser(scan);
		//log.info(user.getId() + " | " + user.getScanValue());

		log.info("End");
		return;
	}
}
