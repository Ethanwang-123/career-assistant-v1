package com.example.applicationtracker.service;

import com.example.applicationtracker.dto.AuthRequest;
import com.example.applicationtracker.dto.AuthResponse;
import com.example.applicationtracker.entity.AppUser;
import com.example.applicationtracker.repository.AppUserRepository;
import com.example.applicationtracker.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(AuthRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (appUserRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        appUserRepository.save(appUser);

        return new AuthResponse(jwtService.generateToken(email), email);
    }

    public AuthResponse login(AuthRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );
        return new AuthResponse(jwtService.generateToken(email), email);
    }
}
