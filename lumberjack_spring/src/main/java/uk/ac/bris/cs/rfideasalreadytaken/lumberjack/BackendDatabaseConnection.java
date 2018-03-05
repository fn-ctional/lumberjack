package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.Statement;

public class BackendDatabaseConnection {

    protected boolean connected = false;
    protected Connection conn = null;
    protected Statement stmt = null;

    protected boolean connectToDatabase() throws Exception{

        if(connected == false){
            try {
                MysqlDataSource dataSource = new MysqlDataSource();

                dataSource.setServerName("129.150.119.251");
                dataSource.setPortNumber(3306);
                dataSource.setDatabaseName("LumberjackDatabase");
                dataSource.setUser("lumberjack");
                dataSource.setPassword("Lumberjack1#");
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
}
