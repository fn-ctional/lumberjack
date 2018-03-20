package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.User;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Rule;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.UserGroup;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.GroupPermission;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Assignment;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.AssignmentHistory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WebBackend implements FromFrontEnd {

    @Autowired
    private DatabaseConnection databaseConnection;

    @Autowired
    private DatabaseUsers databaseUsers;

    @Autowired
    private DatabaseUserGroups databaseUserGroups;

    @Autowired
    private DatabaseDevices databaseDevices;

    @Autowired
    private DatabaseRules databaseRules;

    @Autowired
    private DatabaseAssignments databaseAssignments;

    //Tested
    public void insertUser(User user) throws SQLException {
        databaseUsers.insertIntoUsers(user);
    }

    //Tested
    public void insertUsers(List<User> users) throws SQLException {
        for (User user : users) {
            insertUser(user);
        }
    }

    //Tested
    public User getUser(String userID) throws SQLException {
        return databaseUsers.loadUser(userID);
    }

    //Tested
    public List<User> getUsers() throws SQLException {
        List<User> users = new ArrayList<User>();
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM Users");
        ResultSet rs = stmt.executeQuery();

        rs.last();
        int total = rs.getRow();
        rs.beforeFirst();

        for (int i = 0; i < total; i++) {
            users.add(databaseUsers.loadUserFromResultSet(rs));
        }

        return users;
    }

    //Tested
    public void deleteUser(String userID) throws Exception {
        try {
            databaseUsers.deleteFromUsers(userID);
        }
            catch (SQLException e)
        {}
    }

    //Tested
    public void resetUsers() throws SQLException {
        List<User> users = getUsers();
        for (User user : users) {
            try {
                databaseUsers.deleteFromUsers(user.getId());
            }
            catch (SQLException e)
            {}
        }
    }

    //Tested
    public void insertDevice(Device device) throws SQLException {
        databaseDevices.insertIntoDevices(device);
    }

    //Tested
    public void insertDevices(List<Device> devices) throws SQLException {
        for (Device device : devices) {
            insertDevice(device);
        }
    }

    public void insertUserGroup(UserGroup group) throws SQLException {
        databaseUserGroups.insertIntoUserGroups(group);
    }

    public void insertRule(Rule rule) throws Exception {
        databaseRules.insertIntoRules(rule);
    }

    public void insertGroupPermission(GroupPermission groupPermission) throws Exception {
        databaseUserGroups.insertIntoGroupPermissions(groupPermission);
    }

    public Device getDevice(String deviceID) throws SQLException {
        return databaseDevices.loadDevice(deviceID);
    }

    public void editUser(String userID, User newValue) throws SQLException {
        databaseUsers.updateUser(userID, newValue);
    }

    public List<AssignmentHistory> getUserAH(User user) throws SQLException{
        return databaseUsers.loadUserAssignmentHistory(user);
    }

    public List<AssignmentHistory> getUsersAH(ArrayList<User> users) throws Exception {
        List<AssignmentHistory> assignmentHistories = new ArrayList<AssignmentHistory>();
        for (User user : users) {
            assignmentHistories.addAll(databaseUsers.loadUserAssignmentHistory(user));
        }
        return assignmentHistories;
    }


    public List<AssignmentHistory> getDeviceAH(Device device) throws SQLException {
        return databaseDevices.loadDeviceAssignmentHistory(device);
    }

    public List<AssignmentHistory> getDevicesAH(ArrayList<Device> devices) throws Exception {
        List<AssignmentHistory> assignmentHistories = new ArrayList<AssignmentHistory>();
        for (Device device : devices) {
            assignmentHistories.addAll(databaseDevices.loadDeviceAssignmentHistory(device));
        }
        return assignmentHistories;
    }


    public void deleteDevice(Device device) throws SQLException {
        databaseDevices.deleteFromDevices(device.getId());
    }

    public void setDeviceType(Device device, String type) {
        device.setType(type);
    }

    public void setUserMaxDevices(User user, int max) {
        user.setDeviceLimit(max);
    }

    public void deleteUserGroup(User user) throws SQLException {
        databaseUserGroups.deleteFromUserGroups(user.getId());
    }

    public void deleteRule(Rule rule) throws Exception {
        databaseRules.deleteFromRules(rule.getId());
    }

    public void deleteAssignment(Assignment assignment) throws Exception {
        databaseAssignments.deleteFromAssignments(assignment.getId());
    }

    public void deletePermission(GroupPermission groupPermission) throws Exception {
        databaseUserGroups.deleteFromGroupPermissions(groupPermission.getId());
    }

    public void addUserToGroup(User user, UserGroup group) {
        user.setGroupId(group.getId());
    }

    public void editGroup(ArrayList<User> users, UserGroup group) {
        for (User user : users) {
            addUserToGroup(user, group);
        }
    }
}
