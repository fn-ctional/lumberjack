package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.AdminUser;

public interface IUserService {

    AdminUser registerNewUserAccount(AdminUserDTO accountDTO) throws EmailExistsException, EmailNotPermittedException;

    AdminUser getUser(String verificationToken);

    void saveRegisteredUser(AdminUser user);

    void createVerificationToken(AdminUser user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

}
