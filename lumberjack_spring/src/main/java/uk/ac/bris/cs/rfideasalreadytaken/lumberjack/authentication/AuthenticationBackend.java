package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.Token;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.VerificationToken;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;

@Service
public class AuthenticationBackend {

    @Autowired
    private DatabaseConnection databaseConnection;

    @Autowired
    private DatabaseTokens databaseTokens;

    @Autowired
    private DatabaseAdminUsers databaseAdminUsers;

    @Autowired
    private DatabaseEmails databaseEmails;

    /**
     * Adds a new AdminUser to the database. Passwords are already encoded before reaching function.
     * @param adminUser
     */
    public void addAdminUser(AdminUser adminUser) throws SQLException {
        databaseAdminUsers.insertIntoAdminUsers(adminUser);
    }

    //Throws UsernameNotFoundException in the case of a database error.
    //This is required because of spring security
    public AdminUser findByEmail(String email) throws UsernameNotFoundException {
        try {
            return databaseAdminUsers.loadAdminUser(email);

        } catch (Exception e) {
            throw new UsernameNotFoundException("Database error!");
        }
    }

    public boolean userExists(String email) throws SQLException {
        return databaseAdminUsers.adminUserExists(email);
    }

    //Because of spring, this cannot throw the appropriate exception
    public Token findByToken(String verificationToken) {
        try {
            return databaseTokens.loadToken(verificationToken);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Because of spring, this cannot throw the appropriate exception
    public void addToken(Token verificationToken) {
        try {
            databaseTokens.insertIntoTokens(verificationToken);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(AdminUser adminUser) throws SQLException {
        databaseAdminUsers.updateAdminUser(adminUser.getEmail(), adminUser);
    }

    public Token save(Token verificationToken) throws SQLException {
        databaseTokens.updateToken(verificationToken.getToken(), verificationToken);
        return verificationToken;
    }

    /**
     * Whether the email being used to register as an admin is on the permitted list of emails.
     * An empty list returns true and the email is used as the first entry.
     * @param email Email being used to register.
     * @return Whether the email is on the list.
     *  If the list is empty, the email being checked is added, and it returns true.
     */
    public boolean emailPermitted(String email) throws SQLException {
        return databaseEmails.isEmailPermitted(email);
    }

    public String validatePasswordResetToken(String id, String token) {
        Token passToken = findByToken(token);
        if ((passToken == null) || !passToken.getAdminUser().getEmail().equals(id)) {
            return "invalidToken";
        }

        Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate()
                .getTime() - cal.getTime()
                .getTime()) <= 0) {
            return "expired";
        }

        AdminUser adminUser = passToken.getAdminUser();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                adminUser, null, Arrays.asList(
                new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return null;
    }
}
