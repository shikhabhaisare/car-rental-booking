package com.xyz.carrental.booking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * Define an in-memory user for testing.
     * It can be later replaced this with DB-based auth or OAuth2/JWT.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername("admin")
                .password(passwordEncoder.encode("password123"))
                .roles("ADMIN")
                .build();

        UserDetails user2 = User.withUsername("user")
                .password(passwordEncoder.encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user, user2);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security filter chain with clear rules:
     * - /api/v1/bookings/** → requires authentication
     * - /stub/** → open for local stubs/testing
     * - everything else → requires authentication
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // disable CSRF for APIs
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/stub/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/bookings").hasAnyRole("USER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/v1/bookings/**").hasAnyRole("USER", "ADMIN")
                    .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())  // enable Basic Auth
            .formLogin(form -> form.disable());   // disable HTML login form

        return http.build();
    }
}
