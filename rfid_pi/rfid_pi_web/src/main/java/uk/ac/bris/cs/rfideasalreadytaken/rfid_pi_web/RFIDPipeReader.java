package uk.ac.bris.cs.rfideasalreadytaken.rfid_pi_web;

import java.io.*;
import java.util.concurrent.*;

public class RFIDPipeReader implements Callable {

    private boolean userSuccess;
    private boolean deviceSuccess;
    private boolean success;

    RFIDPipeReader() {
       this.userSuccess = false;
       this.deviceSuccess = false;
       this.success = false;
    }

    public Scan read() throws IOException {
        String userID = "";
        String deviceID = "";

        while (!this.isUserSuccess()) {
            userID = this.firstScan();
        }

        deviceID = this.secondScan();
        this.success = this.userSuccess && this.deviceSuccess;
        return new Scan(userID, deviceID, this.success);

    }

    private String firstScan() throws IOException {
        File file = new File("pipe");
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String userID = bufferedReader.readLine();
        if(!userID.isEmpty()) this.userSuccess = true;

        fileReader.close();
        return userID;
    }

    private boolean isUserSuccess() {
        return this.userSuccess;
    }

    public boolean isDeviceSuccess() {
        return this.deviceSuccess;
    }

    private String secondScan() throws IOException {
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<String> future = executor.submit(this);

        try {
            return future.get(5, TimeUnit.SECONDS);

        } catch (TimeoutException ex) {
            System.out.println("Device was not presented within 5 seconds.");
        } catch (InterruptedException e) {
            System.out.println("Scanning was interrupted.");
        } catch (ExecutionException e) {
            System.out.println("Execution exception.");
        }

        this.deviceSuccess = false;

        return null;
    }

    public String call() throws IOException {
        File file = new File("pipe");
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String userID = bufferedReader.readLine();
        if(!userID.isEmpty()) this.userSuccess = true;

        fileReader.close();
        return userID;
    }
}
