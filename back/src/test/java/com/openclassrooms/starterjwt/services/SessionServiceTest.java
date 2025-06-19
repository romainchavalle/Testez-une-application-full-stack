package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session createFakeSession(Long id, String name) {
        return Session.builder()
                .id(id)
                .name(name)
                .date(new Date())
                .description("Fake description")
                .teacher(null)
                .users(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateSession() {

        // Arrange
        Session session = new Session();
        session.setName("Test");

        when(sessionRepository.save(session)).thenReturn(session);

        // Act
        Session result = sessionService.create(session);

        // Assert
        assertNotNull(result);
        assertEquals("Test", result.getName());
        verify(sessionRepository).save(session);
    }

    @Test
    void testDeleteSession() {
        // Arrange
        Long id = 1L;

        // Act
        sessionService.delete(id);

        // Assert
        verify(sessionRepository).deleteById(id);
    }

    @Test
    void testFindAllSessions() {
        // Arrange
        List<Session> sessions = Arrays.asList(
                createFakeSession(1L, "Test1"), createFakeSession(2L, "Test2")
        );

        when(sessionRepository.findAll()).thenReturn(sessions);

        // Act
        List<Session> result = sessionService.findAll();

        // Assert
        assertEquals(2, result.size());
        verify(sessionRepository).findAll();
    }

    @Test
    void testGetByIdFound() {
        // Arrange
        Session session = createFakeSession(1L, "Found");

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        // Act
        Session result = sessionService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Found", result.getName());
    }

    @Test
    void testGetByIdNotFound() {
        // Arrange
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Session result = sessionService.getById(1L);

        // Assert
        assertNull(result);
    }


    @Test
    void testUpdateSession() {
        // Arrange
        Session session = new Session();
        session.setName("Updated");

        Session savedSession = createFakeSession(1L, "Updated");

        when(sessionRepository.save(any(Session.class))).thenReturn(savedSession);

        // Act
        Session result = sessionService.update(1L, session);


        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Updated", result.getName());

        verify(sessionRepository).save(session);
        assertEquals(1L, session.getId());
    }


}
