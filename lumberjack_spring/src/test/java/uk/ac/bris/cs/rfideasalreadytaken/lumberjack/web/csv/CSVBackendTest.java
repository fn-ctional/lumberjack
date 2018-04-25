package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.csv;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.DatabaseTesting;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.WebBackend;

import static org.junit.Assert.*;

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

        assertEquals(csvBackend.getUsersCSV(), "id,scan value,device limit,devices removed,can remove,group id\nBetty1248,457436545,3,1,true,groupTwo\n");
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