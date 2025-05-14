package com.jkTech.document.managementApp.controller;

import com.jkTech.document.managementApp.service.StorageService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StorageController {

    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(storageService.store(file));
    }

    @GetMapping("/files")
    public ResponseEntity<List<String>> listFiles() {
        List<String> files = storageService.loadAll()
                .map(Path::toString)
                .collect(Collectors.toList());
        return ResponseEntity.ok(files);
    }

    @GetMapping("/files/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok(file);
    }

    @DeleteMapping("/files/{filename}")
    public ResponseEntity<Void> deleteFile(@PathVariable String filename) {
        storageService.delete(filename);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/files")
    public ResponseEntity<Void> deleteAllFiles() {
        storageService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
