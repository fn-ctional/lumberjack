package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class DatabaseConnection {

    private Connection conn;

    @Autowired
    public DatabaseConnection(@Value("${database-ip}") final String serverName,
                              @Value("${database-port}") final int portNumber,
                              @Value("${database-name}") final String databaseName,
                              @Value("${database-username}") final String username,
                              @Value("${database-password}") final String password) throws Exception {
        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setServerName(serverName);
        dataSource.setPortNumber(portNumber);
        dataSource.setDatabaseName(databaseName);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setConnectTimeout(5000);

        try {
            conn = dataSource.getConnection();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return conn;
    }
}
