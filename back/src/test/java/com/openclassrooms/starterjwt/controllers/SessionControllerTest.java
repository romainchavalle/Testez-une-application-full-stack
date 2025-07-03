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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    // ALL SESSIONS
    @Test
    void testFindAll() throws Exception {
        // GIVEN: a session saved in the database
        savedSession = testHelper.createSession();

        // WHEN: we perform a GET request to fetch all sessions
        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                // THEN: the response contains the saved session with correct name and description
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[-1].name").value("Test Session"))
                .andExpect(jsonPath("$[-1].description").value("Ceci est une session de test"));
    }

    // SESSIONS BY ID
    @Test
    void testFindByValidId() throws Exception {
        // GIVEN: a session saved in the database
        savedSession = testHelper.createSession();

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
    public void testFindByIdInvalidInteger() throws Exception {
        // Given
        //When
        mockMvc.perform(get("/api/session/invalid")
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindByIdNotFound() throws Exception {
        // Given
        //When
        mockMvc.perform(get("/api/session/1")
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isNotFound());
    }


    // CREATE SESSION
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
    public void testCreateInvalidSession() throws Exception {
        // Given
        //When
        mockMvc.perform(post("/api/session")
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                        .contentType("application/json")
                        .content("{}"))
                //Then
                .andExpect(status().isBadRequest());
    }


    // UPDATE SESSION
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
    public void testUpdateInvalidIntegerSession() throws Exception {
        // Given
        Teacher teacher = new Teacher();
        teacher.setFirstName("Teacher");
        teacher.setLastName("1");
        teacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Session 1");
        session.setDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        session.setDescription("Description 1");
        sessionRepository.save(session);

        SessionDto sessionDto = new SessionDto();
        sessionDto.setDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        sessionDto.setDescription("Description 2");
        sessionDto.setName("Session 2");
        sessionDto.setTeacher_id(teacher.getId());

        //When
        mockMvc.perform(put("/api/session/invalid")
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(sessionDto)))
                //Then
                .andExpect(status().isBadRequest());
    }


    // DELETE SESSION
    @Test
    void testDeleteValidSession() throws Exception {
        // GIVEN: an existing session saved in the database
        savedSession = testHelper.createSession();
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
    public void testDeleteInvalidIntegerSession() throws Exception {
        // Given
        //When
        mockMvc.perform(delete("/api/session/invalid")
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isBadRequest());
    }


    // PARTICIPATE
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
    public void testParticipateInvalidIntegerSession() throws Exception {
        // Given
        //When
        mockMvc.perform(post("/api/session/invalid/participate/1")
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testParticipateSessionInvalidIntegerUser() throws Exception {
        // Given
        //When
        mockMvc.perform(post("/api/session/1/participate/invalid")
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testParticipateNotFoundSession() throws Exception {
        // Given
        //When
        mockMvc.perform(post("/api/session/1/participate/1")
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isNotFound());
    }

    @Test
    public void testParticipateSessionNotFoundUser() throws Exception {
        // Given
        Session session = new Session();
        session.setName("Session 1");
        session.setDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        session.setDescription("Description 1");
        session = sessionRepository.save(session);

        //When
        mockMvc.perform(post("/api/session/"+session.getId()+"/participate/1")
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isNotFound());
    }

    // NO LONGER PARTICPATE
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

    @Test
    public void testNoLongerParticipateInvalidIntegerSession() throws Exception {
        // Given
        //When
        mockMvc.perform(delete("/api/session/invalid/participate/1")
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNoLongerParticipateSessionInvalidIntegerUser() throws Exception {
        // Given
        Session session = Session.builder()
                .name("Session 1")
                .date(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .description("Description 1")
                .build();
        session = sessionRepository.save(session);

        //When
        mockMvc.perform(delete("/api/session/"+session.getId()+"/participate/invalid")
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNoLongerParticipateNotFoundSession() throws Exception {
        // Given
        //When
        mockMvc.perform(delete("/api/session/1/participate/1")
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isNotFound());
    }

    @Test
    public void testNoLongerParticipateSessionNotParticipatingUser() throws Exception {
        // Given
        Session session = new Session();
        session.setName("Session 1");
        session.setDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        session.setDescription("Description 1");
        session = sessionRepository.save(session);

        //When
        mockMvc.perform(delete("/api/session/"+session.getId()+"/participate/100")
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isBadRequest());
    }

}
