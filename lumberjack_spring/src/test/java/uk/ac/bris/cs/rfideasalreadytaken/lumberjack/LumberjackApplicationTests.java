package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.AuthenticationController;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "file:${user.dir}/config/testdatabase.properties")
@SpringBootTest
public class LumberjackApplicationTests {

    @Autowired
    private AuthenticationController testController;

	@Test
	public void contextLoads() {
        assertThat(testController).isNotNull();
	}

}
