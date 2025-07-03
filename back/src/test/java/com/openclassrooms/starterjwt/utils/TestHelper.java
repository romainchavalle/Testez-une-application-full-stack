package com.openclassrooms.starterjwt.utils;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TestHelper {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeacherRepository teacherRepository;
    private final SessionRepository sessionRepository;

    public TestHelper(UserRepository userRepository, PasswordEncoder passwordEncoder, TeacherRepository teacherRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.teacherRepository = teacherRepository;
        this.sessionRepository = sessionRepository;
    }

    public User createAdminUser(String email) {
        User user = User.builder()
                .email(email)
                .lastName("Doe")
                .firstName("John")
                .password(passwordEncoder.encode("password"))
                .admin(true)
                .build();
        return userRepository.save(user);
    }

    public Teacher createTeacher(String firstName, String lastName) {
        Teacher teacher = Teacher.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
        return teacherRepository.save(teacher);
    }

    public Session createSession() {
        Session session = Session.builder()
                .name("Test Session")
                .description("Ceci est une session de test")
                .date(new Date())
                .build();
        return sessionRepository.save(session);
    }
}
