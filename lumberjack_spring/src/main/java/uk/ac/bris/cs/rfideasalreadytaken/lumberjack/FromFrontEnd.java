package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;

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

    //------------------------------------------------------------------------------------------------------------------

    int getNumberOfUsers();

    int getNumberOfDevices();

    int getNumberOfDevicesTakenOut();

    //------------------------------------------------------------------------------------------------------------------

    User getUser(String id) throws NotFoundException;

    List<User> getAllUsers();

    List<User> getUsers(List<String> ids) throws NotFoundException;

    //List<User> getUsers(String userGroup) throws NotFoundException;

    //------------------------------------------------------------------------------------------------------------------

    Device getDevice(String id) throws NotFoundException;

    List<Device> getAllDevices();

    List<Device> getDevices(List<String> ids) throws NotFoundException;

    //List<Device> getDevices(String deviceType) throws NotFoundException;

    //------------------------------------------------------------------------------------------------------------------

    //Group functionality not yet implemented at object/database level

    //------------------------------------------------------------------------------------------------------------------

    //Discussion needed for best log implementation

    //------------------------------------------------------------------------------------------------------------------

    //Rules functionality not yet implemented at object/database level

    //------------------------------------------------------------------------------------------------------------------

    void setUserDetails(User user) throws NotFoundException;

    void setDeviceDetails(Device device) throws NotFoundException;

    // Setting groups, deviceTypes and rules not yet implemented

    //------------------------------------------------------------------------------------------------------------------

    void removeUser(User user) throws NotFoundException;

    void removeUsers(List<User> users) throws NotFoundException;

    void removeDevice(Device device) throws NotFoundException;

    void removeDevices(List<Device> devices) throws NotFoundException;

    // Removing groups, deviceTypes and rules not yet implemented

    //------------------------------------------------------------------------------------------------------------------

    void addUser(User user);

    void addUsers(List<User> users);

    void addDevice(Device device);

    void addDevices(List<Device> devices);

    // Adding groups, deviceTypes and rules not yet implemented



}
