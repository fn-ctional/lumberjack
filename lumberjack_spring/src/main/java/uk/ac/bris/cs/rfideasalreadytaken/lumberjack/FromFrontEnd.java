package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FromFrontEnd {

    /* Functions we need for the front-end

    

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
