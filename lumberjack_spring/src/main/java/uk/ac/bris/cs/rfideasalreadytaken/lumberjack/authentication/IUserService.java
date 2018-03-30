package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUserDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.VerificationToken;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.EmailExistsException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.EmailNotPermittedException;

import java.sql.SQLException;

public interface IUserService {

    AdminUser registerNewUserAccount(AdminUserDTO accountDTO) throws EmailNotPermittedException, SQLException, EmailExistsException;

    AdminUser getUser(String verificationToken);

    void saveRegisteredUser(AdminUser user) throws SQLException;

    void createVerificationToken(AdminUser user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String token) throws Exception;

}
