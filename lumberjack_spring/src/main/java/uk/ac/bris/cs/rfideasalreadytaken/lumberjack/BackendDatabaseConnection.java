package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.Statement;

public class BackendDatabaseConnection {

    protected boolean connected = false;
    protected Connection conn = null;
    protected Statement stmt = null;

    @Value("${ip}")
    private String serverName;
    @Value("${port}")
    private int portNumber;
    @Value("${database}")
    private String databaseName;
    @Value("${username}")
    private String username;
    @Value("${password}")
    private String password;

    BackendDatabaseConnection() throws Exception{
        try {
            MysqlDataSource dataSource = new MysqlDataSource();

            dataSource.setServerName(serverName);
            dataSource.setPortNumber(portNumber);
            dataSource.setDatabaseName(databaseName);
            dataSource.setUser(username);
            dataSource.setPassword(password);
            dataSource.setConnectTimeout(5000);

            conn = dataSource.getConnection();

            stmt = conn.createStatement();

            connected = true;
        }
        catch(com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
        }
    }

    protected boolean connectToDatabase() throws Exception{

        if(connected == false){
            try {
                MysqlDataSource dataSource = new MysqlDataSource();

                dataSource.setServerName(serverName);
                dataSource.setPortNumber(portNumber);
                dataSource.setDatabaseName(databaseName);
                dataSource.setUser(username);
                dataSource.setPassword(password);
                dataSource.setConnectTimeout(5000);

                conn = dataSource.getConnection();

                stmt = conn.createStatement();

                connected = true;

            }catch(com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                return false;
            }
        }

        return true;
    }

    public boolean diconnectFromDatabase() throws Exception{
        try {
            conn.close();
            return true;

        }catch(Exception e) {
            return false;
        }
    }

    public boolean setConnectionValues( String serverName, int portNumber, String databaseName, String username, String password) throws Exception{
        try {

            this.serverName = serverName;
            this.portNumber = portNumber;
            this.databaseName = databaseName;
            this.username = username;
            this.password = password;

            return true;

        }catch(Exception e) {
            return false;
        }
    }
}
