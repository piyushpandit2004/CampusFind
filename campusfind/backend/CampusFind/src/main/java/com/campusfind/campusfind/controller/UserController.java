
package com.campusfind.campusfind.controller;

import com.campusfind.campusfind.dto.LoginRequestDto;
import com.campusfind.campusfind.dto.LoginResponseDto;
import com.campusfind.campusfind.dto.RegisterRequestDto;
import com.campusfind.campusfind.dto.RegisterResponseDto;
import com.campusfind.campusfind.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> registerUser(
            @Valid @RequestBody RegisterRequestDto request) {

        RegisterResponseDto response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(
            @Valid @RequestBody LoginRequestDto request) {

        LoginResponseDto response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }

}
