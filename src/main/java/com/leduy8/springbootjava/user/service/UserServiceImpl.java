package com.leduy8.springbootjava.user.service;

import com.leduy8.springbootjava.core.dto.PaginatedResponseDTO;
import com.leduy8.springbootjava.user.dto.UserCreateRequestDTO;
import com.leduy8.springbootjava.user.dto.UserResponseDTO;
import com.leduy8.springbootjava.user.dto.UserUpdateRequestDTO;
import com.leduy8.springbootjava.user.exception.UserNotFoundException;
import com.leduy8.springbootjava.user.model.User;
import com.leduy8.springbootjava.user.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDTO createUser(UserCreateRequestDTO request) {
        User user = User
                .builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);
        return UserResponseDTO.of(user, UserResponseDTO.class);
    }

    @Override
    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findByIdAndIsDeletedFalse(id)
                .map((user) -> UserResponseDTO.of(user, UserResponseDTO.class))
                .or(() -> { throw new UserNotFoundException(id); });
    }

    @Override
    public PaginatedResponseDTO<UserResponseDTO> getUsers(int page, int size, String orderBy) {
        Sort.Direction direction = orderBy.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = orderBy.replace("-", "");
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<User> userPage = userRepository.findAllByIsDeletedFalse(pageable);

        return PaginatedResponseDTO.of(userPage, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO request) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setName(request.name());
            return UserResponseDTO.of(userRepository.save(existingUser), UserResponseDTO.class);
        }).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.softDelete();
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return (UserDetails) User.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
