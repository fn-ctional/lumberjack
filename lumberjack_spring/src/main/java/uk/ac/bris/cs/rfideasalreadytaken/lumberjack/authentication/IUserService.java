package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUserDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.Token;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.VerificationToken;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.EmailExistsException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.EmailNotPermittedException;

import java.sql.SQLException;

public interface IUserService {

    AdminUser registerNewUserAccount(AdminUserDTO accountDTO) throws EmailNotPermittedException, SQLException, EmailExistsException;

    AdminUser getUser(String verificationToken);

    void saveRegisteredUser(AdminUser user) throws SQLException;

    void createVerificationToken(AdminUser user, String token);

    Token getVerificationToken(String VerificationToken);

    Token generateNewVerificationToken(String token) throws Exception;

    void createPasswordResetTokenForUser(AdminUser adminUser, String token);

    void changeUserPassword(AdminUser adminUser, String password);

}
