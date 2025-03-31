package com.leduy8.springbootjava.user.service;

import com.leduy8.springbootjava.core.dto.PaginatedResponseDTO;
import com.leduy8.springbootjava.user.dto.UserCreateRequestDTO;
import com.leduy8.springbootjava.user.dto.UserResponseDTO;
import com.leduy8.springbootjava.user.dto.UserUpdateRequestDTO;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
  UserResponseDTO createUser(UserCreateRequestDTO request);

  Optional<UserResponseDTO> getUserById(Long id);

  PaginatedResponseDTO<UserResponseDTO> getUsers(int page, int size, String orderBy);

  UserResponseDTO updateUser(Long id, UserUpdateRequestDTO request);

  boolean existsByEmail(String email);

  void deleteUser(Long id);
}
