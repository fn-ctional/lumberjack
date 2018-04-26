package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data;

public class PasswordDTO {

    private String email;

    private String newPassword;

    public String getNewPassword() {
        return  this.newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
