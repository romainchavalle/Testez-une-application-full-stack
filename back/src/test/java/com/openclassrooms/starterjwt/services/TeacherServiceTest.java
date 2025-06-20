package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;


    @Test
    void findAll_shouldReturnListOfTeachers() {
        // Arrange
        Teacher t1 = new Teacher();
        t1.setId(1L);
        t1.setLastName("Durand");

        Teacher t2 = new Teacher();
        t2.setId(2L);
        t2.setLastName("Dupont");

        when(teacherRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        // Act
        List<Teacher> result = teacherService.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Durand", result.get(0).getLastName());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void findById_existingId_shouldReturnTeacher() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setLastName("Durand");

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        // Act
        Teacher result = teacherService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Durand", result.getLastName());
        verify(teacherRepository).findById(1L);
    }

    @Test
    void findById_nonExistingId_shouldReturnNull() {
        // Arrange
        when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Teacher result = teacherService.findById(999L);

        // Assert
        assertNull(result);
        verify(teacherRepository).findById(999L);
    }

}
