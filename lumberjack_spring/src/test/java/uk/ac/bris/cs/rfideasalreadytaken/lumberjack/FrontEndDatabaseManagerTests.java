package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@SpringBootTest
@TestPropertySource({ "classpath:${envTarget:testdatabase}.properties" })
@RunWith(SpringRunner.class)
public class FrontEndDatabaseManagerTests {

    @Autowired
    private BackendFrontEndManager frontEndDatabaseManager;

    @Before
    public void setupDatabase() throws Exception {
        frontEndDatabaseManager.resetDatabase();
    }


    @Test
    public void testAddUser() throws Exception {
        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user");
        testUser.setScanValue("what is scan value?");
        frontEndDatabaseManager.insertUser(testUser);
        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()), testUser);
    }

    @Test
    public void testNoUserNotFound() throws Exception {
        User testUser = new User();
        testUser.setId("definitely_not_in_there_test_user");
        assertNotEquals(frontEndDatabaseManager.getUser(testUser.getId()), testUser);
    }

    @Test(expected = NotFoundException.class)
    public void testNoUserNotFoundThrowsException() throws Exception {
        User testUser = new User();
        testUser.setId("definitely_not_in_there_test_user");
        assertNotEquals(frontEndDatabaseManager.getUser(testUser.getId()), testUser);
    }
}
