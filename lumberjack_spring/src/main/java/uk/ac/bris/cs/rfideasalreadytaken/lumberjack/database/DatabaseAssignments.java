package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Assignment;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.AssignmentHistory;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class DatabaseAssignments {

    @Autowired
    private DatabaseConnection databaseConnection;


    protected Assignment loadAssignment(Device device) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM Assignments WHERE DeviceID = ?");
        stmt.setString(1, device.getId());
        ResultSet rs = stmt.executeQuery();
        return loadAssignmentFromResultSet(rs);
    }

    public Assignment loadAssignmentFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            Assignment assignment = new Assignment();
            assignment.setDeviceID(rs.getString("DeviceID"));
            assignment.setUserID(rs.getString("UserID"));
            assignment.setDateAssigned(rs.getDate("DateAssigned"));
            assignment.setAssignmentID(rs.getInt("id"));
            assignment.setTimeAssigned(rs.getTime("TimeAssigned"));
            return assignment;
        }
        return null;
    }


    protected AssignmentHistory loadAssignmentHistory(Device device) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM AssignmentHistory WHERE DeviceID = ?");
        stmt.setString(1, device.getId());
        ResultSet rs = stmt.executeQuery();
        return loadAssignmentHistoryFromResultSet(rs);
    }

    public AssignmentHistory loadAssignmentHistoryFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            AssignmentHistory assignmentHistory = new AssignmentHistory();
            assignmentHistory.setAssignmentHistoryID(rs.getString("id"));
            assignmentHistory.setDeviceID(rs.getString("DeviceID"));
            assignmentHistory.setUserID(rs.getString("UserID"));
            assignmentHistory.setDateAssigned(rs.getDate("DateAssigned"));
            assignmentHistory.setTimeAssigned(rs.getTime("TimeAssigned"));
            assignmentHistory.setDateReturned(rs.getDate("DateReturned"));
            assignmentHistory.setTimeReturned(rs.getTime("TimeReturned"));
            assignmentHistory.setReturnedOnTime(rs.getBoolean("ReturnedOnTime"));
            assignmentHistory.setReturnedByID(rs.getString("ReturnedBy"));
            return assignmentHistory;
        }
        return null;
    }

    public boolean checkIfReturnedOnTime(Assignment assignment) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT MaximumRemovalTime FROM Rules INNER JOIN Devices ON Rules.id = Devices.RuleID WHERE Devices.id = ?;");
        stmt.setString(1, assignment.getDeviceID());
        ResultSet rs = stmt.executeQuery();
        if(rs.next()){

            int removalTime = rs.getInt("MaximumRemovalTime");

            long timeOut = Calendar.getInstance().getTime().getTime() - assignment.getDateAssigned().getTime() - assignment.getTimeAssigned().getTime() - 3600000;

            return timeOut < removalTime * 3600000;

        }
        return false;
    }

        public void insertIntoAssignments(Assignment assignment) throws SQLException {
            PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("INSERT INTO Assignments (DeviceID, UserID, DateAssigned, TimeAssigned)\n" +
                    "VALUES (?,?,?,?)");
            stmt.setString(1, assignment.getDeviceID());
            stmt.setString(2, assignment.getUserID());
            stmt.setDate(3, assignment.getDateAssigned());
            stmt.setTime(4, assignment.getTimeAssigned());
            stmt.execute();
    }

    public void deleteFromAssignments(int assignmentID) throws SQLException {
            PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("DELETE FROM Assignments WHERE id = ?");
            stmt.setInt(1, assignmentID);
            stmt.execute();
    }

    public void insertIntoAssignmentHistory(Assignment assignment, String returningUserID, boolean returnedOnTime) throws SQLException {

            PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("INSERT INTO AssignmentHistory (DeviceID, UserID, DateAssigned, TimeAssigned, DateReturned, TimeReturned, ReturnedOnTime, ReturnedBy)" +
                    "VALUES (?,?,?,?,?,?,?,?)");

            java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
            java.sql.Time time = new java.sql.Time(Calendar.getInstance().getTime().getTime());

            stmt.setString(1, assignment.getDeviceID());
            stmt.setString(2, assignment.getUserID());
            stmt.setDate(3, assignment.getDateAssigned());
            stmt.setTime(4, assignment.getTimeAssigned());
            stmt.setDate(5, date);
            stmt.setTime(6, time);
            stmt.setBoolean(7, returnedOnTime);
            stmt.setString(8, returningUserID);

            stmt.execute();
    }

    protected void deleteFromAssignmentHistory(String assignmentHistoryID) throws SQLException {
            PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("DELETE FROM AssignmentHistory WHERE id = ?");
            stmt.setString(1, assignmentHistoryID);
            stmt.execute();
    }


}
