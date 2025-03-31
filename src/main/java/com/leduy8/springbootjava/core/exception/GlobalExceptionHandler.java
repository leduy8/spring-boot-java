package com.leduy8.springbootjava.core.exception;

import com.leduy8.springbootjava.auth.exception.InvalidCredentialException;
import com.leduy8.springbootjava.user.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(InvalidCredentialException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidCredentialException(
      InvalidCredentialException ex) {
    return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(
      MethodArgumentNotValidException ex) {
    final Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Validation Error");
    response.put("message", "Invalid input data");
    response.put(
        "fieldErrors",
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage)));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Map<String, Object>> handleMissingParameterException(
      MissingServletRequestParameterException ex) {
    return buildErrorResponse(
        HttpStatus.BAD_REQUEST, "Missing required parameter: " + ex.getParameterName());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Map<String, Object>> handleTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    return buildErrorResponse(
        HttpStatus.BAD_REQUEST, "Invalid value for parameter: " + ex.getName());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
      ConstraintViolationException ex) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation error: " + ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
  }

  private ResponseEntity<Map<String, Object>> buildErrorResponse(
      HttpStatus status, String message) {
    final Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("status", status.value());
    response.put("error", status.getReasonPhrase());
    response.put("message", message);
    return ResponseEntity.status(status).body(response);
  }
}
