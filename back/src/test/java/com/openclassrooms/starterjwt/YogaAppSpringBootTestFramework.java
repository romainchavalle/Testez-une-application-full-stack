package com.openclassrooms.starterjwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
