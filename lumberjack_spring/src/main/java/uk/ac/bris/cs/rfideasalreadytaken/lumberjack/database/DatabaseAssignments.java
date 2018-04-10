package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Assignment;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.AssignmentHistory;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Device;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.*;

@Service
public class DatabaseAssignments {

    @Autowired
    private DatabaseConnection databaseConnection;


    public Assignment loadAssignment(Device device) throws SQLException {
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

    public List<Assignment> loadAssignmentsFromResultSet(ResultSet rs) throws SQLException {
        List<Assignment> assignments = new ArrayList<>();
        while (rs.next()) {
            Assignment assignment = new Assignment();
            rs.previous();
            assignment = loadAssignmentFromResultSet(rs);
            assignments.add(assignment);
        }
        return assignments;
    }

    public AssignmentHistory loadAssignmentHistory(Device device) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM AssignmentHistory WHERE DeviceID = ?");
        stmt.setString(1, device.getId());
        ResultSet rs = stmt.executeQuery();
        return loadAssignmentHistoryFromResultSet(rs);
    }

    public AssignmentHistory loadAssignmentHistoryFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            AssignmentHistory assignmentHistory = new AssignmentHistory();
            assignmentHistory.setAssignmentHistoryID(rs.getInt("id"));
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

    public List<AssignmentHistory> loadAssignmentHistoriesFromResultSet(ResultSet rs) throws SQLException {
        List<AssignmentHistory> assignmentHistories = new ArrayList<>();
        while (rs.next()) {
            rs.previous();
            AssignmentHistory assignmentHistory = loadAssignmentHistoryFromResultSet(rs);
            assignmentHistories.add(assignmentHistory);
        }
        return assignmentHistories;
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

    public void deleteFromAssignmentHistory(String assignmentHistoryID) throws SQLException {
            PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("DELETE FROM AssignmentHistory WHERE id = ?");
            stmt.setString(1, assignmentHistoryID);
            stmt.execute();
    }

    public int getAssignmentsTakeoutsByTime(Calendar start, Calendar end) throws SQLException {


        String startDate = start.get(YEAR) + "-" + start.get(MONTH) + "-" + start.get(DATE);
        String startTime = start.get(HOUR_OF_DAY) + ":" + start.get(MINUTE) + ":" + start.get(SECOND);
        String endDate = end.get(YEAR) + "-" + end.get(MONTH) + "-" + end.get(DATE);
        String endTime = end.get(HOUR_OF_DAY) + ":" + end.get(MINUTE) + ":" + end.get(SECOND);

        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM AssignmentHistory WHERE " +
                "DateAssigned >= CAST(? AS DATE) AND DateAssigned <= CAST(? AS DATE)" +
                " AND TimeAssigned >= CAST(? AS TIME) AND TimeAssigned <= CAST(? AS TIME)");

        stmt.setString(1, startDate);
        stmt.setString(2, endDate);
        stmt.setString(3, startTime);
        stmt.setString(4, endTime);

        ResultSet rs = stmt.executeQuery();
        rs.last();
        int history = rs.getRow();

        PreparedStatement stmt2 = databaseConnection.getConnection().prepareStatement("SELECT * FROM Assignments WHERE " +
                "DateAssigned >= CAST(? AS DATE) AND DateAssigned <= CAST(? AS DATE)" +
                " AND TimeAssigned >= CAST(? AS TIME) AND TimeAssigned <= CAST(? AS TIME)");

        stmt2.setString(1, startDate);
        stmt2.setString(2, endDate);
        stmt2.setString(3, startTime);
        stmt2.setString(4, endTime);

        ResultSet rs2 = stmt2.executeQuery();
        rs2.last();
        history += rs2.getRow();

        return history;
    }

    public int getAssignmentsReturnsByTime(Calendar start, Calendar end) throws SQLException {


        String startDate = start.get(YEAR) + "-" + start.get(MONTH) + "-" + start.get(DATE);
        String startTime = start.get(HOUR_OF_DAY) + ":" + start.get(MINUTE) + ":" + start.get(SECOND);
        String endDate = end.get(YEAR) + "-" + end.get(MONTH) + "-" + end.get(DATE);
        String endTime = end.get(HOUR_OF_DAY) + ":" + end.get(MINUTE) + ":" + end.get(SECOND);

        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM AssignmentHistory WHERE " +
                "DateReturned >= CAST(? AS DATE) AND DateReturned <= CAST(? AS DATE)" +
                " AND TimeReturned >= CAST(? AS TIME) AND TimeReturned <= CAST(? AS TIME)");

        stmt.setString(1, startDate);
        stmt.setString(2, endDate);
        stmt.setString(3, startTime);
        stmt.setString(4, endTime);

        ResultSet rs = stmt.executeQuery();
        rs.last();
        int history = rs.getRow();

        return history;
    }

}
