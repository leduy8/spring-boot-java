package com.leduy8.springbootjava.user.model;

import com.leduy8.springbootjava.core.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "users",
    uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class User extends BaseEntity {

  @Email
  @NotBlank
  @Column(nullable = false, unique = true)
  private String email;

  @NotBlank
  @Size(min = 4, max = 255)
  @Column(nullable = false)
  private String password;

  @NotBlank
  @Size(min = 2, max = 50)
  private String name;
}
