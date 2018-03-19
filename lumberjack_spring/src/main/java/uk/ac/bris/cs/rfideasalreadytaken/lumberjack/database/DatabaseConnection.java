package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

@Service
public class DatabaseConnection {

    private Connection conn;

    @Autowired
    public DatabaseConnection(@Value("${database-ip}") final String serverName,
                              @Value("${database-port}") final int portNumber,
                              @Value("${database-name}") final String databaseName,
                              @Value("${database-username}") final String username,
                              @Value("${database-password}") final String password) throws SQLException {
        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setServerName(serverName);
        dataSource.setPortNumber(portNumber);
        dataSource.setDatabaseName(databaseName);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setConnectTimeout(5000);

        final boolean debug = false;
        if (debug) debugConnection(serverName,portNumber,databaseName,username,password);

        conn = dataSource.getConnection();
    }

    public Connection getConnection() {
        return conn;
    }

    private void debugConnection(final String serverName,
                                 final int portNumber,
                                 final String databaseName,
                                 final String username,
                                 final String password) {
        System.out.println(serverName);
        System.out.println(portNumber);
        System.out.println(databaseName);
        System.out.println(username);
        System.out.println(password);
    }

}
