package com.openclassrooms.starterjwt.utils;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestHelper {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TestHelper(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
}
