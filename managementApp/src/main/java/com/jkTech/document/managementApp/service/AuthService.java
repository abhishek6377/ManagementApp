package com.jkTech.document.managementApp.service;

import com.jkTech.document.managementApp.dto.request.LoginRequest;
import com.jkTech.document.managementApp.dto.request.RegisterRequest;
import com.jkTech.document.managementApp.dto.request.SignupRequest;
import com.jkTech.document.managementApp.dto.response.JwtResponse;
import com.jkTech.document.managementApp.dto.response.MessageResponse;

public interface AuthService {


    JwtResponse authenticateUser(LoginRequest loginRequest);

    MessageResponse registerUser(SignupRequest signupRequest);

    JwtResponse refreshToken(String refreshToken);

    MessageResponse logoutUser();
}



