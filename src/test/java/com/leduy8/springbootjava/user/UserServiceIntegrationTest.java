package com.leduy8.springbootjava.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.leduy8.springbootjava.core.dto.PaginatedResponseDTO;
import com.leduy8.springbootjava.user.dto.UserCreateRequestDTO;
import com.leduy8.springbootjava.user.dto.UserResponseDTO;
import com.leduy8.springbootjava.user.dto.UserUpdateRequestDTO;
import com.leduy8.springbootjava.user.exception.UserNotFoundException;
import com.leduy8.springbootjava.user.model.User;
import com.leduy8.springbootjava.user.repository.UserRepository;
import com.leduy8.springbootjava.user.service.UserServiceImpl;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

  @Autowired private UserRepository userRepository;

  @Autowired private UserServiceImpl userService;

  private User testUser;

  private UserCreateRequestDTO createRequest;
  private UserUpdateRequestDTO updateRequest;

  @BeforeEach
  void setUp() {
    testUser =
        User.builder().name("Test User").email("testUser@email.com").password("mypassword").build();
    createRequest = new UserCreateRequestDTO("Test User", "test@example.com", "encodedPassword");
    updateRequest = new UserUpdateRequestDTO("Updated User");
  }

  User _createTestUser(User user) {
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    return userRepository.save(user);
  }

  @Test
  void createUser_ShouldReturnUserResponseDTO() {
    final UserResponseDTO result = userService.createUser(createRequest);

    assertNotNull(result);
    assertTrue(userService.existsByEmail(createRequest.email()));
  }

  @Test
  void getUserById_WhenUserExists_ShouldReturnUserResponseDTO() {
    final User user = _createTestUser(testUser);
    final Optional<UserResponseDTO> result = userService.getUserById(user.getId());

    assertTrue(result.isPresent());
    assertEquals(user.getId(), result.get().getId());
    assertEquals(user.getName(), result.get().getName());
    assertEquals(user.getEmail(), result.get().getEmail());
  }

  @Test
  void getUserById_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
    assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
  }

  @Test
  void getUsers_ShouldReturnPaginatedResponse() {
    final User user = _createTestUser(testUser);

    final PaginatedResponseDTO<UserResponseDTO> result = userService.getUsers(0, 10, "id");

    assertNotNull(result);
    assertEquals(1, result.getTotalPages());
    assertEquals(1, result.getData().size());
    assertEquals(user.getId(), result.getData().get(0).getId());
  }

  @Test
  void updateUser_WhenUserExists_ShouldReturnUpdatedUserResponseDTO() {
    final User user = _createTestUser(testUser);

    final UserResponseDTO result = userService.updateUser(user.getId(), updateRequest);

    assertNotNull(result);
    assertEquals(user.getId(), result.getId());
    assertEquals(user.getName(), result.getName());
    assertEquals(user.getEmail(), result.getEmail());
  }

  @Test
  void updateUser_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
    assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, updateRequest));
  }

  @Test
  void existsByEmail_ShouldReturnTrueWhenEmailExists() {
    final User user = _createTestUser(testUser);

    final boolean result = userService.existsByEmail(user.getEmail());

    assertTrue(result);
  }

  @Test
  void existsByEmail_ShouldReturnFalseWhenEmailDoesNotExist() {
    final boolean result = userService.existsByEmail("nonexistent@example.com");
    assertFalse(result);
  }

  @Test
  void deleteUser_WhenUserExists_ShouldSoftDeleteUser() {
    final User user = _createTestUser(testUser);

    userService.deleteUser(user.getId());

    assertThrows(UserNotFoundException.class, () -> userService.getUserById(user.getId()));
  }

  @Test
  void deleteUser_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
    assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
  }
}
