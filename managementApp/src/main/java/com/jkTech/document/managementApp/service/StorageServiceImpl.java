package com.jkTech.document.managementApp.service;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService{

	@Value("${document.storage.location}")
    private final Path storageLocation;

    @Override
    public void init() {
        try {
            Files.createDirectories(storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file");
            }

            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path destinationFile = this.storageLocation.resolve(filename)
                    .normalize()
                    .toAbsolutePath();

            if (!destinationFile.getParent().equals(this.storageLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory");
            }

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.storageLocation, 1)
                    .filter(path -> !path.equals(this.storageLocation))
                    .map(this.storageLocation::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String filename) {
        return storageLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = (Resource) new UrlResource(file.toUri());
            if (((org.springframework.core.io.Resource) resource).exists() || ((org.springframework.core.io.Resource) resource).isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(storageLocation.toFile());
    }

    @Override
    public void delete(String filename) {
        try {
            Path file = load(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + filename, e);
        }
    }
}
