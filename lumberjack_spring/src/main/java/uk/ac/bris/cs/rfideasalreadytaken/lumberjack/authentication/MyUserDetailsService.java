package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.AdminUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private AuthenticationDatabaseManager authenticationDatabaseManager;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        AdminUser user = authenticationDatabaseManager.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(
                    "No user found with username: "+ email);
        }

        boolean enabled = user.isEnabled();

        final boolean accountNonExpired = true;
        final boolean credentialsNonExpired = true;
        final boolean accountNonLocked = true;

        return  new org.springframework.security.core.userdetails.User
                (user.getEmail(),
                        user.getPassword().toLowerCase(), enabled, accountNonExpired,
                        credentialsNonExpired, accountNonLocked,
                        getAuthorities(user.getRoles()));
    }

    private static List<GrantedAuthority> getAuthorities (Collection<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
