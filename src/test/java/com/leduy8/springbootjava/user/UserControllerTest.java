package com.leduy8.springbootjava.user;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leduy8.springbootjava.core.dto.PaginatedResponseDTO;
import com.leduy8.springbootjava.core.exception.GlobalExceptionHandler;
import com.leduy8.springbootjava.user.controller.UserController;
import com.leduy8.springbootjava.user.dto.UserCreateRequestDTO;
import com.leduy8.springbootjava.user.dto.UserResponseDTO;
import com.leduy8.springbootjava.user.dto.UserUpdateRequestDTO;
import com.leduy8.springbootjava.user.exception.UserNotFoundException;
import com.leduy8.springbootjava.user.model.User;
import com.leduy8.springbootjava.user.service.UserService;
import java.time.LocalDateTime;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Mock private UserService userService;

  @InjectMocks private UserController userController;

  private UserCreateRequestDTO createRequest;
  private UserUpdateRequestDTO updateRequest;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setValidator(new LocalValidatorFactoryBean())
            .build();

    createRequest = new UserCreateRequestDTO("john_doe", "john@example.com", "password123");
    updateRequest = new UserUpdateRequestDTO("john_dee");
  }

  @Test
  void testGetUserById_ShouldReturnUser() throws Exception {
    // Given
    final User user = User.builder().email("john@example.com").name("john_doe").build();
    user.setId(1L);
    final UserResponseDTO userResponseDTO = UserResponseDTO.of(user, UserResponseDTO.class);

    when(userService.getUserById(1L)).thenReturn(Optional.of(userResponseDTO));

    // When & Then
    mockMvc
        .perform(get("/api/users/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("john_doe")))
        .andExpect(jsonPath("$.email", is("john@example.com")));

    verify(userService, times(1)).getUserById(1L);
  }

  @Test
  void testGetUserById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
    // Given
    when(userService.getUserById(2L)).thenThrow(new UserNotFoundException(2L));

    // When & Then
    mockMvc
        .perform(get("/api/users/2").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(userService, times(1)).getUserById(2L);
  }

  @Test
  void testCreateUser_ShouldReturnUser() throws Exception {
    // Given
    final User user = User.builder().email("john@example.com").name("john_doe").build();
    user.setId(1L);
    final UserResponseDTO userResponseDTO = UserResponseDTO.of(user, UserResponseDTO.class);

    when(userService.createUser(createRequest)).thenReturn(userResponseDTO);

    // When & Then
    mockMvc
        .perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("john_doe")))
        .andExpect(jsonPath("$.email", is("john@example.com")));

    verify(userService, times(1)).createUser(createRequest);
  }

  @Test
  void testCreateUser_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
    // Given: Invalid user request (missing email)
    final UserCreateRequestDTO invalidRequest =
        new UserCreateRequestDTO("John Doe", "", "password123");

    // When & Then: Expect 400 Bad Request
    mockMvc
        .perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testGetUsers_ShouldReturnUser() throws Exception {
    // Given
    final int page = 1;
    final int size = 10;
    final String orderBy = "id";

    final User user = User.builder().email("john@example.com").name("john_doe").build();
    user.setId(1L);
    user.setPassword("encodedPassword");
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());

    final List<User> users = List.of(user);
    final Page<User> userPage = new PageImpl<>(users);
    final PaginatedResponseDTO<UserResponseDTO> paginatedResponseDTO =
        PaginatedResponseDTO.of(userPage, UserResponseDTO.class);
    when(userService.getUsers(page - 1, size, orderBy)).thenReturn(paginatedResponseDTO);

    // When & Then
    mockMvc
        .perform(
            get("/api/users")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("orderBy", orderBy)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].id", is(1)))
        .andExpect(jsonPath("$.data[0].name", is("john_doe")))
        .andExpect(jsonPath("$.data[0].email", is("john@example.com")))
        .andExpect(jsonPath("$.currentPage", is(1)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.totalItems", is(1)))
        .andExpect(
            jsonPath(
                "$.pageSize",
                is(1))); // Should be 10, but due to mock userPage, so we just assert it with 1

    verify(userService, times(1)).getUsers(0, 10, "id");
  }

  @Test
  void testUpdateUser_ShouldReturnUser() throws Exception {
    // Given
    final User user = User.builder().email("john@example.com").name("john_doe").build();
    user.setId(1L);
    final UserResponseDTO userResponseDTO = UserResponseDTO.of(user, UserResponseDTO.class);

    when(userService.updateUser(1L, updateRequest)).thenReturn(userResponseDTO);

    // When & Then
    mockMvc
        .perform(
            put("/api/users/" + user.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("john_doe")))
        .andExpect(jsonPath("$.email", is("john@example.com")));

    verify(userService, times(1)).updateUser(1L, updateRequest);
  }

  @Test
  void testUpdateUser_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
    // Given: Invalid user request (missing email)
    final UserUpdateRequestDTO invalidRequest = new UserUpdateRequestDTO("");

    // When & Then: Expect 400 Bad Request
    mockMvc
        .perform(
            put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testUpdateUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
    // Given
    when(userService.updateUser(2L, updateRequest)).thenThrow(new UserNotFoundException(2L));

    // When & Then
    mockMvc
        .perform(
            put("/api/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
    // Given
    // When & Then
    mockMvc
        .perform(delete("/api/users/2").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
