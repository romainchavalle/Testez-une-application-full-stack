package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.YogaAppSpringBootTestFramework;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.utils.TestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TeacherControllerTest extends YogaAppSpringBootTestFramework {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    private TestHelper testHelper;

    private Teacher savedTeacher;

    @AfterEach
    public void cleanUp() {
        teacherRepository.deleteAll();
    }

    @Test
    public void getAllTeacher() throws Exception {
        // Given: a teacher exists in the database
        Teacher teacher = testHelper.createTeacher("Jean", "Dupont");

        // When: performing GET request to retrieve all teachers
        mockMvc.perform(get("/api/teacher")
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                // Then: the response contains the saved teacher
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[-1].firstName").value("Jean"))
                .andExpect(jsonPath("$[-1].lastName").value("Dupont"));
    }

    @Test
    public void getTeacher() throws Exception {
        // Given: a teacher exists in the database
        Teacher teacher = testHelper.createTeacher("Jean", "Dupont");

        // When: performing GET request to retrieve the teacher by ID
        mockMvc.perform(get("/api/teacher/" + teacher.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                // Then: the response contains the correct teacher details
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jean"))
                .andExpect(jsonPath("$.lastName").value("Dupont"));
    }
}
