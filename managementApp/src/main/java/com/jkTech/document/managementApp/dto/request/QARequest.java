package com.jkTech.document.managementApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QARequest {

    @NotBlank
    private String question;

    private Long documentId;
}
