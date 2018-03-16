package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class DatabaseAdminUsers {

    @Autowired
    private DatabaseConnection databaseConnection;

    public void insertIntoAdminUsers(AdminUser adminUser) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("INSERT INTO Admins (Email, Username, Password, Enabled)" +
                "VALUES (?,?,?,?)");
        stmt.setString(1, adminUser.getEmail());
        stmt.setString(2, adminUser.getName());
        stmt.setString(3, adminUser.getPassword());
        stmt.setBoolean(4, adminUser.isEnabled());
        stmt.execute();
    }

    public void updateAdminUser(String email, AdminUser adminUser) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("UPDATE Admins SET Email = ?, Username = ?, Password = ?, Enabled = ? " +
                "WHERE Email = ?");
        stmt.setString(1, adminUser.getEmail());
        stmt.setString(2, adminUser.getName());
        stmt.setString(3, adminUser.getPassword());
        stmt.setBoolean(4, adminUser.isEnabled());
        stmt.setString(5, email);
        stmt.execute();
    }

    public AdminUser loadAdminUser(String email) throws UsernameNotFoundException, SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM Admins WHERE Email = ?");
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        AdminUser adminUser = loadAdminUserFromResultSet(rs);
        if (adminUser == null) throw new UsernameNotFoundException("Username not found: " + email);
        return adminUser;
    }

    private AdminUser loadAdminUserFromResultSet(ResultSet rs) throws UsernameNotFoundException, SQLException {
        if (rs.next()) {
            AdminUser adminUser = new AdminUser();
            adminUser.setEmail(rs.getString("Email"));
            adminUser.setPassword(rs.getString("Password"));
            adminUser.setName(rs.getString("Username"));
            adminUser.setEnabled(rs.getBoolean("Enabled"));
            return adminUser;
        }
        return null;
    }

    public boolean adminUserExists(String email) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM Admins WHERE Email = ?");
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }
}
