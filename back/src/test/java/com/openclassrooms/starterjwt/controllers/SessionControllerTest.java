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
import com.openclassrooms.starterjwt.utils.TestHelper;
import org.junit.jupiter.api.AfterEach;
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

    @Autowired
    private TestHelper testHelper;

    private Session savedSession;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    public void cleanUp() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindAll() throws Exception {
        // GIVEN: a session saved in the database
        Session session = Session.builder()
                .name("Test Session")
                .description("Ceci est une session de test")
                .date(new Date())
                .build();
        savedSession = sessionRepository.save(session);

        // WHEN: we perform a GET request to fetch all sessions
        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                // THEN: the response contains the saved session with correct name and description
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[-1].name").value("Test Session"))
                .andExpect(jsonPath("$[-1].description").value("Ceci est une session de test"));
    }

    @Test
    void testFindByValidId() throws Exception {
        // GIVEN: a session saved in the database
        Session session = Session.builder()
                .name("Test Session")
                .description("Ceci est une session de test")
                .date(new Date())
                .build();
        savedSession = sessionRepository.save(session);

        // WHEN: we perform a GET request to fetch the session by valid ID
        mockMvc.perform(get("/api/session/" + savedSession.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                // THEN: the response contains the correct session details
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Session"))
                .andExpect(jsonPath("$.description").value("Ceci est une session de test"));
    }

    @Test
    void testCreateValidSession() throws Exception {
        // GIVEN: a teacher saved in the database and a session DTO ready to be posted
        Teacher savedTeacher = testHelper.createTeacher("Jean", "Dupont");

        SessionDto dto = new SessionDto();
        dto.setName("Nouvelle Session");
        dto.setDescription("Description de la nouvelle session");
        dto.setDate(new Date());
        dto.setTeacher_id(savedTeacher.getId());

        long countBefore = sessionRepository.count();

        // WHEN: we perform a POST request to create a new session
        mockMvc.perform(post("/api/session")
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                // THEN: the response is OK and contains the created session data
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Nouvelle Session"))
                .andExpect(jsonPath("$.description").value("Description de la nouvelle session"))
                .andExpect(jsonPath("$.teacher_id").value(savedTeacher.getId()));

        // AND THEN: the session count in the database has increased by one
        assertEquals(countBefore + 1, sessionRepository.count());
    }

    @Test
    void testUpdateValidSession() throws Exception {
        // GIVEN: an existing session and teacher saved in the database
        Teacher savedTeacher = testHelper.createTeacher("Jean", "Dupont");

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

        // WHEN: we perform a PUT request to update the session
        mockMvc.perform(
                        put("/api/session/" + savedSession.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedDto))
                                .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                // THEN: the response is OK and contains the updated session data
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedSession.getId()))
                .andExpect(jsonPath("$.name").value("Session Modifiée"))
                .andExpect(jsonPath("$.description").value("Nouvelle description"))
                .andExpect(jsonPath("$.teacher_id").value(savedTeacher.getId()));

        // AND THEN: the session in the database is updated accordingly
        Session updatedSession = sessionRepository.findById(savedSession.getId()).orElseThrow(() -> new RuntimeException("Session not found"));
        assertEquals("Session Modifiée", updatedSession.getName());
        assertEquals("Nouvelle description", updatedSession.getDescription());
    }

    @Test
    void testDeleteValidSession() throws Exception {
        // GIVEN: an existing session saved in the database
        Session session = Session.builder()
                .name("Session à supprimer")
                .description("Description")
                .date(new Date())
                .build();
        savedSession = sessionRepository.save(session);
        Long sessionId = savedSession.getId();

        assertTrue(sessionRepository.findById(sessionId).isPresent());

        // WHEN: we perform a DELETE request to remove the session
        mockMvc.perform(delete("/api/session/" + sessionId)
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                // THEN: the response is OK and the session is deleted from the database
                .andExpect(status().isOk());

        assertFalse(sessionRepository.findById(sessionId).isPresent());
    }

    @Test
    void testParticipateSessionValid() throws Exception {
        // GIVEN: a user saved in the database
        User user = testHelper.createAdminUser("admin@example.com");

        // AND: a session saved in the database without participants
        Session session = Session.builder()
                .name("Session Participation")
                .description("Participate Test")
                .date(new Date())
                .users(new ArrayList<>())
                .build();
        session = sessionRepository.save(session);

        // WHEN: the user participates in the session via POST request
        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                .andExpect(status().isOk());

        // THEN: the user is added to the session participants list in the database
        Session updatedSession = sessionRepository.findById(session.getId()).orElseThrow(() -> new RuntimeException("Session not found"));

        boolean isParticipant = updatedSession.getUsers()
                .stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
        assertTrue(isParticipant);
    }

    @Test
    void testNoLongerParticipateSessionValid() throws Exception {
        // GIVEN: a user saved in the database
        User user = testHelper.createAdminUser("admin@example.com");

        // AND: a session saved in the database with the user as participant
        Session session = Session.builder()
                .name("Session à tester")
                .description("session avec user")
                .date(new Date())
                .users(new ArrayList<>())
                .build();
        session.getUsers().add(user);
        session = sessionRepository.save(session);

        // Ensure the user is initially a participant
        assertEquals(1, sessionRepository.findById(session.getId()).get().getUsers().size());

        // WHEN: the user is removed from the session participants via DELETE request
        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + user.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                .andExpect(status().isOk());

        // THEN: the user is no longer in the session participants list
        Session updatedSession = sessionRepository.findById(session.getId()).orElseThrow(() -> new RuntimeException("Session not found"));
        assertEquals(0, updatedSession.getUsers().size());
    }

}
