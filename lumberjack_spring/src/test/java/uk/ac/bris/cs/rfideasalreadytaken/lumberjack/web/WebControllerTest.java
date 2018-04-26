package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.DatabaseTesting;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "file:${user.dir}/config/testdatabase.properties")
@SpringBootTest
@WithMockUser(username = "test", roles = "ADMINISTRATOR")
public class WebControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private DatabaseTesting databaseTesting;

    private MockMvc mvc;

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    @WithAnonymousUser
    public void addViewControllers() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk());
        mvc.perform(get("/about"))
                .andExpect(status().isOk());
        mvc.perform(get("/login"))
                .andExpect(status().isOk());
        mvc.perform(get("/help"))
                .andExpect(status().isOk());
        mvc.perform(get("/download"))
                .andExpect(status().isOk());
	}

    @Test
    public void dashboard() throws Exception {
        databaseTesting.addTestAdmins();
        mvc.perform(get("/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    public void user() throws Exception {
        mvc.perform(get("/user"))
                .andExpect(status().isOk());
    }

    @Test
    public void allUsers() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    public void userSpecified() throws Exception {
        databaseTesting.addTestUsers();

        mvc.perform(get("/user/user01"))
                .andExpect(status().isOk());

        mvc.perform(get("/user/usernotinthere"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void device() throws Exception {
        mvc.perform(get("/device"))
                .andExpect(status().isOk());
    }

    @Test
    public void allDevices() throws Exception {
        mvc.perform(get("/devices"))
                .andExpect(status().isOk());
    }

    @Test
    public void deviceSpecified() {
    }

    @Test
    public void search() {
    }

    @Test
    public void searchType() {
    }

    @Test
    public void add() {
    }

    @Test
    public void addType() {
    }

    @Test
    public void addUsersCSV() {
    }

    @Test
    public void addDevicesCSV() {
    }
}