package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@SpringBootApplication
@PropertySource({ "classpath:${envTarget:database}.properties" })
public class LumberjackApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(LumberjackApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        return;
    }

}
