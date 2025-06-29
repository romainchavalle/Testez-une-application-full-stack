package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.YogaAppSpringBootTestFramework;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TeacherControllerTest extends YogaAppSpringBootTestFramework {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TeacherRepository teacherRepository;

    private Teacher savedTeacher;

    @Test
    public void getAllTeacher()  throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .build();
        savedTeacher = teacherRepository.save(teacher);

        mockMvc.perform(get("/api/teacher")
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[-1].firstName").value("Jean"))
                .andExpect(jsonPath("$[-1].lastName").value("Dupont"));

    }

    @Test
    public void getTeacher() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .build();
        savedTeacher = teacherRepository.save(teacher);

        mockMvc.perform(get("/api/teacher/" + savedTeacher.getId())
                        .header("Authorization", "Bearer " + getAdminAccessToken())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jean"))
                .andExpect(jsonPath("$.lastName").value("Dupont"));
    }
}
