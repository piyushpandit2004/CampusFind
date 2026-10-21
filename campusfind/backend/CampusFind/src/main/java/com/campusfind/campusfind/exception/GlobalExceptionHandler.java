
package com.campusfind.campusfind.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation errors: @NotBlank, @NotNull, etc.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    // Lost item ya doosra resource nahi mila
    @ExceptionHandler(LostItemNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(
            LostItemNotFoundException exception) {

        Map<String, String> body = new HashMap<>();
        body.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // User ko resource access karne ki permission nahi hai
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(
            AccessDeniedException exception) {

        Map<String, String> body = new HashMap<>();
        body.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // Duplicate ya invalid request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
            IllegalArgumentException exception) {

        Map<String, String> body = new HashMap<>();
        body.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
