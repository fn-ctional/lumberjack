package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data;

import org.springframework.context.annotation.Role;

import java.util.Collection;
import java.util.List;

public class AdminUser {

    private String name;

    private String email;

    private String password;

    /**
     * Whether the account has been activated via email.
     */
    private boolean enabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (!o.getClass().equals(AdminUser.class)) return false;
        AdminUser adminUser = (AdminUser) o;
        if (!adminUser.getEmail().equals(this.getEmail())) return false;
        if (!adminUser.getName().equals(this.getName())) return false;
        if (!adminUser.getPassword().equals(this.getPassword())) return false;
        return true;
    }

}
