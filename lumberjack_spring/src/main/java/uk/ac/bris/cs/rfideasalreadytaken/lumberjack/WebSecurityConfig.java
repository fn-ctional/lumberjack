package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final String[] unlocked = {"/", "/about", "/download", "/css/**", "/js/**", "/images/**"};

        http
                    .authorizeRequests()
                    .antMatchers(unlocked).permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/dashboard")
                    .permitAll()
                .and()
                    .authorizeRequests()
                    .antMatchers("/*").hasRole("ADMINISTRATOR")
                    .anyRequest().authenticated()
                .and()
                    .logout()
                    .logoutSuccessUrl("/")
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .and()
                    .csrf().disable();//this should be fixed and enabled for release
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("test@lumberjack").password("password1").roles("ADMINISTRATOR");
    }

}
