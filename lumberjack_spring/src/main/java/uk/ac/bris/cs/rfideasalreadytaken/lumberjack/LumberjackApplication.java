package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.DatabaseConnection;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.DatabaseTesting;

@SpringBootApplication
public class LumberjackApplication {

    public static void main(String[] args) {
        SpringApplication.run(LumberjackApplication.class, args);
    }

}
