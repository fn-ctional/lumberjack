package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.BackendDatabaseLoading;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.LumberjackApplication;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;

@Service
public class AuthenticationDatabaseManager extends BackendDatabaseLoading {

    AuthenticationDatabaseManager() throws Exception{};

    /**
     * Adds a new AdminUser to the database. Passwords are already encoded before reaching function.
     * @param adminUser
     */
    public void addAdminUser(AdminUser adminUser) throws Exception {

        insertIntoAdminUsers(adminUser);
    }

    //Throws UsernameNotFoundException in the case of a database error.
    public AdminUser findByEmail(String email) throws UsernameNotFoundException {
        try {
            

            return loadAdminUser(email);

        } catch (Exception e) {
            throw new UsernameNotFoundException("Database error!");
        }
    }

    public boolean userExists(String email) {
        try {
            

            return adminUserExists(email);

        } catch (Exception e) {
            return false;
        }
    }

    public VerificationToken findByToken(String verificationToken) {
        try {
            

            return loadToken(verificationToken);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addToken(VerificationToken verificationToken) {
        try {
            

            insertIntoTokens(verificationToken);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(AdminUser adminUser) throws Exception {
        
        updateAdminUser(adminUser.getEmail(), adminUser);
    }

    /**
     * Whether the email being used to register as an admin is on the permitted list of emails.
     * An empty list returns true and the email is used as the first entry.
     * @param email Email being used to register.
     * @return Whether the email is on the list.
     *  If the list is empty, the email being checked is added, and it returns true.
     */
    public boolean emailPermitted(String email) {
        try {
            

            return isEmailPermitted(email);

        } catch (Exception e) {
            return false;
        }
    }
}
