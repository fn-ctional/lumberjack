package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.Token;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.VerificationToken;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class DatabaseTokens {

    @Autowired
    private DatabaseConnection databaseConnection;

    @Autowired
    private DatabaseAdminUsers databaseAdminUsers;

    public void insertIntoTokens(Token verificationToken) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("INSERT INTO Tokens (Token, AdminEmail, ExpiryDate)" +
                "VALUES (?,?,?)");
        stmt.setString(1, verificationToken.getToken());
        stmt.setString(2, verificationToken.getAdminUser().getEmail());
        stmt.setDate(3, verificationToken.getExpiryDate());
        stmt.execute();
    }

    public Token loadToken(String verificationToken) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM Tokens WHERE Token = ?");
        stmt.setString(1, verificationToken);
        ResultSet rs = stmt.executeQuery();
        return loadTokenFromResultSet(rs);
    }

    private Token loadTokenFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            AdminUser adminUser = databaseAdminUsers.loadAdminUser(rs.getString("AdminEmail"));
            String token = rs.getString("Token");
            return new VerificationToken(token, adminUser);
        }
        return null;
    }

    public void updateToken(String token, Token verificationToken) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("UPDATE Tokens SET Token = ?, AdminEmail = ?, ExpiryDate = ? " +
                "WHERE Token = ?");
        stmt.setString(1, verificationToken.getToken());
        stmt.setString(2, verificationToken.getAdminUser().getEmail());
        stmt.setDate(3, verificationToken.getExpiryDate());
        stmt.setString(5, token);
        stmt.execute();
    }
}
