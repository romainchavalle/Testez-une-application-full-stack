package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SessionRepository sessionRepository;

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

        mockMvc.perform(get("/api/session"))
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

        mockMvc.perform(get("/api/session/" + savedSession.getId()))
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Nouvelle Session"))
                .andExpect(jsonPath("$.description").value("Description de la nouvelle session"))
                .andExpect(jsonPath("$.teacher_id").value(savedTeacher.getId()));


        // ET : on a bien créé une ligne de plus
        assertEquals(countBefore + 1, sessionRepository.count());

    }

}
