package com.starter.crudexample;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class ApiTest {

    private ApiTest() {}

    public static final RequestPostProcessor ADMIN_USER = user(userDetails("admin", List.of("ROLE_ADMIN")));
    public static final RequestPostProcessor STANDARD_USER = user(userDetails("user", List.of("ROLE_USER")));

    // Caso queira simular JWT especificamente (sem validar assinatura),
    // use o suporte do spring-security-test para Resource Server:
    public static final RequestPostProcessor ADMIN_JWT = jwt()
            .jwt(jwt -> jwt.subject("admin-id").claim("roles", List.of("ROLE_ADMIN")))
            .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));

    public static final RequestPostProcessor USER_JWT = jwt()
            .jwt(jwt -> jwt.subject("user-id").claim("roles", List.of("ROLE_USER")))
            .authorities(new SimpleGrantedAuthority("ROLE_USER"));

    private static UserDetails userDetails(String username, List<String> roles) {
        return User.withUsername(username)
            .password("password")
            .authorities(roles.stream().map(SimpleGrantedAuthority::new).toArray(SimpleGrantedAuthority[]::new))
            .build();
    }
}
