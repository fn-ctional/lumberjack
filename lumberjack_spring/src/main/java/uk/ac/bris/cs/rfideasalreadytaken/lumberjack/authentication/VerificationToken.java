package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

public class VerificationToken {

    private String token;

    private AdminUser adminUser;

    private Date expiryDate;

    VerificationToken(String token, AdminUser adminUser) {
        this.token = token;
        this.adminUser = adminUser;
        this.expiryDate = calculateExpiryDate(60 * 24);

    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AdminUser getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(AdminUser adminUser) {
        this.adminUser = adminUser;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
