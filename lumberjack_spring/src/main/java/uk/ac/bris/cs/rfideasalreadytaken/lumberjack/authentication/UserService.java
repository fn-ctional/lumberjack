package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUserDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;

import java.util.Collections;

@Service
public class UserService implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationDatabaseManager authenticationDatabaseManager;

    @Transactional
    @Override
    public AdminUser registerNewUserAccount(AdminUserDTO accountDTO) throws EmailNotPermittedException, Exception {

        if (authenticationDatabaseManager.userExists(accountDTO.getEmail())) {
            throw new EmailExistsException("There is already an account with that email address: "  + accountDTO.getEmail());
        }

        if (!authenticationDatabaseManager.emailPermitted(accountDTO.getEmail())) {
            throw new EmailNotPermittedException("The following email is not on the list of permitted emails: "  + accountDTO.getEmail());
        }

        AdminUser user = new AdminUser();
        user.setName(accountDTO.getName());
        user.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
        user.setEmail(accountDTO.getEmail());
        user.setEnabled(false);
        authenticationDatabaseManager.addAdminUser(user);
        return user;
    }

    @Override
    public AdminUser getUser(String verificationToken) {
        AdminUser user = authenticationDatabaseManager.findByToken(verificationToken).getAdminUser();
        return user;
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return authenticationDatabaseManager.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(AdminUser user) throws Exception {
        authenticationDatabaseManager.save(user);
    }

    @Override
    public void createVerificationToken(AdminUser user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        authenticationDatabaseManager.addToken(myToken);
    }
}
