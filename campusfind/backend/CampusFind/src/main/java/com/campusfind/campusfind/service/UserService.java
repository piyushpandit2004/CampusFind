
package com.campusfind.campusfind.service;

import com.campusfind.campusfind.dto.LoginRequestDto;
import com.campusfind.campusfind.dto.LoginResponseDto;
import com.campusfind.campusfind.dto.RegisterRequestDto;
import com.campusfind.campusfind.dto.RegisterResponseDto;
import com.campusfind.campusfind.entity.User;
import com.campusfind.campusfind.repository.UserRepository;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // User registration
    public RegisterResponseDto registerUser(RegisterRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        return new RegisterResponseDto(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }

    // User login
    public LoginResponseDto loginUser(LoginRequestDto request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponseDto(
                "Login successful!",
                user.getEmail(),
                token
        );
    }

    // Temporary admin setup for local testing
    public void makeAdmin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        user.setRole("ADMIN");
        userRepository.save(user);
    }
}
