package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeacherMapperImplTest {

    private TeacherMapperImpl teacherMapper;

    @BeforeEach
    void setUp() {
        teacherMapper = new TeacherMapperImpl();
    }

    @Test
    void shouldMapDtoToEntity() {
        // Préparation du DTO complet
        TeacherDto dto = new TeacherDto();
        dto.setId(10L);
        dto.setLastName("Durand");
        dto.setFirstName("Alice");
        dto.setCreatedAt(LocalDateTime.of(2025, 2, 14, 9, 0));
        dto.setUpdatedAt(LocalDateTime.of(2025, 2, 14, 10, 30));

        // Mapping
        Teacher entity = teacherMapper.toEntity(dto);

        // Assertions
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getLastName(), entity.getLastName());
        assertEquals(dto.getFirstName(), entity.getFirstName());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    void shouldMapEntityToDto() {
        // Préparation de l'entité complète
        Teacher teacher = Teacher.builder()
                .id(20L)
                .lastName("Martin")
                .firstName("Bob")
                .createdAt(LocalDateTime.of(2025, 3, 1, 8, 15))
                .updatedAt(LocalDateTime.of(2025, 3, 1, 9, 45))
                .build();

        // Mapping
        TeacherDto dto = teacherMapper.toDto(teacher);

        // Assertions
        assertNotNull(dto);
        assertEquals(teacher.getId(), dto.getId());
        assertEquals(teacher.getLastName(), dto.getLastName());
        assertEquals(teacher.getFirstName(), dto.getFirstName());
        assertEquals(teacher.getCreatedAt(), dto.getCreatedAt());
        assertEquals(teacher.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    void shouldMapDtoListToEntityList() {
        TeacherDto dto = new TeacherDto();
        dto.setId(30L);
        dto.setLastName("Petit");
        dto.setFirstName("Claire");
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        List<TeacherDto> dtoList = new ArrayList<>();
        dtoList.add(dto);

        List<Teacher> teachers = teacherMapper.toEntity(dtoList);

        assertNotNull(teachers);
        assertEquals(1, teachers.size());
        Teacher t = teachers.get(0);
        assertEquals(dto.getId(), t.getId());
        assertEquals(dto.getLastName(), t.getLastName());
        assertEquals(dto.getFirstName(), t.getFirstName());
    }

    @Test
    void shouldMapEntityListToDtoList() {
        Teacher teacher = Teacher.builder()
                .id(40L)
                .lastName("Leroy")
                .firstName("David")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<Teacher> teacherList = new ArrayList<>();
        teacherList.add(teacher);

        List<TeacherDto> dtos = teacherMapper.toDto(teacherList);

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        TeacherDto d = dtos.get(0);
        assertEquals(teacher.getId(), d.getId());
        assertEquals(teacher.getLastName(), d.getLastName());
        assertEquals(teacher.getFirstName(), d.getFirstName());
    }

    @Test
    void shouldReturnNullForNullInputs() {
        assertNull(teacherMapper.toEntity((TeacherDto) null));
        assertNull(teacherMapper.toDto((Teacher) null));
        assertNull(teacherMapper.toEntity((List<TeacherDto>) null));
        assertNull(teacherMapper.toDto((List<Teacher>) null));
    }
}
