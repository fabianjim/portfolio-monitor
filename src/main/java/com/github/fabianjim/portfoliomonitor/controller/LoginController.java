package com.github.fabianjim.portfoliomonitor.controller;

import com.github.fabianjim.portfoliomonitor.model.User;
import com.github.fabianjim.portfoliomonitor.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        // check user info
        if (userRepository.existsByUsername(request.username)) {
            return ResponseEntity.badRequest()
                    .body(new RegisterResponse("This user already exists", null));
        }

        String hashedPassword = passwordEncoder.encode(request.password);
        User user = new User(request.username, hashedPassword);
        userRepository.save(user);

        return ResponseEntity.ok(
                new RegisterResponse("User created successfully", user.getUsername())
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Optional<User> foundUser = userRepository.findByUsername(request.username);
        
        if (foundUser.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse("Invalid username or password", null, null));
        }
        User user = foundUser.get();
        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse("Invalid username or password", null, null));
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        System.out.println("User " + user.getUsername() + " " + user.getId() + " logged in successfully.");
        return ResponseEntity.ok(
                new LoginResponse("Login successful", user.getUsername(), user.getId())
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(
                new LoginResponse("Logout successful", null, null)
        );
    }


    public static class RegisterRequest {
        public String username;
        public String password;
    }

    public static class LoginRequest {
        public String username;
        public String password;
    } 

    public record LoginResponse(String message, String username, Integer userId) {}

    public record RegisterResponse(String message, String username) {}
}
