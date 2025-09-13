package com.novara_demo.admin_servlet.config;

import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class AuthConfig {


    @Bean
    public AuthBearerHeaderProvider tokenProvider() {
        return new AuthBearerHeaderProvider();
    }

    @Bean
    public HttpHeadersProvider customHttpHeadersProvider(
            @Qualifier("basicAuthHttpHeadersProvider") HttpHeadersProvider defaultProvider,
            AuthBearerHeaderProvider tokenProvider
    ) {
        return (instance) -> {
            HttpHeaders existingHeaders = defaultProvider.getHeaders(instance);
            return tokenProvider.addBearerAuthHeader(existingHeaders);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("pass"))
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated())
                .httpBasic(withDefaults())
                .formLogin(withDefaults())
                .logout(l -> l.logoutUrl("/logout"))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/logout", "/login", "/instances", "/instances/*"));

        return http.build();
    }
}
