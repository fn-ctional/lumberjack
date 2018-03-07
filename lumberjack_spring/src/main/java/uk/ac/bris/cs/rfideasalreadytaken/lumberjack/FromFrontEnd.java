package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Rule;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.UserGroup;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.GroupPermission;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Assignment;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.AssignmentHistory;
import java.util.ArrayList;

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

    boolean insertUser(User user) throws Exception;

    boolean insertUsers(ArrayList<User> users) throws Exception;

    ArrayList<AssignmentHistory> getUserAH(User user);

    ArrayList<AssignmentHistory> getUsersAH(ArrayList<User> users);

    boolean insertDevice(Device device) throws Exception;

    boolean insertDevices(ArrayList<Device> devices) throws Exception;

    ArrayList<AssignmentHistory> getDeviceAH(Device device);

    ArrayList<AssignmentHistory> getDevicesAH(ArrayList<Device> devices);

    boolean insertGroupPermission(GroupPermission groupPermission);

    boolean insertRule(Rule rule);

    boolean removeUser(User user) throws Exception;

    boolean resetUsers() throws Exception;

    User getUser(String userID) throws Exception;

    ArrayList<User> getUsers() throws Exception;

    boolean editUser(String userID, User newValue) throws Exception;

    boolean deleteUserGroup(User user) throws Exception;

    boolean deleteDevice(Device device) throws Exception;

    boolean deleteRule(Rule rule) throws Exception;

    boolean deleteAssignment(Assignment assignment) throws Exception;

    boolean deletePermission(GroupPermission groupPermission) throws Exception;

    boolean addUserToGroup(User user, UserGroup group) throws Exception;

    boolean editGroup(ArrayList<User> users, UserGroup group) throws Exception;

    boolean insertUserGroup(UserGroup group);

}
