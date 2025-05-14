package com.jkTech.document.managementApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@Builder
public class DocumentUploadRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private MultipartFile file;

    private Long documentTypeId;

    private Set<String> keywords;
}
