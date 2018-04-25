package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.csv;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.DatabaseTesting;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.WebBackend;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "file:${user.dir}/config/testdatabase.properties")
@SpringBootTest
public class CSVBackendTest {

    @Autowired
    private DatabaseTesting databaseTesting;

    @Autowired
    private WebBackend webBackend;

    @Autowired
    private CSVBackend csvBackend;

    @Test
    public void testGetUsersCSV() throws Exception {
        databaseTesting.addTestUsers();

        webBackend.deleteUser("Aidan9876");
        webBackend.deleteUser("Dorathy0369");
        webBackend.deleteUser("Callum2468");

        assertEquals(csvBackend.getUsersCSV(), "id,username,scan value,device limit,devices removed,can remove,group id\nBetty1248,457436545,3,1,true,groupTwo\n");
    }

    @Test
    public void testGetDevicesCSV() throws Exception {
        databaseTesting.addTestDevices();

        webBackend.deleteDevice("laptop01");
        webBackend.deleteDevice("laptop03");
        webBackend.deleteDevice("camera01");

        assertEquals(csvBackend.getDevicesCSV(), "id,scan value,type,available,currently assigned,rule id\nlaptop02,23482364326842334,laptop,true,true,ruleSet2\n");
    }

}