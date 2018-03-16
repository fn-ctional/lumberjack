package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUserDTO;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.EmailExistsException;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.exceptions.EmailNotPermittedException;

import java.sql.SQLException;

@Service
public class UserService implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationBackend authenticationBackend;

    @Transactional
    @Override
    public AdminUser registerNewUserAccount(AdminUserDTO accountDTO) throws EmailNotPermittedException, EmailExistsException, SQLException {

        if (authenticationBackend.userExists(accountDTO.getEmail())) {
            throw new EmailExistsException("There is already an account with that email address: "  + accountDTO.getEmail());
        }

        if (!authenticationBackend.emailPermitted(accountDTO.getEmail())) {
            throw new EmailNotPermittedException("The following email is not on the list of permitted emails: "  + accountDTO.getEmail());
        }

        AdminUser user = new AdminUser();
        user.setName(accountDTO.getName());
        user.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
        user.setEmail(accountDTO.getEmail());
        user.setEnabled(false);
        authenticationBackend.addAdminUser(user);
        return user;
    }

    @Override
    public AdminUser getUser(String verificationToken) {
        return authenticationBackend.findByToken(verificationToken).getAdminUser();
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return authenticationBackend.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(AdminUser user) throws Exception {
        authenticationBackend.save(user);
    }

    @Override
    public void createVerificationToken(AdminUser user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        authenticationBackend.addToken(myToken);
    }
}
