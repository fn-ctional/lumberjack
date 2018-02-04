package uk.ac.bris.cs.rfideasalreadytaken.rfid_pi_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        try {
            while (true) {
                RFIDPipeReader reader = new RFIDPipeReader();
                Scan scan = reader.read();
                if (scan.isSuccess()) {
                    //send the scan to server
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not find pipe!");
            System.out.println("In directory: " + System.getProperty("user.dir"));
            System.out.println("Files in directory:");
            try (Stream<Path> paths = Files.walk(Paths.get(System.getProperty("user.dir")))) {
                paths
                        .filter(Files::isRegularFile)
                        .forEach(System.out::println);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
