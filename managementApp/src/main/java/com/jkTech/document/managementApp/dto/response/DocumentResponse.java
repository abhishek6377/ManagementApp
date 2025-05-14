package com.jkTech.document.managementApp.dto.response;

import com.jkTech.document.managementApp.model.enums.DocumentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    private Long id;
    private String title;
    private String description;
    private String filename;
    private Long fileSize;
    private String mimeType;
    private String documentType;
    private String uploaderUsername;
    private Set<String> keywords;
    private DocumentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
