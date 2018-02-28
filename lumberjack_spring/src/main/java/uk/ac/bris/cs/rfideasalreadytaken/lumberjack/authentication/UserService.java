package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.AdminUser;

import java.util.Arrays;

@Service
public class UserService implements IUserService {
    //Put autowire object database here

    @Transactional
    @Override
    public AdminUser registerNewUserAccount(AdminUserDTO accountDTO) throws EmailExistsException {

        if (emailExist(accountDTO.getEmail())) {
            throw new EmailExistsException("There is already an account with that email address:"  + accountDTO.getEmail());
        }

        AdminUser user = new AdminUser();
        user.setName(accountDTO.getName());
        user.setPassword(accountDTO.getPassword());
        user.setEmail(accountDTO.getEmail());
        user.setRoles(Arrays.asList("ADMIN"));
        //Save user in database
        return user;
    }
    private boolean emailExist(String email) {
        //TODO: implement this
        return false;
    }
}
