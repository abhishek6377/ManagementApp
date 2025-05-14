package com.jkTech.document.managementApp.service;


import com.jkTech.document.managementApp.dto.response.UserResponse;
import com.jkTech.document.managementApp.model.Role;
import com.jkTech.document.managementApp.model.User;
import com.jkTech.document.managementApp.repository.RoleRepository;
import com.jkTech.document.managementApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToUserResponse)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#username")
    public UserResponse getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::mapToUserResponse)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToUserResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public UserResponse updateUser(Long id, UserResponse userResponse) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Set<Role> managedRoles = userResponse.getRoles().stream()
                .map(roleDto -> roleRepository.findById(roleDto.getId())
                        .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleDto.getId())))
                .collect(Collectors.toSet());

        user.setUsername(userResponse.getUsername());
        user.setEmail(userResponse.getEmail());
        user.setRoles(managedRoles);

        return mapToUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }
}
