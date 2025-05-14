package com.jkTech.document.managementApp.repository;

import com.jkTech.document.managementApp.model.DocumentContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentContentRepository extends JpaRepository<DocumentContent, Long> {

    @Query(value = "SELECT dc.* FROM document_contents dc " +
            "WHERE to_tsvector('english', dc.content) @@ plainto_tsquery('english', :query) " +
            "ORDER BY ts_rank(to_tsvector('english', dc.content), plainto_tsquery('english', :query)) DESC",
            nativeQuery = true)
    List<DocumentContent> searchByContent(@Param("query") String query);

    @Modifying
    @Query(value = "UPDATE document_contents SET search_vector = " +
            "to_tsvector('english', content) WHERE document_id = :documentId",
            nativeQuery = true)
    void updateSearchVector(@Param("documentId") Long documentId);

    @Query(value = "SELECT ts_headline('english', content, plainto_tsquery('english', :query), 'MaxFragments=3') " +
            "FROM document_contents WHERE document_id = :documentId",
            nativeQuery = true)
    String generateHighlights(@Param("documentId") Long documentId, @Param("query") String query);
}
