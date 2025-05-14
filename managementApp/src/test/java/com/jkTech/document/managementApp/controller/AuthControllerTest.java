package com.jkTech.document.managementApp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jkTech.document.managementApp.dto.request.LoginRequest;
import com.jkTech.document.managementApp.dto.request.SignupRequest;
import com.jkTech.document.managementApp.dto.response.JwtResponse;
import com.jkTech.document.managementApp.dto.response.MessageResponse;
import com.jkTech.document.managementApp.security.jwt.JwtUtils;
import com.jkTech.document.managementApp.security.services.UserDetailsServiceImpl;
import com.jkTech.document.managementApp.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import({AuthControllerTest.TestConfig.class, AuthController.class})
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class AuthControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/v1/auth/**").permitAll()
                            .anyRequest().authenticated())
                    .cors(cors -> cors.disable());
            return http.build();
        }

        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            return (web) -> web.ignoring().requestMatchers("/api/v1/auth/**");
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager() {
            return authentication -> new UsernamePasswordAuthenticationToken(
                    "testuser",
                    "password",
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private JwtResponse jwtResponse;

    @BeforeEach
    void setUp() {
        // Setup login request
        loginRequest = new LoginRequest("testuser", "password123");

        // Setup signup request
        signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("new@user.com");
        signupRequest.setPassword("password123");

        // Setup JWT response
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        jwtResponse = new JwtResponse(
                "test.access.token",
                "test.refresh.token",
                "Bearer",
                1L,
                "testuser",
                "test@example.com",
                roles
        );
    }

    @Test
    void authenticateUser_Success() throws Exception {
        // Given
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        JwtResponse mockResponse = new JwtResponse(
                "test.access.token",
                "test.refresh.token",
                "Bearer",
                1L,
                "testuser",
                "test@example.com",
                roles
        );

        when(authService.authenticateUser(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print()) // Keep this to see the actual response
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.accessToken").value("test.access.token"))
                .andExpect(jsonPath("$.refreshToken").value("test.refresh.token"))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }
    @Test
    void registerUser_Success() throws Exception {
        MessageResponse messageResponse = new MessageResponse(
                "User registered successfully! Please check your email for verification."
        );
        when(authService.registerUser(any(SignupRequest.class)))
                .thenReturn(messageResponse);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("User registered successfully! Please check your email for verification."));
    }

    @Test
    void refreshToken_Success() throws Exception {
        when(authService.refreshToken(any(String.class)))
                .thenReturn(jwtResponse);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .header("Authorization", "Bearer refresh.token.here"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test.access.token"))
                .andExpect(jsonPath("$.refreshToken").value("test.refresh.token"));
    }

    @Test
    void logoutUser_Success() throws Exception {
        MessageResponse messageResponse = new MessageResponse("Logged out successfully!");
        when(authService.logoutUser()).thenReturn(messageResponse);

        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully!"));
    }

    @Test
    void authenticateUser_InvalidRequest() throws Exception {
        LoginRequest invalidRequest = new LoginRequest();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_InvalidRequest() throws Exception {
        SignupRequest invalidRequest = new SignupRequest();

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshToken_MissingHeader() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andExpect(status().isBadRequest());
    }
}