package com.airline.checkin.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
public class DemoSecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/api/bookings/*/seat", "/api/bookings/*/check-in"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(GET, "/api/bookings", "/api/bookings/search",
                                "/api/bookings/*/detail").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(POST, "/api/bookings").hasRole("ADMIN")
                        .requestMatchers(POST, "/api/bookings/*/cancel").hasRole("ADMIN")
                        .requestMatchers(PUT, "/api/bookings/*").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(GET, "/api/bookings/*").permitAll()
                        .requestMatchers(PUT, "/api/bookings/*/seat").permitAll()
                        .requestMatchers(POST, "/api/bookings/*/check-in").permitAll()
                        .requestMatchers("/api/bookings/**").denyAll()
                        .anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService demoUsers(
            PasswordEncoder passwordEncoder,
            @Value("${demo.security.admin.username}") String adminUsername,
            @Value("${demo.security.admin.password}") String adminPassword,
            @Value("${demo.security.staff.username}") String staffUsername,
            @Value("${demo.security.staff.password}") String staffPassword) {
        return new InMemoryUserDetailsManager(
                User.withUsername(adminUsername).password(passwordEncoder.encode(adminPassword)).roles("ADMIN").build(),
                User.withUsername(staffUsername).password(passwordEncoder.encode(staffPassword)).roles("STAFF").build());
    }
}
