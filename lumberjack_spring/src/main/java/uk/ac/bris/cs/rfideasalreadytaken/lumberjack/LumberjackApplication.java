package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.ScanDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;

import java.util.ArrayList;

@SpringBootApplication
public class LumberjackApplication implements CommandLineRunner{

	private static final Logger log = LoggerFactory.getLogger(LumberjackApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(LumberjackApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {

//		BackendCardReaderManager backend = new BackendCardReaderManager();
//		BackendFrontEndManager frontend = new BackendFrontEndManager();
		final Logger log = LoggerFactory.getLogger(LumberjackApplication.class);
//		backend.resetDatabase();
//		backend.insertTestCases();
//
//		ScanDTO scanDTO = new ScanDTO();
//		scanDTO.setUser("1314831486");
//		scanDTO.setDevice("36109839730967812");
//
//		log.info("ScanDTO User:");
//		log.info(backend.scanReceived(scanDTO) + "\n");
//
//
//		scanDTO.setUser("457436545");
//		scanDTO.setDevice("23482364326842334");
//
//		log.info("ScanDTO User:");
//		log.info(backend.scanReceived(scanDTO) + "\n");


		//log.info("ScanDTO User:");

		//User user = new User("id", "dfsdfsdfsfd",546,0,true,"groupOne");
		//ArrayList<User> users = new ArrayList<>();
		//users.add(user);
		//user = new User("idgg", "dfsdfsdfsfdgg",5546,0,true,"groupOne");
		//users.add(user);

		//user = new User("id", "tyttryrt",6666,0,false,"groupOne");

		//frontend.insertUser(user);
		//frontend.insertUsers(users);
		//frontend.removeUser(user);
		//frontend.resetUsers();
		//log.info(frontend.getUser("idgg").getScanValue());
		//log.info(frontend.getUsers().toString());
		//frontend.updateUser("id",user);

		//log.info("ScanDTO User:");





		return;
	}

}
