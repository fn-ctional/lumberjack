package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.context.ApplicationEvent;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;

import java.util.Locale;

public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private String appUrl;
    private Locale locale;
    private AdminUser adminUser;

    public OnRegistrationCompleteEvent(AdminUser adminUser, Locale locale, String appUrl) {
        super(adminUser);

        this.adminUser = adminUser;
        this.locale = locale;
        this.appUrl = appUrl;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public AdminUser getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(AdminUser adminUser) {
        this.adminUser = adminUser;
    }
}
