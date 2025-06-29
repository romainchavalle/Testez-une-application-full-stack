package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperImplTest {

    private UserMapperImpl userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    @Test
    void shouldMapDtoToEntity() {
        // Préparation du DTO complet
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail("test@example.com");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPassword("secret");
        dto.setAdmin(true);
        dto.setCreatedAt(LocalDateTime.of(2025, 6, 29, 10, 0));
        dto.setUpdatedAt(LocalDateTime.of(2025, 6, 29, 12, 0));

        // Mapping
        User entity = userMapper.toEntity(dto);

        // Assertions
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getEmail(), entity.getEmail());
        assertEquals(dto.getFirstName(), entity.getFirstName());
        assertEquals(dto.getLastName(), entity.getLastName());
        assertEquals(dto.getPassword(), entity.getPassword());
        assertEquals(dto.isAdmin(), entity.isAdmin());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    void shouldMapEntityToDto() {
        // Préparation de l'Entity complète
        User user = User.builder()
                .id(2L)
                .email("jane@domain.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("topsecret")
                .admin(false)
                .createdAt(LocalDateTime.of(2025, 1, 15, 8, 30))
                .updatedAt(LocalDateTime.of(2025, 1, 15, 9, 45))
                .build();

        // Mapping
        UserDto dto = userMapper.toDto(user);

        // Assertions
        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getFirstName(), dto.getFirstName());
        assertEquals(user.getLastName(), dto.getLastName());
        assertEquals(user.getPassword(), dto.getPassword());
        assertEquals(user.isAdmin(), dto.isAdmin());
        assertEquals(user.getCreatedAt(), dto.getCreatedAt());
        assertEquals(user.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    void shouldMapDtoListToEntityList() {
        UserDto dto = new UserDto();
        dto.setId(3L);
        dto.setEmail("list@example.com");
        dto.setFirstName("Foo");
        dto.setLastName("Bar");
        dto.setPassword("pwd");
        dto.setAdmin(true);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        List<UserDto> dtoList = new ArrayList<>();
        dtoList.add(dto);

        List<User> users = userMapper.toEntity(dtoList);

        assertNotNull(users);
        assertEquals(1, users.size());
        User u = users.get(0);
        assertEquals(dto.getId(), u.getId());
        assertEquals(dto.getEmail(), u.getEmail());
    }

    @Test
    void shouldMapEntityListToDtoList() {
        User user = User.builder()
                .id(4L)
                .email("list2@example.com")
                .firstName("Baz")
                .lastName("Qux")
                .password("pwd2")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<User> userList = new ArrayList<>();
        userList.add(user);

        List<UserDto> dtos = userMapper.toDto(userList);

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        UserDto d = dtos.get(0);
        assertEquals(user.getId(), d.getId());
        assertEquals(user.getEmail(), d.getEmail());
    }

    @Test
    void shouldReturnNullForNullInputs() {
        assertNull(userMapper.toEntity((UserDto) null));
        assertNull(userMapper.toDto((User) null));
        assertNull(userMapper.toEntity((List<UserDto>) null));
        assertNull(userMapper.toDto((List<User>) null));
    }
}
