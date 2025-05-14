package com.jkTech.document.managementApp.controller;

import com.jkTech.document.managementApp.dto.request.DocumentUploadRequest;
import com.jkTech.document.managementApp.dto.response.DocumentResponse;
import com.jkTech.document.managementApp.service.DocumentService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "documentTypeId", required = false) Long documentTypeId,
            @RequestParam(value = "keywords", required = false) Set<String> keywords,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        var request = DocumentUploadRequest.builder()
                .file(file)
                .title(title)
                .description(description)
                .documentTypeId(documentTypeId)
                .keywords(keywords)
                .build();
                
        return ResponseEntity.ok(documentService.uploadDocument(request, userDetails.getUsername()));
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<DocumentResponse>> batchUploadDocuments(
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(documentService.batchUploadDocuments(files, userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocument(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @GetMapping
    public ResponseEntity<Page<DocumentResponse>> getAllDocuments(Pageable pageable) {
        return ResponseEntity.ok(documentService.getAllDocuments(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DocumentResponse>> searchDocuments(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(documentService.searchDocuments(keyword, pageable));
    }

    @GetMapping("/user")
    public ResponseEntity<Page<DocumentResponse>> getUserDocuments(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(documentService.getDocumentsByUser(userDetails.getUsername(), pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        DocumentResponse document = documentService.getDocumentById(id);
        byte[] content = documentService.downloadDocument(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getFilename() + "\"")
                .body(content);
    }
}