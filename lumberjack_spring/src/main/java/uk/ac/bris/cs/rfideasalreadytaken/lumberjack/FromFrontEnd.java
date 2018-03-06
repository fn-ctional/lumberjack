package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Rule;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.UserGroup;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.GroupPermission;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Assignment;
import java.util.ArrayList;
import java.util.Map;

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

    - getting the log for a user
    - getting the log for a device
    - getting the log for a group/subset of users
    - getting the log for a group/subset of devices
    - getting the entire log (why not)

    - get single rule (applied to groups)
    - get all rules

    - edit user details (single and batch)
    - edit device details (single and batch)
    - edit group details (e.g. add users to a group)---------------
    - edit rules

    - delete certain logs (e.g. by device, user, time frame)
    - delete users--------------
    - delete devices---------
    - delete groups------------
    - delete rules----------

    - add users (single and batch)---------------
    - add devices (single and batch)---------------
    - add groups
    - add rules

    */

    boolean insertUser(User user) throws Exception;

    boolean insertUsers(ArrayList<User> users) throws Exception;

    boolean insertDevice(Device device) throws Exception;

    boolean insertDevices(ArrayList<Device> devices) throws Exception;

    boolean removeUser(User user) throws Exception;

    boolean resetUsers() throws Exception;

    User getUser(String userID) throws Exception;

    ArrayList<User> getUsers() throws Exception;

    boolean editUser(String userID, User newValue) throws Exception;



    //Map<Device,User> getDevicesOut();

    void setUserMaxDevices(User user, int max);

    //void setDeadline(Device device, Date date);

    void setDeviceType(Device device, String type);

    //void getStatus(Device device);


   // void resetTable();

   // void resetDatabase();

  //  void getUserAssignmentsHistory(User user);

  //  void getDeviceAssignmentHistory(Device device);



    boolean deleteUserGroup(User user) throws Exception;

    boolean deleteDevice(Device device) throws Exception;

    boolean deleteRule(Rule rule) throws Exception;

    boolean deleteAssignment(Assignment assignment) throws Exception;

    boolean deletePermission(GroupPermission groupPermission) throws Exception;



    boolean addUserToGroup(User user, UserGroup group) throws Exception;

    boolean editGroup(ArrayList<User> users, UserGroup group) throws Exception;




//view select record from id
//view all for each table


}
