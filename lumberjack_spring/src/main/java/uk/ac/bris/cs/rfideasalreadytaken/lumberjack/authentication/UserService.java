package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.AdminUser;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Service
public class UserService implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationDatabaseManager authenticationDatabaseManager;

    @Transactional
    @Override
    public AdminUser registerNewUserAccount(AdminUserDTO accountDTO) throws EmailExistsException, EmailNotPermittedException {

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
        user.setRoles(Collections.singletonList("ADMIN"));
        authenticationDatabaseManager.addAdminUser(user);
        return user;
    }
}
