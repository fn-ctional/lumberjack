package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data;

import java.sql.Date;

public interface Token {

    String getToken();

    void setToken(String token);

    AdminUser getAdminUser();

    void setAdminUser(AdminUser adminUser);

    Date getExpiryDate();

    void setExpiryDate(Date date);

}
