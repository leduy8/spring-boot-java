package com.leduy8.springbootjava.auth.service;

import com.leduy8.springbootjava.core.utils.JwtUtil;
import com.leduy8.springbootjava.user.model.User;
import com.leduy8.springbootjava.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public String authenticate(String email, String password) {
    final Optional<User> user = userRepository.findByEmail(email);

    if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
      throw new RuntimeException("Invalid credentials");
    }

    return jwtUtil.generateToken(email);
  }
}
