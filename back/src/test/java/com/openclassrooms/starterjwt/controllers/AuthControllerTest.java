package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.YogaAppSpringBootTestFramework;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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

    @Test
    public void registerUser_Success() throws Exception {
        SignupRequest signup = new SignupRequest();
        signup.setEmail("newuser@example.com");
        signup.setFirstName("Alice");
        signup.setLastName("Smith");
        signup.setPassword("secret");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsString(signup)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        // Vérifie que l'utilisateur a bien été persisté
        assertThat(userRepository.existsByEmail("newuser@example.com")).isTrue();
        User saved = userRepository.findByEmail("newuser@example.com").get();
        assertThat(passwordEncoder.matches("secret", saved.getPassword())).isTrue();
    }


    @Test
    public void authenticateUser_Success() throws Exception {
        // Création en base
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

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsString(login)))
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
        // Pas d'utilisateur en base ou mauvais mot de passe
        LoginRequest login = new LoginRequest();
        login.setEmail("nouser@example.com");
        login.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}
