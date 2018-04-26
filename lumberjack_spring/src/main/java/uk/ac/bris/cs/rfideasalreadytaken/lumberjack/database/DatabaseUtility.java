package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.xml.SqlXmlFeatureNotImplementedException;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class DatabaseUtility {

    @Autowired
    private DatabaseConnection databaseConnection;

    /**
     * Completely resets database.
     * @throws SQLException
     */
    public void resetDatabase() throws SQLException {
        Statement stmt = databaseConnection.getConnection().createStatement();

        dropAllTables(stmt);
        addAllTables(stmt);

        stmt.executeBatch();
    }


    /**
     * Creates any tables that are missing from the database.
     * @throws SQLException
     */
    public void checkDatabase() throws SQLException {
        Statement stmt = databaseConnection.getConnection().createStatement();

        addAllTables(stmt);

        stmt.executeBatch();
    }

    private void addAllTables(Statement stmt) throws SQLException {
        createAdminsTable(stmt);
        createTokensTable(stmt);
        createUserGroupsTable(stmt);
        createRulesTable(stmt);
        createGroupPermissionsTable(stmt);
        createUsersTable(stmt);
        createDevicesTable(stmt);
        createAssignmentsTable(stmt);
        createAssignmentHistoryTable(stmt);
        createPermittedEmailsTable(stmt);
    }

    private void dropAllTables(Statement stmt) throws SQLException {
        stmt.addBatch("DROP TABLE IF EXISTS AssignmentHistory");
        stmt.addBatch("DROP TABLE IF EXISTS Assignments");
        stmt.addBatch("DROP TABLE IF EXISTS Users");
        stmt.addBatch("DROP TABLE IF EXISTS Devices");
        stmt.addBatch("DROP TABLE IF EXISTS GroupPermissions");
        stmt.addBatch("DROP TABLE IF EXISTS Rules");
        stmt.addBatch("DROP TABLE IF EXISTS UserGroups");
        stmt.addBatch("DROP TABLE IF EXISTS PermittedEmails");
        stmt.addBatch("DROP TABLE IF EXISTS Tokens");
        stmt.addBatch("DROP TABLE IF EXISTS Admins");
    }

    /**
     * Resets Users table, also resets Assignments table
     * @throws SQLException
     */
    public void resetUsers() throws SQLException {
        Statement stmt = databaseConnection.getConnection().createStatement();
        stmt.addBatch("DROP TABLE IF EXISTS Assignments");
        stmt.addBatch("DROP TABLE IF EXISTS Users");

        createUserGroupsTable(stmt);
        createUsersTable(stmt);
        stmt.executeBatch();
    }

    /**
     * Resets Devices table, also resets Assignments table
     * @throws SQLException
     */
    public void resetDevices() throws SQLException {
        Statement stmt = databaseConnection.getConnection().createStatement();
        stmt.addBatch("DROP TABLE IF EXISTS Assignments");
        stmt.addBatch("DROP TABLE IF EXISTS Devices");

        createRulesTable(stmt);
        createDevicesTable(stmt);

        stmt.executeBatch();
    }

    /**
     * Resets UserGroup table, also resets Users, Assignments and GroupPermissions
     * @throws SQLException
     */
    public void resetUserGroups() throws SQLException {
        Statement stmt = databaseConnection.getConnection().createStatement();
        stmt.addBatch("DROP TABLE IF EXISTS Assignments");
        stmt.addBatch("DROP TABLE IF EXISTS Users");
        stmt.addBatch("DROP TABLE IF EXISTS GroupPermissions");
        stmt.addBatch("DROP TABLE IF EXISTS UserGroups");

        createUserGroupsTable(stmt);

        stmt.executeBatch();
    }

    /**
     * Resets Rules table, also resets Devices, Assignments and GroupPermissions
     * @throws SQLException
     */
    public void resetRules() throws SQLException {
        Statement stmt = databaseConnection.getConnection().createStatement();
        stmt.addBatch("DROP TABLE IF EXISTS Assignments");
        stmt.addBatch("DROP TABLE IF EXISTS Devices");
        stmt.addBatch("DROP TABLE IF EXISTS GroupPermissions");
        stmt.addBatch("DROP TABLE IF EXISTS Rules");

        createRulesTable(stmt);

        stmt.executeBatch();
    }

    /**
     * Resets GroupPermissions table
     * @throws SQLException
     */
    public void resetGroupPermissions() throws SQLException {
        Statement stmt = databaseConnection.getConnection().createStatement();

        stmt.addBatch("DROP TABLE IF EXISTS GroupPermissions");

        createGroupPermissionsTable(stmt);

        stmt.executeBatch();
    }


    /**
     * Resets Assignments table
     * @throws SQLException
     */
    public void resetAssignments() throws SQLException {
        Statement stmt = databaseConnection.getConnection().createStatement();

        stmt.addBatch("DROP TABLE IF EXISTS Assignments");

        createUserGroupsTable(stmt);
        createUsersTable(stmt);
        createAssignmentsTable(stmt);

        stmt.executeBatch();
    }

    /**
     * Resets Admins table
     * @throws SQLException
     */
    public void resetAdmins() throws SQLException {
        Statement stmt = databaseConnection.getConnection().createStatement();

        stmt.addBatch("DROP TABLE IF EXISTS Tokens");
        stmt.addBatch("DROP TABLE IF EXISTS Admins");

        createAdminsTable(stmt);

        stmt.executeBatch();
    }

    private void createUsersTable(Statement stmt) throws SQLException {
        stmt.addBatch("CREATE TABLE IF NOT EXISTS Users (" +
                "\nid varchar(100)," +
                "\nScanValue varchar(100) NOT NULL UNIQUE," +
                "\nUsername varchar(100)," +
                "\nDeviceLimit int," +
                "\nDevicesRemoved int," +
                "\nCanRemove bit," +
                "\nGroupID varchar(100)," +
                "\nCONSTRAINT FOREIGN KEY (GroupID) REFERENCES UserGroups(id)," +
                "\nPRIMARY KEY (id));");
    }

    private void createAssignmentsTable(Statement stmt) throws SQLException {
        stmt.addBatch("CREATE TABLE IF NOT EXISTS Assignments (" +
                "\nid int AUTO_INCREMENT,\n" +
                "\nDeviceID varchar(100) NOT NULL," +
                "\nUserID varchar(100) NOT NULL," +
                "\nDateAssigned DATE," +
                "\nTimeAssigned TIME," +
                "\nPRIMARY KEY (id)," +
                "\nCONSTRAINT FOREIGN KEY (DeviceID) REFERENCES Devices(id)," +
                "\nCONSTRAINT FOREIGN KEY (UserID) REFERENCES Users(id));");
    }

    private void createAdminsTable(Statement stmt) throws SQLException {
        stmt.addBatch("CREATE TABLE IF NOT EXISTS Admins (" +
                "\nEmail varchar(100)," +
                "\nUsername varchar(100) NOT NULL," +
                "\nPassword varchar(60) NOT NULL," +
                "\nEnabled bit," +
                "\nPRIMARY KEY (Email));");
    }

    private void createTokensTable(Statement stmt) throws SQLException {
        stmt.addBatch("CREATE TABLE IF NOT EXISTS Tokens (" +
                "\nToken varchar(100)," +
                "\nAdminEmail varchar(100) NOT NULL," +
                "\nExpiryDate DATE," +
                "\nCONSTRAINT FOREIGN KEY (AdminEmail) REFERENCES Admins(Email), " +
                "\nPRIMARY KEY (Token));");
    }

    private void createUserGroupsTable(Statement stmt) throws SQLException {
        stmt.addBatch("CREATE TABLE IF NOT EXISTS UserGroups (" +
                "\nid varchar(100)," +
                "\nPRIMARY KEY (id));");
    }

    private void createRulesTable(Statement stmt) throws SQLException {
        stmt.addBatch("CREATE TABLE IF NOT EXISTS Rules (" +
                "\nid varchar(100)," +
                "\nMaximumRemovalTime int," +
                "\nPRIMARY KEY (id));");
    }

    private void createGroupPermissionsTable(Statement stmt) throws SQLException {
        stmt.addBatch("CREATE TABLE IF NOT EXISTS GroupPermissions (" +
                "\nid int AUTO_INCREMENT,\n" +
                "\nRuleID varchar(100) NOT NULL," +
                "\nUserGroupID varchar(100) NOT NULL," +
                "\nPRIMARY KEY (id)," +
                "\nCONSTRAINT FOREIGN KEY (UserGroupID) REFERENCES UserGroups(id)," +
                "\nCONSTRAINT FOREIGN KEY (RuleID) REFERENCES Rules(id));");
    }

    private void createDevicesTable(Statement stmt) throws SQLException {
        stmt.addBatch("CREATE TABLE IF NOT EXISTS Devices (" +
                "\nid varchar(100)," +
                "\nScanValue varchar(100) NOT NULL UNIQUE," +
                "\nType varchar(100)," +
                "\nAvailable bit," +
                "\nCurrentlyAssigned bit," +
                "\nRuleID varchar(100)," +
                "\nCONSTRAINT FOREIGN KEY (RuleID) REFERENCES Rules(id)," +
                "\nPRIMARY KEY (id));");
    }

    private void createAssignmentHistoryTable(Statement stmt) throws SQLException {
        stmt.addBatch("CREATE TABLE IF NOT EXISTS AssignmentHistory (" +
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
    }

    private void createPermittedEmailsTable(Statement stmt) throws SQLException {
        stmt.addBatch("CREATE TABLE IF NOT EXISTS PermittedEmails (" +
                "\nEmail varchar(100) NOT NULL," +
                "\nPRIMARY KEY (Email))");
    }

}
