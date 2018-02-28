package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LumberjackApplicationTests {

    @Autowired
    private MainController testController;

	@Test
	public void contextLoads() {
        assertThat(testController).isNotNull();
	}

}
