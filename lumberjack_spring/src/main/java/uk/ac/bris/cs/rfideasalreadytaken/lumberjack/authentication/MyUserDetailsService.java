package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.LumberjackApplication;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.data.AdminUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private AuthenticationBackend authenticationBackend;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        final Logger log = LoggerFactory.getLogger(LumberjackApplication.class);

        AdminUser user = authenticationBackend.findByEmail(email);
        log.info(email);
        log.info(user.getEmail());
        if (user.isEnabled()){
            log.info("enabled");
        }
        else {
            log.info("disabled");
        }

        boolean enabled = user.isEnabled();

        final boolean accountNonExpired = true;
        final boolean credentialsNonExpired = true;
        final boolean accountNonLocked = true;

        return  new org.springframework.security.core.userdetails.User
                (user.getEmail(),
                        user.getPassword(), enabled, accountNonExpired,
                        credentialsNonExpired, accountNonLocked,
                        getAuthorities(Collections.singletonList("ADMINISTRATOR")));
    }

    private static List<GrantedAuthority> getAuthorities (Collection<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
