package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class DatabaseEmails {

    @Autowired
    private DatabaseConnection databaseConnection;

    public boolean isEmailPermitted(String email) throws SQLException {
        PreparedStatement stmt1 = databaseConnection.getConnection().prepareStatement("SELECT * FROM PermittedEmails ");
        ResultSet rs1 = stmt1.executeQuery();
        if (!rs1.next()) return true;

        PreparedStatement stmt2 = databaseConnection.getConnection().prepareStatement("SELECT * FROM PermittedEmails WHERE Email = ?");
        stmt2.setString(1, email);
        ResultSet rs2 = stmt2.executeQuery();
        return rs2.next();
    }

}
