package com.leduy8.springbootjava.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.leduy8.springbootjava.core.dto.PaginatedResponseDTO;
import com.leduy8.springbootjava.user.dto.UserCreateRequestDTO;
import com.leduy8.springbootjava.user.dto.UserResponseDTO;
import com.leduy8.springbootjava.user.dto.UserUpdateRequestDTO;
import com.leduy8.springbootjava.user.exception.UserNotFoundException;
import com.leduy8.springbootjava.user.model.User;
import com.leduy8.springbootjava.user.repository.UserRepository;
import com.leduy8.springbootjava.user.service.UserServiceImpl;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserServiceImpl userService;

  private User testUser;
  private UserCreateRequestDTO createRequest;
  private UserUpdateRequestDTO updateRequest;

  @BeforeEach
  void setUp() {
    testUser =
        User.builder()
            .name("Test User")
            .email("test@example.com")
            .password("encodedPassword")
            .build();
    testUser.setId(1L);
    testUser.setCreatedAt(LocalDateTime.now());
    testUser.setUpdatedAt(LocalDateTime.now());

    createRequest = new UserCreateRequestDTO("Test User", "test@example.com", "password123");
    updateRequest = new UserUpdateRequestDTO("Updated User");
  }

  @Test
  void createUser_ShouldReturnUserResponseDTO() {
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    final UserResponseDTO result = userService.createUser(createRequest);

    assertNotNull(result);
    assertEquals(testUser.getId(), result.getId());
    assertEquals(testUser.getName(), result.getName());
    assertEquals(testUser.getEmail(), result.getEmail());
    verify(passwordEncoder).encode("password123");
    verify(userRepository).save(any(User.class));
  }

  @Test
  void getUserById_WhenUserExists_ShouldReturnUserResponseDTO() {
    // Given
    when(userRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(testUser));

    // When
    final Optional<UserResponseDTO> result = userService.getUserById(1L);

    // Then
    assertTrue(result.isPresent());
    assertEquals(testUser.getId(), result.get().getId());
    assertEquals(testUser.getName(), result.get().getName());
    assertEquals(testUser.getEmail(), result.get().getEmail());
    verify(userRepository).findByIdAndIsDeletedFalse(1L);
  }

  @Test
  void getUserById_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
    // Given
    when(userRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.empty());

    // When & Assert
    assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    verify(userRepository).findByIdAndIsDeletedFalse(1L);
  }

  @Test
  void getUsers_ShouldReturnPaginatedResponse() {
    // Given
    final List<User> users = Arrays.asList(testUser);
    final Page<User> page = new PageImpl<>(users);
    when(userRepository.findAllByIsDeletedFalse(any(Pageable.class))).thenReturn(page);

    // When
    final PaginatedResponseDTO<UserResponseDTO> result = userService.getUsers(0, 10, "id");

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalPages());
    assertEquals(1, result.getData().size());
    assertEquals(testUser.getId(), result.getData().get(0).getId());
    verify(userRepository).findAllByIsDeletedFalse(any(Pageable.class));
  }

  @Test
  void updateUser_WhenUserExists_ShouldReturnUpdatedUserResponseDTO() {
    final User updatedUser =
        User.builder()
            .name("Updated User")
            .email("test@example.com")
            .password("encodedPassword")
            .build();
    updatedUser.setId(1L);
    updatedUser.setCreatedAt(testUser.getCreatedAt());
    updatedUser.setUpdatedAt(LocalDateTime.now());

    when(userRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    final UserResponseDTO result = userService.updateUser(1L, updateRequest);

    assertNotNull(result);
    assertEquals(updatedUser.getId(), result.getId());
    assertEquals(updatedUser.getName(), result.getName());
    assertEquals(updatedUser.getEmail(), result.getEmail());
    verify(userRepository).findByIdAndIsDeletedFalse(1L);
    verify(userRepository).save(any(User.class));
  }

  @Test
  void updateUser_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
    // Given
    when(userRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.empty());

    // When & Assert
    assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, updateRequest));
    verify(userRepository).findByIdAndIsDeletedFalse(1L);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void existsByEmail_ShouldReturnTrueWhenEmailExists() {
    // Given
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    // When
    final boolean result = userService.existsByEmail("test@example.com");

    // Assert
    assertTrue(result);
    verify(userRepository).existsByEmail("test@example.com");
  }

  @Test
  void existsByEmail_ShouldReturnFalseWhenEmailDoesNotExist() {
    // Given
    when(userRepository.existsByEmail(anyString())).thenReturn(false);

    // When
    final boolean result = userService.existsByEmail("nonexistent@example.com");

    // Assert
    assertFalse(result);
    verify(userRepository).existsByEmail("nonexistent@example.com");
  }

  @Test
  void deleteUser_WhenUserExists_ShouldSoftDeleteUser() {
    // Given
    when(userRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // When
    userService.deleteUser(1L);

    // Assert
    verify(userRepository).findByIdAndIsDeletedFalse(1L);
    verify(userRepository).save(testUser);
  }

  @Test
  void deleteUser_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
    // Given
    when(userRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.empty());

    // When & Assert
    assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    verify(userRepository).findByIdAndIsDeletedFalse(1L);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
    // Given
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // When & Then
    assertThrows(
        UsernameNotFoundException.class,
        () -> userService.loadUserByUsername("nonexistent@example.com"));
    verify(userRepository).findByEmail("nonexistent@example.com");
  }

  @Test
  void getUsers_WithDescendingOrder_ShouldPassCorrectSortDirection() {
    // Given
    final List<User> users = Arrays.asList(testUser);
    final Page<User> page = new PageImpl<>(users);
    when(userRepository.findAllByIsDeletedFalse(any(Pageable.class))).thenReturn(page);

    // When
    userService.getUsers(0, 10, "-id");

    // Then
    verify(userRepository)
        .findAllByIsDeletedFalse(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id")));
  }
}
