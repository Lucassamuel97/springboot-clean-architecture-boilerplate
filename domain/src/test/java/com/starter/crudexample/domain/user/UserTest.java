package com.starter.crudexample.domain.user;

import com.starter.crudexample.domain.role.Role;
import com.starter.crudexample.domain.role.RoleName;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(RoleName.ROLE_ADMIN));
        User user = new User("testuser", "password", roles);

        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleName.ROLE_ADMIN)));
    }
}
