package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class DatabaseUtility {

    @Autowired
    private DatabaseConnection databaseConnection;

    public void resetDatabase() throws SQLException {
        Statement stmt = databaseConnection.getConnection().createStatement();

        stmt.execute("DROP TABLE IF EXISTS AssignmentHistory");
        stmt.execute("DROP TABLE IF EXISTS Assignments");
        stmt.execute("DROP TABLE IF EXISTS Users");
        stmt.execute("DROP TABLE IF EXISTS Devices");
        stmt.execute("DROP TABLE IF EXISTS GroupPermissions");
        stmt.execute("DROP TABLE IF EXISTS Rules");
        stmt.execute("DROP TABLE IF EXISTS UserGroups");
        stmt.execute("DROP TABLE IF EXISTS PermittedEmails");
        stmt.execute("DROP TABLE IF EXISTS Tokens");
        stmt.execute("DROP TABLE IF EXISTS Admins");

        stmt.execute("CREATE TABLE IF NOT EXISTS Admins (" +
                "\nEmail varchar(100)," +
                "\nUsername varchar(100) NOT NULL," +
                "\nPassword varchar(60) NOT NULL," +
                "\nEnabled bit," +
                "\nPRIMARY KEY (Email));");

        stmt.execute("CREATE TABLE IF NOT EXISTS Tokens (" +
                "\nToken varchar(100)," +
                "\nAdminEmail varchar(100) NOT NULL," +
                "\nExpiryDate DATE," +
                "\nCONSTRAINT FOREIGN KEY (AdminEmail) REFERENCES Admins(Email), " +
                //There was once a lot of foriegn key errors,
                //I commented out this line,
                //And then it worked fine
                //I then uncommneted it and it still worked fine
                //
                // so maybe this line doesnt work just fyi
                "\nPRIMARY KEY (Token));");

        stmt.execute("CREATE TABLE IF NOT EXISTS UserGroups (" +
                "\nid varchar(100)," +
                "\nPRIMARY KEY (id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS Rules (" +
                "\nid varchar(100)," +
                "\nMaximumRemovalTime int," +
                "\nPRIMARY KEY (id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS GroupPermissions (" +
                "\nid int AUTO_INCREMENT,\n" +
                "\nRuleID varchar(100) NOT NULL," +
                "\nUserGroupID varchar(100) NOT NULL," +
                "\nPRIMARY KEY (id)," +
                "\nCONSTRAINT FOREIGN KEY (UserGroupID) REFERENCES UserGroups(id)," +
                "\nCONSTRAINT FOREIGN KEY (RuleID) REFERENCES Rules(id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS Users (" +
                "\nid varchar(100)," +
                "\nScanValue varchar(100) NOT NULL UNIQUE," +
                "\nDeviceLimit int," +
                "\nDevicesRemoved int," +
                "\nCanRemove bit," +
                "\nGroupID varchar(100)," +
                "\nCONSTRAINT FOREIGN KEY (GroupID) REFERENCES UserGroups(id)," +
                "\nPRIMARY KEY (id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS Devices (" +
                "\nid varchar(100)," +
                "\nScanValue varchar(100) NOT NULL UNIQUE," +
                "\nType varchar(100)," +
                "\nAvailable bit," +
                "\nCurrentlyAssigned bit," +
                "\nRuleID varchar(100)," +
                "\nCONSTRAINT FOREIGN KEY (RuleID) REFERENCES Rules(id)," +
                "\nPRIMARY KEY (id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS Assignments (" +
                "\nid int AUTO_INCREMENT,\n" +
                "\nDeviceID varchar(100) NOT NULL," +
                "\nUserID varchar(100) NOT NULL," +
                "\nDateAssigned DATE," +
                "\nTimeAssigned TIME," +
                "\nPRIMARY KEY (id)," +
                "\nCONSTRAINT FOREIGN KEY (DeviceID) REFERENCES Devices(id)," +
                "\nCONSTRAINT FOREIGN KEY (UserID) REFERENCES Users(id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS AssignmentHistory (" +
                "\nid int AUTO_INCREMENT,\n" +
                "\nDeviceID varchar(100) NOT NULL," +
                "\nUserID varchar(100) NOT NULL," +
                "\nDateAssigned DATE," +
                "\nTimeAssigned TIME," +
                "\nDateReturned DATE," +
                "\nTimeReturned TIME," +
                "\nReturnedOnTime bit," +
                "\nReturnedBy varchar(100) NOT NULL," +
                "\nPRIMARY KEY (id))");

        stmt.execute("CREATE TABLE IF NOT EXISTS PermittedEmails (" +
                "\nEmail varchar(100) NOT NULL," +
                "\nPRIMARY KEY (Email))");
    }
}
