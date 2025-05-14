package com.jkTech.document.managementApp.service;

import com.jkTech.document.managementApp.configuration.RabbitMqConfig;
import com.jkTech.document.managementApp.dto.request.QARequest;
import com.jkTech.document.managementApp.dto.response.QAResponse;
import com.jkTech.document.managementApp.dto.response.UserResponse;
import com.jkTech.document.managementApp.model.Document;
import com.jkTech.document.managementApp.model.User;
import com.jkTech.document.managementApp.repository.DocumentRepository;
import com.jkTech.document.managementApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QAServiceImpl implements QAService {

    private final RabbitTemplate rabbitTemplate;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    @Async
    @Override
    @Transactional
    public CompletableFuture<QAResponse> processQuestion(QARequest qaRequest) {
        // Find relevant documents first
        List<QAResponse.DocumentSnippet> relevantSnippets = findRelevantDocuments(qaRequest.getQuestion());

        // Send to RabbitMQ for async processing
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.QA_ROUTING_KEY,
                qaRequest
        );

        // Return a placeholder response wrapped in a CompletableFuture
        QAResponse response = QAResponse.builder()
                .question(qaRequest.getQuestion())
                .answer("Processing your question...") // Placeholder
                .relevantSnippets(relevantSnippets)
                .build();

        return CompletableFuture.completedFuture(response);
    }


    @Cacheable(value = "documentSnippets", key = "#question")
    @Override
    @Transactional(readOnly = true)
    public List<QAResponse.DocumentSnippet> findRelevantDocuments(String question) {
        return documentRepository.fullTextSearch(question, PageRequest.of(0, 5))
                .getContent()
                .stream()
                .map(this::mapToDocumentSnippet)
                .collect(Collectors.toList());
    }

    private QAResponse.DocumentSnippet mapToDocumentSnippet(Document document) {
        return QAResponse.DocumentSnippet.builder()
                .documentId(document.getId())
                .documentTitle(document.getTitle())
                .snippet(generateSnippet(document.getDescription()))
                .build();
    }

    private String generateSnippet(String text) {
        if (text == null || text.length() <= 200) {
            return text;
        }
        return text.substring(0, 200) + "...";
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles()) // Assuming User entity has getRoles() method
                .build();
    }
}
