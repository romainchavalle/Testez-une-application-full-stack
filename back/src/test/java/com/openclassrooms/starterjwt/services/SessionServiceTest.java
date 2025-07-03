package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

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

    @Test
    void shouldAddUserToSession() {
        // Arrange
        Session session = createFakeSession(1L, "Participate");
        User user = new User();
        user.setId(1L);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        sessionService.participate(1L, 1L);

        // Arrange
        assertTrue(session.getUsers().contains(user));
        verify(sessionRepository).save(session);
    }

    @Test
    void shouldThrowNotFoundException_whenSessionDoesNotExist() {

        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void shouldRemoveUserFromSession() {
        // Arrange
        Session session = createFakeSession(1L, "NoLongerParticipate");
        User user = new User();
        user.setId(1L);
        session.getUsers().add(user);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        // Act
        sessionService.noLongerParticipate(1L, 1L);

        // Assert
        assertFalse(session.getUsers().contains(user));
        verify(sessionRepository).save(session);
    }

    @Test
    void shouldThrowNotFoundException_noLongerParticipate() {
        when(sessionRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(100L, 1L));
        verify(sessionRepository, never()).save(any());
    }



}
