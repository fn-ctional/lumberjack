package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader.data.ScanDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.AssignmentHistory;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseUsers {

    @Autowired
    private DatabaseConnection databaseConnection;

    @Autowired
    private DatabaseAssignments databaseAssignments;

    public User loadUser(ScanDTO scanDTO) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM Users WHERE ScanValue = ?");
        stmt.setString(1, scanDTO.getUser());
        ResultSet rs = stmt.executeQuery();
        User user = loadUserFromResultSet(rs);
        return user;
    }

    public User loadUser(String userID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM Users WHERE id = ?");
        stmt.setString(1, userID);
        ResultSet rs = stmt.executeQuery();
        return loadUserFromResultSet(rs);
    }


    public List<AssignmentHistory> loadUserAssignmentHistory(String userID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM AssignmentHistory WHERE UserID = ?");
        stmt.setString(1, userID);
        ResultSet rs = stmt.executeQuery();
        ArrayList<AssignmentHistory> assignmentHistories = new ArrayList<AssignmentHistory>();

        rs.last();
        int total = rs.getRow();
        rs.beforeFirst();

        for (int i = 0; i < total; i++) {
            assignmentHistories.add(databaseAssignments.loadAssignmentHistoryFromResultSet(rs));
        }

        return assignmentHistories;
    }

    public User loadUserFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            User user = new User();
            user.setCanRemove(rs.getBoolean("CanRemove"));
            user.setDeviceLimit(rs.getInt("DeviceLimit"));
            user.setDevicesRemoved(rs.getInt("DevicesRemoved"));
            user.setId(rs.getString("id"));
            user.setScanValue(rs.getString("ScanValue"));
            user.setGroupId(rs.getString("GroupID"));
            user.setUsername(rs.getString("Username"));
            return user;
        }
        return null;
    }

    public List<User> loadUsersFromResultSet(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            User user = new User();
            user.setCanRemove(rs.getBoolean("CanRemove"));
            user.setDeviceLimit(rs.getInt("DeviceLimit"));
            user.setDevicesRemoved(rs.getInt("DevicesRemoved"));
            user.setId(rs.getString("id"));
            user.setScanValue(rs.getString("ScanValue"));
            user.setGroupId(rs.getString("GroupID"));
            user.setUsername(rs.getString("Username"));
            users.add(user);
        }
        return users;
    }

    public boolean isValidUser(ScanDTO scanDTO) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT id FROM Users WHERE ScanValue = ?");
        stmt.setString(1, scanDTO.getUser());
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    public void insertIntoUsers(User user) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("INSERT INTO Users (id, ScanValue, Username, DeviceLimit, DevicesRemoved, CanRemove, GroupID)" +
                "VALUES (?,?,?,?,?,?,?)");
        stmt.setString(1, user.getId());
        stmt.setString(2, user.getScanValue());
        stmt.setString(3, user.getUsername());
        stmt.setInt(4, user.getDeviceLimit());
        stmt.setInt(5, user.getDevicesRemoved());
        stmt.setBoolean(6, user.canRemove());
        stmt.setString(7, user.getGroupId());
        stmt.execute();
    }

    public void updateUser(String userID, User user) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("UPDATE Users SET id = ?, ScanValue = ?, Username = ?, DeviceLimit = ?, DevicesRemoved = ?, CanRemove = ?, GroupID = ? " +
                "WHERE id = ?");
        stmt.setString(1, user.getId());
        stmt.setString(2, user.getScanValue());
        stmt.setString(3, user.getUsername());
        stmt.setInt(4, user.getDeviceLimit());
        stmt.setInt(5, user.getDevicesRemoved());
        stmt.setBoolean(6, user.canRemove());
        stmt.setString(7, user.getGroupId());
        stmt.setString(8, userID);
        stmt.execute();
    }

    public void deleteFromUsers(String userID) throws SQLException {
            PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("DELETE FROM Users WHERE id = ?");
            stmt.setString(1, userID);
            stmt.execute();
    }

    public List<User> loadUsersFromUserGroup(String userGroupID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM Users WHERE GroupID = ?");
        stmt.setString(1, userGroupID);
        ResultSet rs = stmt.executeQuery();
        return loadUsersFromResultSet(rs);
    }

    // These two functions do not need to check the database
    public boolean canUserRemoveDevices(User user) {
        return user.canRemove();
    }

    public boolean isUserAtDeviceLimit(User user){
        return user.getDeviceLimit() <= user.getDevicesRemoved();
    }

    public void removeGroupFromUsers(String groupID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("UPDATE Users SET GroupID = NULL WHERE GroupID = ?");
        stmt.setString(1, groupID);
        stmt.execute();
    }

}
