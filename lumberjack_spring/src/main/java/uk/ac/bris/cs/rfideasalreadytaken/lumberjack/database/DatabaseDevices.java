package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.cardreader.data.ScanDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.AssignmentHistory;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Device;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseDevices {

    @Autowired
    private DatabaseConnection databaseConnection;

    @Autowired
    private DatabaseAssignments databaseAssignments;

    public Device loadDevice(ScanDTO scanDTO) throws SQLException {
         PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM Devices WHERE ScanValue = ?");
         stmt.setString(1, scanDTO.getDevice());
         ResultSet rs = stmt.executeQuery();
         Device device = loadDeviceFromResultSet(rs);
         return device;
    }

    public List<AssignmentHistory> loadDeviceAssignmentHistory(Device device) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM AssignmentHistory WHERE DeviceId = ?");
        stmt.setString(1, device.getId());
        ResultSet rs = stmt.executeQuery();
        List<AssignmentHistory> assignmentHistorys = new ArrayList<AssignmentHistory>();
        while (rs.next()) assignmentHistorys.add(databaseAssignments.loadAssignmentHistoryFromResultSet(rs));
        return assignmentHistorys;
    }

    private Device loadDeviceFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            Device device = new Device();
            device.setAvailable(rs.getBoolean("Available"));
            device.setCurrentlyAssigned(rs.getBoolean("CurrentlyAssigned"));
            device.setType(rs.getString("Type"));
            device.setId(rs.getString("id"));
            device.setScanValue(rs.getString("ScanValue"));
            device.setRuleID(rs.getString("RuleID"));
            return device;
        }
        return null;
    }

    public boolean isValidDevice(ScanDTO scanDTO) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT id FROM Devices WHERE ScanValue = ?");
        stmt.setString(1, scanDTO.getDevice());
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    public void insertIntoDevices(Device device) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("INSERT INTO Devices (id, ScanValue, Type, Available, CurrentlyAssigned, RuleID) VALUES (?,?,?,?,?,?)");
        stmt.setString(1, device.getId());
        stmt.setString(2, device.getScanValue());
        stmt.setString(3, device.getType());
        stmt.setBoolean(4, device.isAvailable());
        stmt.setBoolean(5, device.isCurrentlyAssigned());
        stmt.setString(6, device.getRuleID());
        stmt.execute();
    }

    public void deleteFromDevices(String deviceID) throws SQLException {
        PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("DELETE FROM Devices WHERE id = ?");
        stmt.setString(1, deviceID);
        stmt.execute();
    }

    //TODO: I don't think this function has correct operation
    public boolean isDeviceCurrentlyOut(Device device) {
        return device.isCurrentlyAssigned();
    }

    //TODO: I don't think this function has correct operation
    public boolean canDeviceBeRemoved(Device device) {
        return device.isAvailable();
    }


}