package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Rule;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class DatabaseRules {

    @Autowired
    private DatabaseConnection databaseConnection;

    public void insertIntoRules(Rule rule) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("INSERT INTO Rules (id, MaximumRemovalTime)" +
                "VALUES (?,?)");
        stmt.setString(1, rule.getId());
        stmt.setInt(2, rule.getMaximumRemovalTime());
        stmt.execute();
    }

    public void deleteFromRules(String ruleID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("DELETE FROM Rules WHERE id = ?");
        stmt.setString(1, ruleID);
        stmt.execute();
    }
}
