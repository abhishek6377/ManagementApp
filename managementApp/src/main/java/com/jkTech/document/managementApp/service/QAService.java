package com.jkTech.document.managementApp.service;

import com.jkTech.document.managementApp.dto.request.QARequest;
import com.jkTech.document.managementApp.dto.response.QAResponse;
import com.jkTech.document.managementApp.dto.response.UserResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface QAService {

	CompletableFuture<QAResponse> processQuestion(QARequest qaRequest);

    List<QAResponse.DocumentSnippet> findRelevantDocuments(String question);
}
