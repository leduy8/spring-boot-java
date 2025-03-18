package com.leduy8.springbootjava.auth.exception;

public class InvalidCredentialException extends RuntimeException {
    public InvalidCredentialException() {
        super("Invalid credentials, check your email or password.");
    }
}

