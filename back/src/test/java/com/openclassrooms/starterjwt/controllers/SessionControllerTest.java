package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.YogaAppSpringBootTestFramework;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;


public class SessionControllerTest extends YogaAppSpringBootTestFramework {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Session savedSession;
    private Teacher savedTeacher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldReturnSession() throws Exception {
        // GIVEN
        Session session = Session.builder()
                .name("Test Session")
                .description("Ceci est une session de test")
                .date(new Date())
                .build();
        savedSession = sessionRepository.save(session);

        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[-1].name").value("Test Session"))
                .andExpect(jsonPath("$[-1].description").value("Ceci est une session de test"));

    }

    @Test
    void shouldReturnOneSession() throws Exception {
        // GIVEN - Crée des sessions en base
        Session session = Session.builder()
                .name("Test Session")
                .description("Ceci est une session de test")
                .date(new Date())
                .build();
        savedSession = sessionRepository.save(session);

        mockMvc.perform(get("/api/session/" + savedSession.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Session"))
                .andExpect(jsonPath("$.description").value("Ceci est une session de test"));

    }

    @Test
    void shouldCreateSession() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .build();
        savedTeacher = teacherRepository.save(teacher);

        SessionDto dto = new SessionDto();
        dto.setName("Nouvelle Session");
        dto.setDescription("Description de la nouvelle session");
        dto.setDate(new Date());
        dto.setTeacher_id(savedTeacher.getId());

        long countBefore = sessionRepository.count();

        // WHEN : appel POST
        mockMvc.perform(post("/api/session")
                .header("Authorization", "Bearer " + getAdminAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Nouvelle Session"))
                .andExpect(jsonPath("$.description").value("Description de la nouvelle session"))
                .andExpect(jsonPath("$.teacher_id").value(savedTeacher.getId()));


        // ET : on a bien créé une ligne de plus
        assertEquals(countBefore + 1, sessionRepository.count());

    }

    @Test
    void shouldUpdateSession() throws Exception {
        // GIVEN : une session et un enseignant existent déjà
        Teacher teacher = Teacher.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .build();
        savedTeacher = teacherRepository.save(teacher);

        Session session = Session.builder()
                .name("Ancienne Session")
                .description("Ancienne description")
                .date(new Date())
                .teacher(savedTeacher)
                .build();
        savedSession = sessionRepository.save(session);

        SessionDto updatedDto = new SessionDto();
        updatedDto.setId(savedSession.getId());
        updatedDto.setName("Session Modifiée");
        updatedDto.setDescription("Nouvelle description");
        updatedDto.setDate(new Date());
        updatedDto.setTeacher_id(savedTeacher.getId());

        mockMvc.perform(
                put("/api/session/" + savedSession.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto))
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedSession.getId()))
                .andExpect(jsonPath("$.name").value("Session Modifiée"))
                .andExpect(jsonPath("$.description").value("Nouvelle description"))
                .andExpect(jsonPath("$.teacher_id").value(savedTeacher.getId()));

        Session updatedSession = sessionRepository.findById(savedSession.getId()).orElseThrow(() -> new RuntimeException("Session not found"));
        assertEquals("Session Modifiée", updatedSession.getName());
        assertEquals("Nouvelle description", updatedSession.getDescription());
    }

    @Test
    void shouldDeleteSession() throws Exception {
        // GIVEN - Une session existante à supprimer
        Session session = Session.builder()
                .name("Session à supprimer")
                .description("Description")
                .date(new Date())
                .build();
        savedSession = sessionRepository.save(session);
        Long sessionId = savedSession.getId();

        assertTrue(sessionRepository.findById(sessionId).isPresent());

        mockMvc.perform(delete("/api/session/" + sessionId)
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                .andExpect(status().isOk());

        assertFalse(sessionRepository.findById(sessionId).isPresent());
    }

    @Test
    void shouldAddUserToSession() throws Exception {
        // GIVEN : Un utilisateur
        User user = User.builder()
                .email("admin@example.com")
                .lastName("Doe")
                .firstName("John")
                .password(passwordEncoder.encode("password"))
                .admin(true)
                .build();
        user = userRepository.save(user);

        // ET une session
        Session session = Session.builder()
                .name("Session Participation")
                .description("Participate Test")
                .date(new Date())
                .users(new ArrayList<>())
                .build();
        session = sessionRepository.save(session);

        // WHEN : l’utilisateur participe
        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                .andExpect(status().isOk());

        // THEN : l’utilisateur est bien dans la session
        Session updatedSession = sessionRepository.findById(session.getId()).orElseThrow(() -> new RuntimeException("Session not found"));

        User finalUser = user;
        boolean isParticipant = updatedSession.getUsers()
                .stream()
                .anyMatch(u -> u.getId().equals(finalUser.getId()));
        assertTrue(isParticipant);
    }

    @Test
    void shouldRemoveUserFromSession() throws Exception {
        // Crée et sauve un utilisateur
        User user = User.builder()
                .email("admin@example.com")
                .lastName("Doe")
                .firstName("John")
                .password(passwordEncoder.encode("password"))
                .admin(true)
                .build();
        user = userRepository.save(user);

        // Crée et sauve une session avec ce user dans la liste
        Session session = Session.builder()
                .name("Session à tester")
                .description("session avec user")
                .date(new Date())
                .users(new ArrayList<>())
                .build();
        session.getUsers().add(user);
        session = sessionRepository.save(session);

        // Assure que le user est bien lié
        assertEquals(1, sessionRepository.findById(session.getId()).get().getUsers().size());

        // Appelle le DELETE /api/session/{id}/participate/{userId}
        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + user.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                .andExpect(status().isOk());

        // Recharge la session pour vérifier que le user a bien été retiré
        Session updatedSession = sessionRepository.findById(session.getId()).orElseThrow(() -> new RuntimeException("Session not found"));
        assertEquals(0, updatedSession.getUsers().size());
    }



}
