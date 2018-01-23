package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class LumberjackApplication implements CommandLineRunner{

	private static final Logger log = LoggerFactory.getLogger(LumberjackApplication.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(LumberjackApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		log.info("Creating tables");
		jdbcTemplate.execute("DROP TABLE example IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE example(" + "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");
	}
}
