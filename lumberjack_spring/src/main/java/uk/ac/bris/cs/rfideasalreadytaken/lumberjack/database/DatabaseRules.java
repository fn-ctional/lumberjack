package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Rule;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.UserGroup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public Rule loadRuleFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            Rule rule = new Rule();
            rule.setId(rs.getString("id"));
            rule.setMaximumRemovalTime(rs.getInt("MaximumRemovalTime"));
            return rule;
        }
        return null;
    }

    public Rule loadRule(String ruleID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM Rules WHERE id = ?");
        stmt.setString(1, ruleID);
        ResultSet rs = stmt.executeQuery();
        return loadRuleFromResultSet(rs);
    }

    public boolean groupHasRule(String groupID, String ruleID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM GroupPermissions " +
                "WHERE UserGroupID = ? AND RuleID = ?");
        stmt.setString(1, groupID);
        stmt.setString(2, ruleID);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    public void updateRule(Rule rule) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("UPDATE Rules SET MaximumRemovalTime = ? WHERE id = ?");
        stmt.setInt(1, rule.getMaximumRemovalTime());
        stmt.setString(2, rule.getId());
        stmt.execute();
    }
}
