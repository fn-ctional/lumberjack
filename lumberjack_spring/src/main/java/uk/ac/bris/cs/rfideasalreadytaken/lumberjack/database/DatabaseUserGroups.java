package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseUserGroups {

    @Autowired
    private DatabaseConnection databaseConnection;

    public void insertIntoUserGroups(UserGroup group) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("INSERT INTO UserGroups (id)" +
                "VALUES (?)");
        stmt.setString(1, group.getId());
        stmt.execute();
    }

    public void deleteFromUserGroups(String groupID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("DELETE FROM UserGroups WHERE id = ?");
        stmt.setString(1, groupID);
        stmt.execute();
    }

    public void insertIntoGroupPermissions(GroupPermission groupPermission) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("INSERT INTO GroupPermissions (RuleID, UserGroupID)\n" +
                "VALUES (?,?)");
        stmt.setString(1, groupPermission.getRuleID());
        stmt.setString(2, groupPermission.getUserGroupID());
        stmt.execute();
    }

    public UserGroup loadUserGroupFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            UserGroup userGroup = new UserGroup();
            userGroup.setId(rs.getString("id"));
            return userGroup;
        }
        return null;
    }

    public UserGroup loadUserGroup(String userGroupID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM UserGroups WHERE id = ?");
        stmt.setString(1, userGroupID);
        ResultSet rs = stmt.executeQuery();
        return loadUserGroupFromResultSet(rs);
    }

    public void deleteFromGroupPermissions(String groupPermissionID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("DELETE FROM GroupPermissions WHERE id = ?");
        stmt.setString(1, groupPermissionID);
        stmt.execute();
    }

    public boolean canUserGroupRemoveDevice(Device device, User user) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM GroupPermissions WHERE UserGroupID = ? AND RuleID = ?;");
        stmt.setString(1, user.getGroupId());
        stmt.setString(2, device.getRuleID());
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    public GroupPermission loadGroupPermissionFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            GroupPermission groupPermission = new GroupPermission();
            groupPermission.setId(rs.getString("id"));
            groupPermission.setUserGroupID(rs.getString("UserGroupID"));
            groupPermission.setRuleID(rs.getString("RuleID"));
            return groupPermission;
        }
        return null;
    }

    public GroupPermission loadGroupPermission(String groupPermissionID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM GroupPermissions WHERE id = ?");
        stmt.setString(1, groupPermissionID);
        ResultSet rs = stmt.executeQuery();
        return loadGroupPermissionFromResultSet(rs);
    }

    public GroupPermission loadGroupPermission(String ruleID, String userGroupID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM GroupPermissions WHERE RuleID = ? AND UserGroupID = ?");
        stmt.setString(1, ruleID);
        stmt.setString(2, userGroupID);
        ResultSet rs = stmt.executeQuery();
        return loadGroupPermissionFromResultSet(rs);
    }

    private List<Rule> loadRulesFromResultSet(ResultSet rs) throws SQLException{
        List<Rule> rules = new ArrayList<>();
        while (rs.next()) {
            Rule rule = new Rule();
            rule.setId(rs.getString("id"));
            rule.setMaximumRemovalTime(rs.getInt("MaximumRemovalTime"));
            rules.add(rule);
        }
        return rules;
    }

    public List<Rule> loadGroupRules(String userGroupID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT R.id, " +
                " R.MaximumRemovalTime FROM GroupPermissions INNER JOIN Rules R ON GroupPermissions.RuleID = R.id " +
                "WHERE UserGroupID = ?");
        stmt.setString(1, userGroupID);
        ResultSet rs = stmt.executeQuery();
        return loadRulesFromResultSet(rs);
    }

    private List<UserGroup> loadUserGroupsFromResultSet(ResultSet rs) throws SQLException {
        List<UserGroup> groups = new ArrayList<>();
        while(rs.next()) {
            UserGroup group = new UserGroup();
            group.setId(rs.getString("id"));
            groups.add(group);
        }
        return groups;
    }

    public List<UserGroup> loadGroupsByRule(String ruleID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT UserGroups.id FROM " +
                " UserGroups INNER JOIN GroupPermissions GP ON UserGroups.id = GP.UserGroupID WHERE RuleID = ?");
        stmt.setString(1, ruleID);
        ResultSet rs = stmt.executeQuery();
        return loadUserGroupsFromResultSet(rs);
    }

    public void deletePermissionsByGroup(String groupID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("DELETE FROM GroupPermissions WHERE UserGroupID = ?");
        stmt.setString(1, groupID);
        stmt.execute();
    }

}
