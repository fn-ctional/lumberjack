package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.BackendDatabaseLoading;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;

@Service
public class AuthenticationDatabaseManager extends BackendDatabaseLoading {

    /**
     * Adds a new AdminUser to the database. Passwords are already encoded before reaching function.
     * @param adminUser
     */
    public void addAdminUser(AdminUser adminUser) throws Exception {
        connectToDatabase();

        insertIntoAdminUsers(adminUser);
    }

    //Throws UsernameNotFoundException in the case of a database error.
    public AdminUser findByEmail(String email) throws UsernameNotFoundException {
        try {
            connectToDatabase();

            return loadAdminUser(email);

        } catch (Exception e) {
            throw new UsernameNotFoundException("Database error!");
        }
    }

    public boolean userExists(String email) {
        try {
            connectToDatabase();

            return adminUserExists(email);

        } catch (Exception e) {
            return false;
        }
    }

    public VerificationToken findByToken(String verificationToken) {
        try {
            connectToDatabase();

            return loadToken(verificationToken);

        } catch (Exception e) {
            //TODO: something better here
            e.printStackTrace();
        }
        return null;
    }

    public void addToken(VerificationToken verificationToken) {
        try {
            connectToDatabase();

            insertIntoTokens(verificationToken);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(AdminUser adminUser) throws Exception {
        connectToDatabase();
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
            connectToDatabase();

            return isEmailPermitted(email);

        } catch (Exception e) {
            return false;
        }
    }
}
