package com.jkTech.document.managementApp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StorageConfig {

    @Value("${document.storage.location}")
    private String storageLocation;

    @Bean
    public Path storageLocation() {
        return Paths.get(storageLocation);
    }

}
