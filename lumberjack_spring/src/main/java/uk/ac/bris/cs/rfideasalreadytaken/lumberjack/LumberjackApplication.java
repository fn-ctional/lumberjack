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

		log.info("1");

		Class.forName("com.mysql.jdbc.Driver");
		MysqlDataSource dataSource = new MysqlDataSource();

		log.info("2");

		//dataSource.setServerName("129.150.119.251");
		dataSource.setConnectTimeout(5000);
		//dataSource.setServerName("oc-129-150-119-251.compute.oraclecloud.com");
		dataSource.setURL("jdbc:mysql://129.150.119.251:3306/LumberjackDatabase");
		//dataSource.setPortNumber(3306);
		//dataSource.setDatabaseName("LumberjackDatabase");
		dataSource.setUser("lumberjack");
		dataSource.setPassword("Lumberjack1#");

		log.info("3");

		Connection conn = dataSource.getConnection();



		log.info("4");

		Statement stmt = conn.createStatement();

		log.info("5");

		//ResultSet rs = stmt.executeQuery("DROP TABLE test IF EXISTS");
		ResultSet rs = stmt.executeQuery("CREATE TABLE test(\" + \"id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

		log.info("6");

		rs.close();
		stmt.close();
		conn.close();

		//ResultSet rs = stmt.executeQuery("SELECT ID FROM USERS");
		//log.info("Creating tables");
		//jdbcTemplate.execute("DROP TABLE example IF EXISTS");
		//jdbcTemplate.execute("CREATE TABLE example(" + "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");
	}
}
