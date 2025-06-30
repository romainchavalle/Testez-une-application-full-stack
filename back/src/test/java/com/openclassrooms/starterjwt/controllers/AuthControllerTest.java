package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.YogaAppSpringBootTestFramework;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTest extends YogaAppSpringBootTestFramework {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    public void registerUser_Success() throws Exception {
        // Given: a new user registration request with valid information
        SignupRequest signup = new SignupRequest();
        signup.setEmail("newuser@example.com");
        signup.setFirstName("Alice");
        signup.setLastName("Smith");
        signup.setPassword("secret");

        // When: the registration endpoint is called
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsString(signup)))
                // Then: the user is successfully registered and response is OK
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        // Then: the user is actually stored in the database with the correct encoded password
        assertThat(userRepository.existsByEmail("newuser@example.com")).isTrue();
        User saved = userRepository.findByEmail("newuser@example.com").get();
        assertThat(passwordEncoder.matches("secret", saved.getPassword())).isTrue();
    }

    @Test
    public void authenticateUser_Success() throws Exception {
        // Given: an existing user in the database
        User u = User.builder()
                .email("login@example.com")
                .firstName("Charlie")
                .lastName("Brown")
                .password(passwordEncoder.encode("mypassword"))
                .admin(false)
                .build();
        userRepository.save(u);

        LoginRequest login = new LoginRequest();
        login.setEmail("login@example.com");
        login.setPassword("mypassword");

        // When: the login endpoint is called with correct credentials
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsString(login)))
                // Then: the response contains a valid JWT and user details
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.id").value(u.getId()))
                .andExpect(jsonPath("$.username").value("login@example.com"))
                .andExpect(jsonPath("$.firstName").value("Charlie"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    public void authenticateUser_BadCredentials_Unauthorized() throws Exception {
        // Given: no user exists with the provided credentials
        LoginRequest login = new LoginRequest();
        login.setEmail("nouser@example.com");
        login.setPassword("wrong");

        // When: the login endpoint is called with invalid credentials
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsString(login)))
                // Then: the response should be 401 Unauthorized
                .andExpect(status().isUnauthorized());
    }
}

