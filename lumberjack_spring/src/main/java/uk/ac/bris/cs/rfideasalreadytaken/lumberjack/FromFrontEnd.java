package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FromFrontEnd {

    /*

    ----------    Things we need for the front-end    ----------

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
    - edit group details (e.g. add users too a group)
    - edit rules

    - delete certain logs (e.g. by device, user, time frame)
    - delete users
    - delete devices
    - delete groups
    - delete rules

    - add users (single and batch)
    - add devices (single and batch)
    - add groups
    - add rules

    */


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

   // void resetDatabase();

  //  void getUserAssignmentsHistory(User user);

  //  void getDeviceAssignmentHistory(Device device);






//view select record from id
//view all for each table


}
