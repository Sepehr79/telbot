package com.sepehr.telbot.security;

import com.sepehr.telbot.config.ApplicationConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApplicationConfiguration applicationConfiguration;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsManager configureGlobal() {
        UserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        UserDetails admin = User.builder().username(applicationConfiguration.getUiDefaultUser())
                .password(passwordEncoder().encode(applicationConfiguration.getUiDefaultPassword()))
                .roles("ADMIN")
                .build();
        userDetailsManager.createUser(admin);
        return userDetailsManager;
    }



}
