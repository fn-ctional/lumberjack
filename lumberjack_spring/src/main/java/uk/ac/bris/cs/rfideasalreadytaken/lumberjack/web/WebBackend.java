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
    public void editUser(String userID, User newValue) throws SQLException {
        databaseUsers.updateUser(userID, newValue);
    }

    //Tested
    public void changeUserGroup(User user, UserGroup group) throws Exception{
        user.setGroupId(group.getId());
        databaseUsers.updateUser(user.getId(), user);
    }

    //Tested
    public void changeUsersGroup(List<User> users, UserGroup group) throws Exception{
        for (User user : users) {
            changeUserGroup(user, group);
        }
    }

    //Tested
    public void setUserMaxDevices(User user, int max) throws Exception {
        user.setDeviceLimit(max);
        databaseUsers.updateUser(user.getId(), user);
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

    //Tested
    public Device getDevice(String deviceID) throws SQLException {
        return databaseDevices.loadDevice(deviceID);
    }

    //Tested
    public List<Device> getDevices() throws SQLException {
        List<Device> devices = new ArrayList<>();
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM Devices");
        ResultSet rs = stmt.executeQuery();

        rs.last();
        int total = rs.getRow();
        rs.beforeFirst();

        for (int i = 0; i < total; i++) {
            devices.add(databaseDevices.loadDeviceFromResultSet(rs));
        }

        return devices;
    }

    //Tested
    public void editDevice(String deviceID, Device newValue) throws SQLException {
        databaseDevices.updateDevice(deviceID, newValue);
    }

    //Tested
    public void setDeviceType(Device device, String type) throws Exception{
        device.setType(type);
        databaseDevices.updateDevice(device.getId(), device);
    }

    //Tested
    public void deleteDevice(String deviceID) throws SQLException {
        databaseDevices.deleteFromDevices(deviceID);
    }

    //Tested
    public void resetDevices() throws SQLException {
        List<Device> devices = getDevices();
        for (Device device : devices) {
            try {
                databaseDevices.deleteFromDevices(device.getId());
            }
            catch (SQLException e)
            {}
        }
    }

    //Tested
    public void deleteUserGroup(String group) throws SQLException {
        databaseUserGroups.deleteFromUserGroups(group);
    }
    
    //Tested
    public void insertUserGroup(UserGroup group) throws SQLException {
        databaseUserGroups.insertIntoUserGroups(group);
    }

    //Tested
    public UserGroup getUserGroup(String userGroupID) throws SQLException {
        return databaseUserGroups.loadUserGroup(userGroupID);
    }

    //Tested
    public List<UserGroup> getUserGroups() throws SQLException {
        List<UserGroup> userGroups = new ArrayList<>();
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM UserGroups");
        ResultSet rs = stmt.executeQuery();

        rs.last();
        int total = rs.getRow();
        rs.beforeFirst();

        for (int i = 0; i < total; i++) {
            userGroups.add(databaseUserGroups.loadUserGroupFromResultSet(rs));
        }

        return userGroups;
    }

    public void insertRule(Rule rule) throws Exception {
        databaseRules.insertIntoRules(rule);
    }

    public void insertGroupPermission(GroupPermission groupPermission) throws Exception {
        databaseUserGroups.insertIntoGroupPermissions(groupPermission);
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

    public void deleteRule(Rule rule) throws Exception {
        databaseRules.deleteFromRules(rule.getId());
    }

    public void deleteAssignment(Assignment assignment) throws Exception {
        databaseAssignments.deleteFromAssignments(assignment.getId());
    }

    public void deletePermission(GroupPermission groupPermission) throws Exception {
        databaseUserGroups.deleteFromGroupPermissions(groupPermission.getId());
    }
}
