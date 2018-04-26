package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.User;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.FileUploadException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.WebBackend;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.config.Elements.HEADERS;

@Service
public class CSVBackend {

    @Autowired
    private WebBackend webBackend;

    public List<User> parseUserCSV(MultipartFile csv) throws FileUploadException {
        Iterable<CSVRecord> records = multipartFileToRecords(csv);

        List<User> newUsers = new ArrayList<>();
        for (CSVRecord record : records) {
            User newUser = new User();

            newUser.setScanValue(record.get("scan value"));
            try {
                newUser.setDeviceLimit(Integer.parseInt(record.get("device limit")));
            } catch (NumberFormatException e) {
                newUser.setDeviceLimit(0);
            }
            try {
                newUser.setDevicesRemoved(Integer.parseInt(record.get("devices removed")));
            } catch (NumberFormatException e) {
                newUser.setDevicesRemoved(0);
            }
            newUser.setCanRemove(Boolean.parseBoolean(record.get("can remove")));
            newUser.setGroupId(record.get("group id"));
            newUser.setId(UUID.randomUUID().toString());
            newUser.setUsername(record.get("username"));

            newUsers.add(newUser);
        }

        return newUsers;
    }

    public List<Device> parseDeviceCSV(MultipartFile csv) throws FileUploadException {
        Iterable<CSVRecord> records = multipartFileToRecords(csv);


        List<Device> newDevices = new ArrayList<>();
        for (CSVRecord record : records) {
            Device newDevice = new Device();

            newDevice.setScanValue(record.get("scan value"));
            newDevice.setAvailable(Boolean.parseBoolean(record.get("can remove")));
            newDevice.setRuleID(record.get("rule id"));
            newDevice.setCurrentlyAssigned(Boolean.parseBoolean(record.get("can remove")));
            newDevice.setType(record.get("scan value"));
            newDevice.setId(UUID.randomUUID().toString());

            newDevices.add(newDevice);
        }

        return newDevices;
    }

    private Iterable<CSVRecord> multipartFileToRecords(MultipartFile csv) throws FileUploadException {
        try {
            Reader in = new InputStreamReader(csv.getInputStream());

            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader(HEADERS)
                    .withFirstRecordAsHeader()
                    .parse(in);
            return records;
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileUploadException();
        }
    }

    public String getUsersCSV() throws SQLException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id,");
        stringBuilder.append("username,");
        stringBuilder.append("scan value,");
        stringBuilder.append("device limit,");
        stringBuilder.append("devices removed,");
        stringBuilder.append("can remove,");
        stringBuilder.append("group id");
        stringBuilder.append("\n");

        List<User> users = webBackend.getUsers();
        for (User user : users) {

            stringBuilder.append(user.getId());
            stringBuilder.append(",");
            stringBuilder.append(user.getUsername());
            stringBuilder.append(",");
            stringBuilder.append(user.getScanValue());
            stringBuilder.append(",");
            stringBuilder.append(user.getDeviceLimit());
            stringBuilder.append(",");
            stringBuilder.append(user.getDevicesRemoved());
            stringBuilder.append(",");
            stringBuilder.append(user.canRemove());
            stringBuilder.append(",");
            stringBuilder.append(user.getGroupId());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public String getDevicesCSV() throws SQLException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id,");
        stringBuilder.append("scan value,");
        stringBuilder.append("type,");
        stringBuilder.append("available,");
        stringBuilder.append("currently assigned,");
        stringBuilder.append("rule id");
        stringBuilder.append("\n");

        List<Device> devices = webBackend.getDevices();
        for (Device device : devices) {
            stringBuilder.append(device.getId());
            stringBuilder.append(",");
            stringBuilder.append(device.getScanValue());
            stringBuilder.append(",");
            stringBuilder.append(device.getType());
            stringBuilder.append(",");
            stringBuilder.append(device.isAvailable());
            stringBuilder.append(",");
            stringBuilder.append(device.isCurrentlyAssigned());
            stringBuilder.append(",");
            stringBuilder.append(device.getRuleID());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
