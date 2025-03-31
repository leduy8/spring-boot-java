package com.leduy8.springbootjava.user.dto;

import com.leduy8.springbootjava.core.dto.BaseResponseDTO;
import com.leduy8.springbootjava.user.model.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO extends BaseResponseDTO<User> {
  private Long id;
  private String name;
  private String email;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Override
  public void mapFromEntity(User user) {
    this.id = user.getId();
    this.name = user.getName();
    this.email = user.getEmail();
    this.createdAt = user.getCreatedAt();
    this.updatedAt = user.getUpdatedAt();
  }
}
