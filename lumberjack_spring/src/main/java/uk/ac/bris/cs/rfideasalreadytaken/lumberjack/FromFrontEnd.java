package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;

import java.util.Map;

public interface FromFrontEnd {

    /*

    ----------    Things we need for the front-end    ----------

    - get stats about users, devices etc like how many users are there, how many devices, how many devices taken out etc

    - get single user details (including groups they are in)-
    - get multiple user details (e.g. in a list or something)-
    - get multiple user details filtered by group-

    - get single device details-
    - get multiple device details (e.g. in a list or something)-
    - get multiple device details filtered by group-

    - get single group details
    - get multiple group details

    - getting the log for a user-
    - getting the log for a device-
    - getting the log for a group/subset of users-
    - getting the log for a group/subset of devices-
    - getting the entire log (why not)-

    - get single rule (applied to groups)
    - get all rules

    - edit user details (single and batch)
    - edit device details (single and batch)
    - edit group details (e.g. add users too a group)
    - edit rules

    - delete certain logs (e.g. by device, user, time frame)-
    - delete users-
    - delete devices-
    - delete groups
    - delete rules

    - add users (single and batch)
    - add devices (single and batch)
    - add groups
    - add rules

    */

    void getUserDetails(User user);

    void getUsersDetails(User... users);

    void getUserDetailsFilt(User user);

    void getDeviceDetails(Device device);

    void getDeviceDetails(Device... devices);

    void getDeviceDetailsFilt(Device device);

    void getDeviceLog(Device device);

    void getUserLog(User user);

    void getDeviceLog(Device... devices);

    void getUserLog(User... users);

    void getLog();

    void deleteLog(Device device);

    void deleteLog(User user);

    void deleteLog(Time time);

    void deleteUser(User user);

    void deleteDevice(Device device);

    void deleteGroup();

    void deleteRule();

    void addUser(String... id, String... scanValue, int... deviceLimit, int... devicesRemoved, boolean... canRemove);

   // void addUser(String id, String scanValue, int deviceLimit, int devicesRemoved, boolean canRemove);


    void addDevice(String id, String scanValue, String type, boolean available, boolean currentlyAssigned, String ruleID);

    //void addDevice(String... id, String... scanValue, String... type, boolean... available, boolean... currentlyAssigned, String... ruleID);







    Map<Device,User> getDevicesOut();

    void setUserMaxDevices(User user, int max);

    //void setDeadline(Device device, Date date);

    void setDeviceType(Device device, String type);

    //void getStatus(Device device);

    void insertUser(User user);

    void insertDevice(Device device);

    void deleteUser(User user);

    void deleteDevice(Device device);

   // void resetTable();

    void resetDatabase();

    Class getUserAssignmentsHistory(User user);

  //  void getDeviceAssignmentHistory(Device device);



}
