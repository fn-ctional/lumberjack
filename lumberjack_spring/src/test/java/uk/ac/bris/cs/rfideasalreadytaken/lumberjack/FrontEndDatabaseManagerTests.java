package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FrontEndDatabaseManagerTests {

    @Autowired
    private FrontEndDatabaseManager frontEndDatabaseManager;

    @Test
    public void testAddUser() throws NotFoundException {
        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user");
        testUser.setScanValue("what is scan value?");
        frontEndDatabaseManager.addUser(testUser);
        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()), testUser);
    }
}
