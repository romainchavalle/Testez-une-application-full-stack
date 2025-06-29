package com.openclassrooms.starterjwt.services;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.Test;

public class UserDetailsImplTest {

    @Test
    void testEquals_sameObject_shouldReturnTrue() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("test")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .password("pass")
                .build();

        assertEquals(user, user); // this == o
    }

    @Test
    void testEquals_null_shouldReturnFalse() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).build();

        assertNotEquals(user, null); // o == null
    }

    @Test
    void testEquals_differentClass_shouldReturnFalse() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).build();

        assertNotEquals(user, "not a user"); // getClass() != o.getClass()
    }

    @Test
    void testEquals_differentId_shouldReturnFalse() {
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(2L).build();

        assertNotEquals(user1, user2);
    }

    @Test
    void testEquals_sameId_shouldReturnTrue() {
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(1L).build();

        assertEquals(user1, user2);
    }
}
