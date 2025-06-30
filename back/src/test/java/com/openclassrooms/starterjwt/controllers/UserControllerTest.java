package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.YogaAppSpringBootTestFramework;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends YogaAppSpringBootTestFramework {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    public void findById() throws Exception {
        // Given: a user exists in the database
        User user = User.builder()
                .email("admin@example.com")
                .lastName("Doe")
                .firstName("John")
                .password(passwordEncoder.encode("password"))
                .admin(true)
                .build();
        user = userRepository.save(user);

        // When: performing GET request to retrieve the user by ID
        mockMvc.perform(get("/api/user/" + user.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                // Then: the response contains the correct user details
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    public void deleteUser() throws Exception {
        // Given: a user exists in the database
        User user = User.builder()
                .email("admin@example.com")
                .lastName("Doe")
                .firstName("John")
                .password(passwordEncoder.encode("password"))
                .admin(true)
                .build();
        user = userRepository.save(user);
        Long userId = user.getId();

        // Ensure the user is present before deletion
        assertTrue(userRepository.findById(userId).isPresent());

        // When: performing DELETE request to remove the user
        mockMvc.perform(delete("/api/user/" + user.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                // Then: the response status is OK
                .andExpect(status().isOk());

        // Then: the user should no longer exist in the database
        assertFalse(userRepository.findById(userId).isPresent());
    }
}

