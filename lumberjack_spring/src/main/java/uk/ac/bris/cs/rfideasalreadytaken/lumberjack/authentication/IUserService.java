package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUserDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;

public interface IUserService {

    AdminUser registerNewUserAccount(AdminUserDTO accountDTO) throws EmailNotPermittedException, Exception;

    AdminUser getUser(String verificationToken);

    void saveRegisteredUser(AdminUser user) throws Exception;

    void createVerificationToken(AdminUser user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

}
