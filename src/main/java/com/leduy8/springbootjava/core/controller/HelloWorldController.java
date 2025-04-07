package com.leduy8.springbootjava.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/startup")
@RequiredArgsConstructor
public class HelloWorldController {
  @GetMapping("/check-health")
  public ResponseEntity<String> checkHealth() {
    return ResponseEntity.ok("I'm okay, I'm fine");
  }
}
