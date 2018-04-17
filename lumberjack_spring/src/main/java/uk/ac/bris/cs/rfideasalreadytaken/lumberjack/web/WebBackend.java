package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.User;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Rule;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.UserGroup;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.GroupPermission;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Assignment;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.AssignmentHistory;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.FileUploadException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Calendar.*;
import java.util.List;
import java.util.UUID;

import static java.util.Calendar.HOUR;
import static java.util.Calendar.MONTH;
import static org.springframework.security.config.Elements.HEADERS;

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

    //Tested
    public void insertRule(Rule rule) throws Exception {
        databaseRules.insertIntoRules(rule);
    }

    //Tested
    public void deleteRule(String rule) throws Exception {
        databaseRules.deleteFromRules(rule);
    }

    //Tested
    public Rule getRule(String ruleID) throws SQLException {
        return databaseRules.loadRule(ruleID);
    }

    //Tested
    public List<Rule> getRules() throws SQLException {
        List<Rule> rules = new ArrayList<>();
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM Rules");
        ResultSet rs = stmt.executeQuery();

        rs.last();
        int total = rs.getRow();
        rs.beforeFirst();

        for (int i = 0; i < total; i++) {
            rules.add(databaseRules.loadRuleFromResultSet(rs));
        }

        return rules;
    }

    //Tested
    public void insertGroupPermission(GroupPermission groupPermission) throws Exception {
        databaseUserGroups.insertIntoGroupPermissions(groupPermission);
    }

    //Tested
    public void deleteGroupPermission(String groupPermissionID) throws Exception {
        databaseUserGroups.deleteFromGroupPermissions(groupPermissionID);
    }

    //Tested
    public GroupPermission getGroupPermission(String ruleID, String userGroupID) throws SQLException {
        return databaseUserGroups.loadGroupPermission(ruleID,userGroupID);
    }

    //Tested
    public GroupPermission getGroupPermission(String permissionID) throws SQLException {
        return databaseUserGroups.loadGroupPermission(permissionID);
    }

    //Tested
    public List<GroupPermission> getGroupPermissions() throws SQLException {
        List<GroupPermission> permissions = new ArrayList<>();
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM GroupPermissions");
        ResultSet rs = stmt.executeQuery();

        rs.last();
        int total = rs.getRow();
        rs.beforeFirst();

        for (int i = 0; i < total; i++) {
            permissions.add(databaseUserGroups.loadGroupPermissionFromResultSet(rs));
        }

        return permissions;
    }

    //Tested
    public List<AssignmentHistory> getUserAssignmentHistory(String userID) throws SQLException{
        return databaseUsers.loadUserAssignmentHistory(userID);
    }

    //Tested
    public List<AssignmentHistory> getDeviceAssignmentHistory(String deviceID) throws SQLException {
        return databaseDevices.loadDeviceAssignmentHistory(deviceID);
    }


    public List<User> parseUserCSV(MultipartFile csv) throws FileUploadException {
        Iterable<CSVRecord> records = multipartFileToRecords(csv);

        List<User> newUsers = new ArrayList<>();
        for (CSVRecord record : records) {
            User newUser = new User();

            newUser.setScanValue(record.get("scan value"));
            try {
                newUser.setDeviceLimit(Integer.parseInt(record.get("device limit")));
            } catch (NumberFormatException e) {
                newUser.setDeviceLimit(0);
            }
            try {
                newUser.setDevicesRemoved(Integer.parseInt(record.get("devices removed")));
            } catch (NumberFormatException e) {
                newUser.setDevicesRemoved(0);
            }
            newUser.setCanRemove(Boolean.parseBoolean(record.get("can remove")));
            newUser.setGroupId(record.get("group id"));
            newUser.setId(UUID.randomUUID().toString());

            newUsers.add(newUser);
        }

        return newUsers;
    }

    public List<Device> parseDeviceCSV(MultipartFile csv) throws FileUploadException {
        Iterable<CSVRecord> records = multipartFileToRecords(csv);


        List<Device> newDevices = new ArrayList<>();
        for (CSVRecord record : records) {
            Device newDevice = new Device();

            newDevice.setScanValue(record.get("scan value"));
            newDevice.setAvailable(Boolean.parseBoolean(record.get("can remove")));
            newDevice.setRuleID(record.get("rule id"));
            newDevice.setCurrentlyAssigned(Boolean.parseBoolean(record.get("can remove")));
            newDevice.setType(record.get("scan value"));
            newDevice.setId(UUID.randomUUID().toString());

        newDevices.add(newDevice);
        }

        return newDevices;
    }

    private Iterable<CSVRecord> multipartFileToRecords(MultipartFile csv) throws FileUploadException {
        try {
            File file = new File(csv.getOriginalFilename());
            csv.transferTo(file);
            Reader in = new FileReader(file);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader(HEADERS)
                    .withFirstRecordAsHeader()
                    .parse(in);
            return records;
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileUploadException();
        }
    }

    public String getUsersCSV() throws SQLException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id,");
        stringBuilder.append("scan value,");
        stringBuilder.append("device limit,");
        stringBuilder.append("devices removed,");
        stringBuilder.append("can remove,");
        stringBuilder.append("group id");
        stringBuilder.append("\n");

        List<User> users = getUsers();
        for (User user : users) {

            stringBuilder.append(user.getId());
            stringBuilder.append(",");
            stringBuilder.append(user.getScanValue());
            stringBuilder.append(",");
            stringBuilder.append(user.getDeviceLimit());
            stringBuilder.append(",");
            stringBuilder.append(user.getDevicesRemoved());
            stringBuilder.append(",");
            stringBuilder.append(user.canRemove());
            stringBuilder.append(",");
            stringBuilder.append(user.getGroupId());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public String getDevicesCSV() throws SQLException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id,");
        stringBuilder.append("scan value,");
        stringBuilder.append("type,");
        stringBuilder.append("available,");
        stringBuilder.append("currently assigned,");
        stringBuilder.append("rule id");
        stringBuilder.append("\n");

        List<Device> devices = getDevices();
        for (Device device : devices) {
            stringBuilder.append(device.getId());
            stringBuilder.append(",");
            stringBuilder.append(device.getScanValue());
            stringBuilder.append(",");
            stringBuilder.append(device.getType());
            stringBuilder.append(",");
            stringBuilder.append(device.isAvailable());
            stringBuilder.append(",");
            stringBuilder.append(device.isCurrentlyAssigned());
            stringBuilder.append(",");
            stringBuilder.append(device.getRuleID());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public List<User> getGroupUsers(String userGroupID) throws SQLException {
        return databaseUsers.loadUsersFromUserGroup(userGroupID);
    }

    public List<Rule> getGroupRules(String userGroupID) throws SQLException {
        return databaseUserGroups.loadGroupRules(userGroupID);
    }

    public int getAvailableCount() throws SQLException {
        return databaseDevices.getAvailableCount();
    }

    public int getTakenCount() throws SQLException {
        return databaseDevices.getTakenCount();
    }

    public int getOtherCount() throws SQLException {
        return databaseDevices.getOtherCount();
    }

    //Tested
    public List<Integer> getRecentTakeouts(int hoursToGet) throws SQLException {

        Calendar end = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.add(HOUR, -hoursToGet);
        end.add(HOUR, -(hoursToGet-1));

        start.add(MONTH, 1);
        end.add(MONTH, 1);

        List<Integer> takeouts = new ArrayList<>();

        for(int i = -(hoursToGet); i != 0; i++)
        {
            takeouts.add(databaseAssignments.getAssignmentsTakeoutsByTime(start, end));
            start.add(HOUR,1);
            end.add(HOUR,1);
        }

        return takeouts;
    }

    //Tested
    public List<Integer> getRecentReturns(int hoursToGet) throws SQLException {

        Calendar end = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.add(HOUR, -hoursToGet);
        end.add(HOUR, -(hoursToGet-1));

        start.add(MONTH, 1);
        end.add(MONTH, 1);

        List<Integer> returns = new ArrayList<>();

        for(int i = -(hoursToGet); i != 0; i++)
        {
            returns.add(databaseAssignments.getAssignmentsReturnsByTime(start, end));
            start.add(HOUR,1);
            end.add(HOUR,1);
        }

        return returns;
    }

    public List<String> getTimes(int hoursToGet) {
        Calendar end = Calendar.getInstance();
        end.add(HOUR, -(hoursToGet-1));

        end.add(MONTH, 1);

        List<String> times = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        for(int i = -(hoursToGet); i != 0; i++)
        {
            String time = sdf.format(end.getTime());
            times.add(time);
            end.add(HOUR,1);
        }

        return times;
    }

    public List<UserGroup> getUserGroupsByRule(String ruleID) throws SQLException {
        return databaseUserGroups.loadGroupsByRule(ruleID);
    }

    public List<Device> getDevicesByRule(String ruleID) throws SQLException {
        return databaseDevices.loadDevicesByRule(ruleID);
    }

    public boolean userHasOutstandingDevices(String userID) throws SQLException {
        List<Device> devices = databaseDevices.loadOutstandingDevicesByUser(userID);
        return !devices.isEmpty();
    }

    public void deleteAssignmentHistoryByUser(String userID) throws SQLException {
        databaseAssignments.deleteFromAssignmentHistoryByUser(userID);
    }

    public boolean deviceIsOut(String deviceID) throws SQLException {
        return databaseDevices.deviceIsOut(deviceID);
    }

    public void deleteAssignmentHistoryByDevice(String deviceID) throws SQLException {
        databaseAssignments.deleteFromAssignmentHistoryByDevice(deviceID);
    }

}
