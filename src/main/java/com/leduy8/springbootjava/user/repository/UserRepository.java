package com.leduy8.springbootjava.user.repository;

import com.leduy8.springbootjava.user.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  Optional<User> findByIdAndIsDeletedFalse(Long id);

  Page<User> findAllByIsDeletedFalse(Pageable pageable);

  Page<User> findAll(Pageable pageable);
}
