package com.jkTech.document.managementApp.controller;

import com.jkTech.document.managementApp.dto.request.QARequest;
import com.jkTech.document.managementApp.dto.response.QAResponse;
import com.jkTech.document.managementApp.service.QAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/qa")
@RequiredArgsConstructor
public class QAController {

    private final QAService qaService;

    @PostMapping("/ask")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> askQuestion(@RequestBody QARequest request) {
        return ResponseEntity.ok(qaService.processQuestion(request));
    }

    @GetMapping("/documents/relevant")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getRelevantDocuments(@RequestParam String question) {
        return ResponseEntity.ok(qaService.findRelevantDocuments(question));
    }
}
