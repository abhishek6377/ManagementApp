package com.jkTech.document.managementApp.repository;

import com.jkTech.document.managementApp.model.Document;
import com.jkTech.document.managementApp.model.User;
import com.jkTech.document.managementApp.model.enums.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Page<Document> findByUploader(User uploader, Pageable pageable);

    Page<Document> findByStatus(DocumentStatus status, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.title LIKE %:keyword% OR d.description LIKE %:keyword%")
    Page<Document> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.createdAt BETWEEN :startDate AND :endDate")
    Page<Document> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   Pageable pageable);

    @Query("SELECT d FROM Document d JOIN d.keywords k WHERE k IN :keywords")
    Page<Document> findByKeywords(@Param("keywords") List<String> keywords, Pageable pageable);

    @Query(value = "SELECT d.* FROM documents d " +
            "JOIN document_contents dc ON d.id = dc.document_id " +
            "WHERE to_tsvector('english', dc.content) @@ plainto_tsquery('english', :query)",
            nativeQuery = true)
    Page<Document> fullTextSearch(@Param("query") String query, Pageable pageable);

    Optional<Document> findByFilename(String filename);
}
