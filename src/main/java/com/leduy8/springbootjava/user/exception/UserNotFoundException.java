package com.leduy8.springbootjava.user.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(Long id) {
    super("User with ID " + id + " not found or has been deleted.");
  }
}
