package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication.MyUserDetailsService;

import java.util.Locale;
import java.util.Properties;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final String[] unlocked = {"/", "/about", "/download", "/register", "/registration", "/css/**", "/js/**",
                "/images/**", "/registrationConfirm*", "/devices", "/rpi"};

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
                    .logoutSuccessUrl("/login")
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .and()
                    .requiresChannel().antMatchers("*").requiresSecure()
                .and()
                    .csrf().disable();//TODO: this should be fixed and enabled for release
    }

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

}
