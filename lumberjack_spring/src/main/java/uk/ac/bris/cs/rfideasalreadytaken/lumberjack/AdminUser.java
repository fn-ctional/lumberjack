package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.context.annotation.Role;

import java.util.Collection;
import java.util.List;

public class AdminUser {

    private String name;

    private String email;

    private String password;

    private boolean enabled;

    private Collection<String> roles;

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

    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles(final Collection<String> roles) {
        this.roles = roles;
    }
}
