package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data;

import org.hibernate.validator.constraints.NotEmpty;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.PasswordMatches;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.ValidEmail;

import javax.validation.constraints.NotNull;

@PasswordMatches
public class AdminUserDTO {

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String password;
    private String matchingPassword;

    @ValidEmail
    @NotNull
    @NotEmpty
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }
}
