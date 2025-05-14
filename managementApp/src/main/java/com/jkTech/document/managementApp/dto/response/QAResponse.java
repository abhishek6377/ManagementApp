package com.jkTech.document.managementApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QAResponse {

    private String question;
    private String answer;
    private List<DocumentSnippet> relevantSnippets;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DocumentSnippet {
        private Long documentId;
        private String documentTitle;
        private String snippet;
        private Double relevanceScore;
    }
}
