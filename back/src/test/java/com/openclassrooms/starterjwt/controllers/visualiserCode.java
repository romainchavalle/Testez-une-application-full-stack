package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.YogaAppMvcSpringBootTestFramework;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SessionControllerTest extends YogaAppMvcSpringBootTestFramework {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @AfterEach
    public void cleanUp() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        getUserRepository().deleteAll();
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
    public void testFindByValidId() throws Exception {
        // Given
        Session session = new Session();
        session.setName("Session 1");
        session.setDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        session.setDescription("Description 1");
        session = sessionRepository.save(session);

        //When
        mockMvc.perform(get("/api/session/" + session.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Session 1"));
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

    @Test
    public void testFindAll() throws Exception {
        // Given
        Session session = new Session();
        session.setName("Session 1");
        session.setDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        session.setDescription("Description 1");
        session = sessionRepository.save(session);

        //When
        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Session 1"));
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

    @Test
    public void testCreateValidSession() throws Exception {
        // Given
        Teacher teacher = new Teacher();
        teacher.setFirstName("Teacher");
        teacher.setLastName("1");
        teacher = teacherRepository.save(teacher);

        SessionDto session = new SessionDto();
        session.setDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        session.setDescription("Description 2");
        session.setName("Session 2");
        session.setTeacher_id(teacher.getId());

        //When
        mockMvc.perform(post("/api/session")
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(session)))
                //Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Session 2"));
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

    @Test
    public void testUpdateValidSession() throws Exception {
        // Given
        Teacher teacher = new Teacher();
        teacher.setFirstName("Teacher");
        teacher.setLastName("1");
        teacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Session 1");
        session.setDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        session.setDescription("Description 1");
        session = sessionRepository.save(session);

        SessionDto sessionDto = new SessionDto();
        sessionDto.setDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        sessionDto.setDescription("Description 2");
        sessionDto.setName("Session 2");
        sessionDto.setTeacher_id(teacher.getId());

        //When
        mockMvc.perform(put("/api/session/" + session.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(sessionDto)))
                //Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Session 2"));
    }

    @Test
    public void testDeleteNotFoundSession() throws Exception {
        // Given
        //When
        mockMvc.perform(delete("/api/session/1")
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isNotFound());
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

    @Test
    public void testDeleteValidSession() throws Exception {
        // Given
        Session session = new Session();
        session.setName("Session 1");
        session.setDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        session.setDescription("Description 1");
        session = sessionRepository.save(session);

        //When
        mockMvc.perform(delete("/api/session/" + session.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isOk());
        assertEquals(0, sessionRepository.count());
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

    @Test
    public void testParticipateSessionValid() throws Exception {
        // Given
        User user = new User(
                null,
                "user@example.com",
                "1",
                "User",
                "password",
                false,
                null,
                null
        );
        user = getUserRepository().save(user);


        Session session = new Session();
        session.setName("Session 1");
        session.setDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        session.setDescription("Description 1");
        session = sessionRepository.save(session);

        //When
        mockMvc.perform(post("/api/session/"+session.getId()+"/participate/" + user.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isOk());

        List<User> users = sessionRepository.findById(session.getId()).get().getUsers();
        assertEquals(1, users.size());
        assertEquals("User", users.get(0).getFirstName());
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

    @Test
    public void testNoLongerParticipateSessionValid() throws Exception {
        // Given
        User user = new User(
                "user@example.com",
                "1",
                "User",
                "password",
                false
        );
        user = getUserRepository().save(user);

        Session session = new Session(
                null,
                "Session 1",
                new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000),
                "Description 1",
                null,
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        session.getUsers().add(user);
        session = sessionRepository.save(session);

        //When
        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + user.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken()))
                //Then
                .andExpect(status().isOk());

        List<User> users = sessionRepository.findById(session.getId()).get().getUsers();
        assertEquals(0, users.size());
    }

}
----
        package com.openclassrooms.starterjwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class YogaAppSpringBootTestFramework {

    @Getter
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    @Autowired
    private UserRepository userRepository;

    @Getter
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    private User admin;

    protected String getAdminAccessToken() {
        if (admin == null) {

            if (userRepository.findByEmail("admin@example.com").isPresent()) {
                admin = userRepository.findByEmail("admin@example.com").get();
            } else {
                admin = new User();
                admin.setEmail("admin@example.com");
                admin.setLastName("Doe");
                admin.setFirstName("John");
                admin.setAdmin(true);
                admin.setPassword(passwordEncoder.encode("password"));

                admin = userRepository.save(admin);
            }
        }

        return authenticate(admin.getEmail(), "password");
    }

    protected String authenticate(String email, String password) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));

        return jwtUtils.generateJwtToken(authentication);
    }
}