package com.jkTech.document.managementApp.repository;

import com.jkTech.document.managementApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationToken(String token);

    Optional<User> findByResetPasswordToken(String token);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.lastLogin = ?2 WHERE u.id = ?1")
    void updateLastLogin(Long userId, Instant lastLogin);

    @Modifying
    @Query("UPDATE User u SET u.enabled = true WHERE u.id = ?1")
    void enableUser(Long userId);
}
