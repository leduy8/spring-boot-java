package com.leduy8.springbootjava.core.config;

import com.leduy8.springbootjava.core.utils.JwtUtil;
import com.leduy8.springbootjava.user.model.User;
import com.leduy8.springbootjava.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }

    final String token = authHeader.substring(7);
    final String email = jwtUtil.extractEmail(token);
    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      final User user = userRepository.findByEmail(email).orElse(null);
      if (user != null && jwtUtil.validateToken(token, user.getEmail())) {
        final UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(user, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }
    chain.doFilter(request, response);
  }
}
