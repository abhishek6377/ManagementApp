package com.jkTech.document.managementApp.repository;

import com.jkTech.document.managementApp.model.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {

    Optional<DocumentType> findByName(String name);

    boolean existsByName(String name);
}
