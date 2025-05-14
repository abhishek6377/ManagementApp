package com.jkTech.document.managementApp.service;

import com.jkTech.document.managementApp.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse updateUser(Long id, UserResponse userResponse);

    void deleteUser(Long id);
}
