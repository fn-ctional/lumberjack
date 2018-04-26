package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;

import java.sql.SQLException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "file:${user.dir}/config/testdatabase.properties")
@SpringBootTest
public class DatabaseAdminUsersTest {

    @Autowired
    private DatabaseUtility databaseUtility;

    @Autowired
    private DatabaseAdminUsers databaseAdminUsers;

    @Test
    public void addingValidAdmin() throws SQLException {
        databaseUtility.resetAdmins();

        AdminUser adminUser = new AdminUser();
        adminUser.setPassword("password");
        adminUser.setEmail("testemail");
        adminUser.setName("testadmin");
        adminUser.setEnabled(false);
        databaseAdminUsers.insertIntoAdminUsers(adminUser);
        assertEquals(adminUser, databaseAdminUsers.loadAdminUser("testemail"));
        assertTrue(databaseAdminUsers.adminUserExists("testemail"));
    }

    @Test
    public void updatingValidAdmin() throws SQLException {
        databaseUtility.resetTokens();

        AdminUser adminUser = new AdminUser();
        adminUser.setPassword("password");
        adminUser.setEmail("testemail");
        adminUser.setName("testadmin");
        adminUser.setEnabled(false);
        databaseAdminUsers.insertIntoAdminUsers(adminUser);
        adminUser.setName("newname");
        databaseAdminUsers.updateAdminUser("testemail", adminUser);

        assertEquals(adminUser, databaseAdminUsers.loadAdminUser("testemail"));
    }

    @Test(expected = SQLException.class)
    public void addingInvalidAdmin() throws SQLException {
        databaseUtility.resetAdmins();

        AdminUser adminUser = new AdminUser();
        adminUser.setPassword("password");
        adminUser.setName("testadmin");
        adminUser.setEnabled(false);
        databaseAdminUsers.insertIntoAdminUsers(adminUser);
        assertEquals(adminUser, databaseAdminUsers.loadAdminUser("testemail"));
        assertTrue(databaseAdminUsers.adminUserExists("testemail"));
    }

    @Test(expected = SQLException.class)
    public void updatingInvalidAdmin() throws SQLException {
        databaseUtility.resetTokens();

        AdminUser adminUser = new AdminUser();
        adminUser.setPassword("password");
        adminUser.setEmail("testemail");
        adminUser.setName("testadmin");
        adminUser.setEnabled(false);
        databaseAdminUsers.insertIntoAdminUsers(adminUser);
        adminUser.setName(null);
        databaseAdminUsers.updateAdminUser("testemail", adminUser);

        assertEquals(adminUser, databaseAdminUsers.loadAdminUser("testemail"));
    }

    @Test
    public void adminUserExists() throws SQLException {
        databaseUtility.resetAdmins();

        AdminUser adminUser = new AdminUser();
        adminUser.setPassword("password");
        adminUser.setEmail("testemail");
        adminUser.setName("testadmin");
        adminUser.setEnabled(false);
        databaseAdminUsers.insertIntoAdminUsers(adminUser);
        assertTrue(databaseAdminUsers.adminUserExists("testemail"));
        assertFalse(databaseAdminUsers.adminUserExists("nothere"));
    }

    @Test
    public void updateValidUserPassword() throws SQLException {
        databaseUtility.resetAdmins();

        AdminUser adminUser = new AdminUser();
        adminUser.setPassword("password");
        adminUser.setEmail("testemail");
        adminUser.setName("testadmin");
        adminUser.setEnabled(false);
        databaseAdminUsers.insertIntoAdminUsers(adminUser);

        databaseAdminUsers.updatePassword("testemail", "newpass");
        assertEquals(databaseAdminUsers.loadAdminUser("testemail").getPassword(), "newpass");
    }

    @Test(expected = UsernameNotFoundException.class)
    public void updateInvalidUserPassword() throws SQLException {
        databaseUtility.resetAdmins();

        databaseAdminUsers.updatePassword("testemail", "newpass");
        assertEquals(databaseAdminUsers.loadAdminUser("testemail").getPassword(), "newpass");
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadInvalidUser() throws SQLException {
        databaseUtility.resetAdmins();

        databaseAdminUsers.updatePassword("testemail", "newpass");
        databaseAdminUsers.loadAdminUser("testemail");
    }

}