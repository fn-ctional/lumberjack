package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.AdminUser;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class AuthenticationDatabaseManager {

    private boolean connected = false;
    private Connection connection = null;
    private Statement stmt = null;

    protected AuthenticationDatabaseManager() {
        try {
            connectToDatabase();
        } catch (SQLException e) {
           //TODO: not sure what to do here
        }
    }

    private void connectToDatabase() throws SQLException {

        if(!connected) {
            MysqlDataSource dataSource = new MysqlDataSource();

            dataSource.setServerName("129.150.119.251");
            dataSource.setPortNumber(3306);
            dataSource.setDatabaseName("LumberjackDatabase");
            dataSource.setUser("lumberjack");
            dataSource.setPassword("Lumberjack1#");
            dataSource.setConnectTimeout(5000);

            this.connection = dataSource.getConnection();

            stmt = this.connection.createStatement();

            this.connected = true;
        }
    }

    public void addAdminUser(AdminUser adminUser) {
       //TODO: implement please
    }

    public AdminUser findByEmail(String email) {
        //TODO: implement please
        return null;
    }

    public boolean userExists(String email) {
        //TODO: implement please
        return true;
    }

    /**
     * Whether the email being used to register as an admin is on the permitted list of emails.
     * An empty list returns true and the email is used as the first entry.
     * @param email Email being used to register.
     * @return Whether the email is on the list.
     *  If the list is empty, the email being checked is added, and it returns true.
     */
    public boolean emailPermitted(String email) {
        //TODO: implement please
        return true;
    }
}
