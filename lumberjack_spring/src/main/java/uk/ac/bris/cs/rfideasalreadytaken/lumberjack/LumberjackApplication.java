package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

@SpringBootApplication
public class LumberjackApplication implements CommandLineRunner{

	private static final Logger log = LoggerFactory.getLogger(LumberjackApplication.class);

	//@Autowired
	//JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(LumberjackApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {

		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser("username");
		dataSource.setPassword("password");
		dataSource.setServerName("DBhost.example.org");

		Connection conn = dataSource.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT ID FROM USERS");

		rs.close();
		stmt.close();
		conn.close();

		//log.info("Creating tables");
		//jdbcTemplate.execute("DROP TABLE example IF EXISTS");
		//jdbcTemplate.execute("CREATE TABLE example(" + "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");
	}
}
