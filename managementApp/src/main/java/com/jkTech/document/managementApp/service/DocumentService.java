package com.jkTech.document.managementApp.service;

import com.jkTech.document.managementApp.dto.request.DocumentUploadRequest;
import com.jkTech.document.managementApp.dto.response.DocumentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DocumentService {

    DocumentResponse uploadDocument(DocumentUploadRequest documentUploadRequest, String username);

    DocumentResponse getDocumentById(Long id);

    Page<DocumentResponse> getAllDocuments(Pageable pageable);

    Page<DocumentResponse> getDocumentsByUser(String username, Pageable pageable);

    Page<DocumentResponse> searchDocuments(String keyword, Pageable pageable);

    DocumentResponse updateDocument(Long id, DocumentUploadRequest documentUploadRequest);

    void deleteDocument(Long id);

    @Async
    CompletableFuture<Void> processDocument(Long documentId);

    byte[] downloadDocument(Long id);

    List<DocumentResponse> batchUploadDocuments(List<MultipartFile> files, String username);
}
