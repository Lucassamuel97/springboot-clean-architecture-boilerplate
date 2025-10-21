package com.starter.crudexample.infrastructure.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
@Profile("test-webmvc")
public class WebMvcTestSecurityConfig {

    @Bean
    public SecurityFilterChain webMvcTestFilterChain(HttpSecurity http) throws Exception {
        // Limita este chain apenas às rotas de API sob teste
        http.securityMatcher("/items/**", "/users/**", "/api/items/**", "/api/users/**");
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/items/**", "/api/items/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/users/**", "/api/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .build();
    }
}
