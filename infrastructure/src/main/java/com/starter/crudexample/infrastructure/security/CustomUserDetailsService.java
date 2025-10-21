package com.starter.crudexample.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserGateway;
import com.starter.crudexample.infrastructure.security.jwt.UserPrincipal;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserGateway userGateway;

    public CustomUserDetailsService(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userGateway.findByUsername(username)
            .orElseThrow(() -> 
                new UsernameNotFoundException("User not found with username: " + username)
            );

        return UserPrincipal.create(user);
    }
}
