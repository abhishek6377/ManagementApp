package com.jkTech.document.managementApp.service;

import com.jkTech.document.managementApp.dto.request.LoginRequest;
import com.jkTech.document.managementApp.dto.request.SignupRequest;
import com.jkTech.document.managementApp.dto.response.JwtResponse;
import com.jkTech.document.managementApp.dto.response.MessageResponse;
import com.jkTech.document.managementApp.model.Role;
import com.jkTech.document.managementApp.model.User;
import com.jkTech.document.managementApp.repository.RoleRepository;
import com.jkTech.document.managementApp.repository.UserRepository;
import com.jkTech.document.managementApp.security.jwt.JwtUtils;
import com.jkTech.document.managementApp.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;
    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new JwtResponse(
                accessToken,
                refreshToken,
                "Bearer",
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(java.util.stream.Collectors.toSet())
        );
    }

    @Override
    @Transactional
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        Set<Role> managedRoles = signUpRequest.getRoles().stream()
                .map(roleDto -> roleRepository.findById(roleDto.getId())
                        .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleDto.getId())))
                .collect(Collectors.toSet());
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .roles(managedRoles)
                .enabled(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationTokenExpiryDate(Instant.now().plusSeconds(86400)) // 24 hours
                .build();

        userRepository.save(user);

        return new MessageResponse("User registered successfully! Please check your email for verification.");
    }

    @Override
    public JwtResponse refreshToken(String refreshToken) {
        if (!refreshToken.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid refresh token format!");
        }

        refreshToken = refreshToken.substring(7);
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token!");
        }

        String username = jwtUtils.getUserNameFromJwtToken(refreshToken);
        UserDetailsImpl userDetails = UserDetailsImpl.build(
                userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found!"))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        String newAccessToken = jwtUtils.generateJwtToken(authentication);
        String newRefreshToken = jwtUtils.generateRefreshToken(authentication);

        return new JwtResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(java.util.stream.Collectors.toSet())
        );
    }

    @Override
    public MessageResponse logoutUser() {
        SecurityContextHolder.clearContext();
        return new MessageResponse("Logged out successfully!");
    }
}