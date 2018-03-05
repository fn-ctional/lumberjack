package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;

import java.util.List;
import java.util.Map;

@Service
public class FrontEndDatabaseManager extends GenericDatabaseManager implements FromFrontEnd {


    public int getNumberOfUsers() {
        return 0;
    }

    public int getNumberOfDevices() {
        return 0;
    }

    public int getNumberOfDevicesTakenOut() {
        return 0;
    }

    public User getUser(String id) throws NotFoundException {
        return null;
    }

    public List<User> getAllUsers() {
        return null;
    }

    public List<User> getUsers(List<String> ids) throws NotFoundException {
        return null;
    }

    public Device getDevice(String id) throws NotFoundException {
        return null;
    }

    public List<Device> getAllDevices() {
        return null;
    }

    public List<Device> getDevices(List<String> ids) throws NotFoundException {
        return null;
    }

    public void setUserDetails(User user) throws NotFoundException {

    }

    public void setDeviceDetails(Device device) throws NotFoundException {

    }

    public void removeUser(User user) throws NotFoundException {

    }

    public void removeUsers(List<User> users) throws NotFoundException {

    }

    public void removeDevice(Device device) throws NotFoundException {

    }

    public void removeDevices(List<Device> devices) throws NotFoundException {

    }

    public void addUser(User user) {

    }

    public void addUsers(List<User> users) {

    }

    public void addDevice(Device device) {

    }

    public void addDevices(List<Device> devices) {

    }
}
