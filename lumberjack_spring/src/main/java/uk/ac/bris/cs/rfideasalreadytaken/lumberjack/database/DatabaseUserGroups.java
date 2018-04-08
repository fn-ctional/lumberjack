package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.GroupPermission;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.User;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.UserGroup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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



}
