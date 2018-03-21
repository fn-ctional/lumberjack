package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.User;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Rule;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.UserGroup;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.GroupPermission;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Assignment;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.AssignmentHistory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface FromFrontEnd {

    /*

    ----------    Things we need for the front-end    ----------

    - get stats about users, devices etc like how many users are there, how many devices, how many devices taken out etc

    - get single user details (including groups they are in)
    - get multiple user details (e.g. in a list or something)
    - get multiple user details filtered by group

    - get single device details
    - get multiple device details (e.g. in a list or something)
    - get multiple device details filtered by group

    - get single group details
    - get multiple group details

    - getting the log for a user-----------------------------
    - getting the log for a device---------------------------
    - getting the log for a subset of users-------------
    - getting the log for a subset of devices----------
    - getting the entire log (why not)

    - get single rule (applied to groups)
    - get all rules

    - edit user details (single and batch)
    - edit device details (single and batch)
    - edit group details (e.g. add users to a group)---------------
    - edit rules

    - delete certain logs (e.g. by device, user, time frame)
    - delete users--------------
    - delete devices----------
    - delete groups------------
    - delete rules----------

    - add users (single and batch)---------------
    - add devices (single and batch)---------------
    - add groups---------------
    - add rules------


    */

    void insertUser(User user) throws Exception;

    void insertUsers(List<User> users) throws Exception;

    List<AssignmentHistory> getUserAH(User user) throws Exception;

    List<AssignmentHistory> getUsersAH(ArrayList<User> users) throws Exception;

    void insertDevice(Device device) throws Exception;

    void insertDevices(List<Device> devices) throws Exception;

    List<AssignmentHistory> getDeviceAH(Device device) throws Exception;

    List<AssignmentHistory> getDevicesAH(ArrayList<Device> devices) throws Exception;

    void insertGroupPermission(GroupPermission groupPermission) throws Exception;

    void insertRule(Rule rule) throws Exception;

    void deleteUser(String userID) throws Exception;

    void resetUsers() throws Exception;

    User getUser(String userID) throws Exception;

    List<User> getUsers() throws Exception;

    UserGroup getUserGroup(String userGroupID) throws SQLException;

    List<UserGroup> getUserGroups() throws SQLException;

    void editUser(String userID, User newValue) throws Exception;

    void deleteUserGroup(String group) throws Exception;

    void deleteDevice(String deviceID) throws Exception;

    void deleteRule(Rule rule) throws Exception;

    void deleteAssignment(Assignment assignment) throws Exception;

    void deletePermission(GroupPermission groupPermission) throws Exception;

    void changeUserGroup(User user, UserGroup group) throws Exception;

    void changeUsersGroup(List<User> users, UserGroup group) throws Exception;

    void insertUserGroup(UserGroup group) throws Exception;

}
