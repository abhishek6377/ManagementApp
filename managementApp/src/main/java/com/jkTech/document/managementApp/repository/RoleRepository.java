package com.jkTech.document.managementApp.repository;

import com.jkTech.document.managementApp.model.Role;

import com.jkTech.document.managementApp.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleType name);
}
