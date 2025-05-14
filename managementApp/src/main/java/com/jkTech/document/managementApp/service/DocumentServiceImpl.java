package com.jkTech.document.managementApp.service;

import com.jkTech.document.managementApp.configuration.RabbitMqConfig;
import com.jkTech.document.managementApp.dto.request.DocumentUploadRequest;
import com.jkTech.document.managementApp.dto.response.DocumentResponse;
import com.jkTech.document.managementApp.model.Document;
import com.jkTech.document.managementApp.model.DocumentType;
import com.jkTech.document.managementApp.model.User;
import com.jkTech.document.managementApp.model.enums.DocumentStatus;
import com.jkTech.document.managementApp.repository.DocumentRepository;
import com.jkTech.document.managementApp.repository.DocumentTypeRepository;
import com.jkTech.document.managementApp.repository.UserRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService{

    private final DocumentRepository documentRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public DocumentResponse uploadDocument(DocumentUploadRequest request, String username) {
        User uploader = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DocumentType documentType = null;
        if (request.getDocumentTypeId() != null) {
            documentType = documentTypeRepository.findById(request.getDocumentTypeId())
                    .orElse(null);
        }

        Document document = Document.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .filename(request.getFile().getOriginalFilename())
                .fileSize(request.getFile().getSize())
                .mimeType(request.getFile().getContentType())
                .documentType(documentType)
                .uploader(uploader)
                .keywords(request.getKeywords())
                .status(DocumentStatus.PENDING)
                .build();

        
        String storedFileName = storageService.store(request.getFile());
        document.setFilePath(storedFileName);
        document = documentRepository.save(document);

//        rabbitTemplate.convertAndSend(
//                RabbitMqConfig.EXCHANGE,
//                RabbitMqConfig.DOCUMENT_ROUTING_KEY,
//                document.getId()
//        );

        return mapToResponse(document);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(Long id) {
        return documentRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponse> getAllDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponse> getDocumentsByUser(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return documentRepository.findByUploader(user, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponse> searchDocuments(String keyword, Pageable pageable) {
        return documentRepository.findByKeyword(keyword, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public DocumentResponse updateDocument(Long id, DocumentUploadRequest request) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        document.setTitle(request.getTitle());
        document.setDescription(request.getDescription());

        if (request.getDocumentTypeId() != null) {
            DocumentType documentType = documentTypeRepository.findById(request.getDocumentTypeId())
                    .orElseThrow(() -> new RuntimeException("Document type not found"));
            document.setDocumentType(documentType);
        }

        document.setKeywords(request.getKeywords());

        if (request.getFile() != null) {
            storageService.delete(document.getFilePath());
            String newFilePath = storageService.store(request.getFile());
            document.setFilePath(newFilePath);
            document.setFileSize(request.getFile().getSize());
            document.setMimeType(request.getFile().getContentType());
        }

        return mapToResponse(documentRepository.save(document));
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        storageService.delete(document.getFilePath());
        documentRepository.delete(document);
    }

    @Async
    @Override
    @Transactional
    public CompletableFuture<Void> processDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        try {
            // Update document status to processing
            document.setStatus(DocumentStatus.PROCESSING);
            documentRepository.save(document);

            // Add your document processing logic here

            // Update status to completed after processing
            document.setStatus(DocumentStatus.valueOf("COMPLETED"));
            documentRepository.save(document);
        } catch (Exception e) {
            document.setStatus(DocumentStatus.ERROR);
            documentRepository.save(document);
            throw new RuntimeException("Failed to process document", e);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        Resource resource = storageService.loadAsResource(document.getFilePath());
        try {
            return ((org.springframework.core.io.Resource) resource).getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Could not read file", e);
        }
    }

    @Override
    @Transactional
    public List<DocumentResponse> batchUploadDocuments(List<MultipartFile> files, String username) {
        List<DocumentResponse> responses = new ArrayList<>();
        User uploader = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        for (MultipartFile file : files) {
            DocumentUploadRequest request = DocumentUploadRequest.builder()
                    .title(file.getOriginalFilename())
                    .description("Batch uploaded document")
                    .file(file)
                    .keywords(Set.of())
                    .build();

            try {
                responses.add(uploadDocument(request, username));
            } catch (Exception e) {
                // Log error and continue with next file
                // You might want to add error handling strategy here
                continue;
            }
        }

        return responses;
    }

    private DocumentResponse mapToResponse(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getDescription(),
                document.getFilename(),
                document.getFileSize(),
                document.getMimeType(),
                document.getDocumentType().getName(),
                document.getUploader().getUsername(),
                document.getKeywords(),
                document.getStatus(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}
