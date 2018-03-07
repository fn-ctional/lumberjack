package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Rule;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.UserGroup;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.GroupPermission;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Assignment;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.AssignmentHistory;

import javax.validation.constraints.Null;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BackendFrontEndManager extends BackendDatabaseLoading implements FromFrontEnd {

    public boolean insertUser(User user) throws Exception{

        connectToDatabase();

        try {
            insertIntoUsers(user);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public boolean insertUsers(ArrayList<User> users) throws Exception{

        connectToDatabase();

        try {
            for(int i = 0; i < users.size(); i++) {
                insertUser(users.get(i));
            }
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public boolean insertDevice(Device device) throws Exception{

        connectToDatabase();

        try {
            insertIntoDevices(device);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public boolean insertDevices(ArrayList<Device> devices) throws Exception{

        connectToDatabase();

        try {
            for(int i = 0; i < devices.size(); i++) {
                insertDevice(devices.get(i));
            }
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public boolean insertUserGroup(UserGroup group){
        try {
            insertIntoUserGroups(group);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public boolean insertRule(Rule rule){
        try {
            insertIntoRules(rule);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public boolean insertGroupPermission(GroupPermission groupPermission){
        try {
            insertIntoGroupPermissions(groupPermission);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }



    public boolean removeUser(User user) throws Exception{

        connectToDatabase();

        try {
            deleteFromUsers(user.getId());
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public boolean resetUsers() throws Exception{

        connectToDatabase();

        try {
            ArrayList<User> users = getUsers();
            for(int i = 0; i < users.size(); i++) {
                deleteFromUsers(users.get(i).getId());
            }
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public User getUser(String userID) throws Exception{

        connectToDatabase();

        try {
            User user = loadUser(userID);
            return user;
        }
        catch (Exception e){
            return new User();
        }
    }

    public ArrayList<User> getUsers() throws Exception{

        connectToDatabase();

        try {
            ArrayList<User> users = new ArrayList<User>();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users");
            ResultSet rs = stmt.executeQuery();

            rs.last();
            int total = rs.getRow();
            rs.beforeFirst();

            for(int i = 0; i < total; i++) {
                users.add(loadUserFromResultSet(rs));
            }

            return users;
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }

    public boolean editUser(String userID, User newValue) throws Exception {

        connectToDatabase();

        try {
            updateUser(userID, newValue);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public ArrayList<AssignmentHistory> getUserAH(User user){
        try {
            ArrayList<AssignmentHistory> assignmentHistorys = new ArrayList<AssignmentHistory>();
        assignmentHistorys = loadUserAssignmentHistory(user);
        return assignmentHistorys;
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }

    public ArrayList<AssignmentHistory> getUsersAH(ArrayList<User> users){
        try {
            ArrayList<AssignmentHistory> assignmentHistorys = new ArrayList<AssignmentHistory>();
            for(int i = 0; i < users.size(); i++) {
                assignmentHistorys.addAll(loadUserAssignmentHistory(users.get(i)));
            }
            return assignmentHistorys;
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }


    public ArrayList<AssignmentHistory> getDeviceAH(Device device){
        try {
            ArrayList<AssignmentHistory> assignmentHistorys = new ArrayList<AssignmentHistory>();
            assignmentHistorys = loadDeviceAssignmentHistory(device);
            return assignmentHistorys;
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }

   public ArrayList<AssignmentHistory> getDevicesAH(ArrayList<Device> devices){
        try {
            ArrayList<AssignmentHistory> assignmentHistorys = new ArrayList<AssignmentHistory>();
            for(int i = 0; i < devices.size(); i++) {
                assignmentHistorys.addAll(loadDeviceAssignmentHistory(devices.get(i)));
            }
            return assignmentHistorys;
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }





    public boolean deleteDevice(Device device){


        try{
        deleteFromDevices(device.getId());
        return true;
    } catch (Exception e) {
        return false;
    }
    }


    public void setDeviceType(Device device, String type){
        device.setType(type);
    }

    public void setUserMaxDevices(User user, int max){
        user.setDeviceLimit(max);
    }


    public boolean deleteUserGroup(User user) throws Exception {
        try {
            deleteFromUserGroups(user.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteRule(Rule rule) throws Exception {
        try {
            deleteFromRules(rule.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteAssignment(Assignment assignment) throws Exception {
        try {
            deleteFromAssignments(assignment.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deletePermission(GroupPermission groupPermission) throws Exception {
        try {
            deleteFromGroupPermissions(groupPermission.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addUserToGroup(User user, UserGroup group) throws Exception {
        try {
            user.setGroupId(group.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean editGroup(ArrayList<User> users, UserGroup group) throws Exception {
        try {
            boolean hold = false;
            for (int i = 0; i < users.size(); i++) {
                hold = addUserToGroup(users.get(i), group);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
