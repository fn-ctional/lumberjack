package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.Statement;

public class BackendDatabaseConnection {

    protected boolean connected = false;
    protected Connection conn = null;
    protected Statement stmt = null;

    private String serverName = "129.150.119.251";
    private int portNumber = 3306;
    private String databaseName = "LumberjackDatabase";
    private String username = "lumberjack";
    private String password = "Lumberjack1#";

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
