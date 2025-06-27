package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.YogaAppSpringBootTestFramework;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class UserControllerTest extends YogaAppSpringBootTestFramework {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void findById() throws Exception {
        User user = User.builder()
                .email("test@oc.com")
                .lastName("Durand")
                .firstName("Marie")
                .password("password")
                .build();
        user = userRepository.save(user);

        mockMvc.perform(get("/api/user/"+ user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@oc.com"))
                .andExpect(jsonPath("$.lastName").value("Durand"))
                .andExpect(jsonPath("$.firstName").value("Marie"));

    }

    @Test
    public void deleteUser() throws Exception {
        User user = User.builder()
                .email("test@oc.com")
                .lastName("Durand")
                .firstName("Marie")
                .password("password")
                .build();
        user = userRepository.save(user);
        Long userId = user.getId();

        assertTrue(userRepository.findById(userId).isPresent());

        mockMvc.perform(delete("/api/user/"+ user.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                .andExpect(status().isOk());

        assertFalse(userRepository.findById(userId).isPresent());
    }
}
