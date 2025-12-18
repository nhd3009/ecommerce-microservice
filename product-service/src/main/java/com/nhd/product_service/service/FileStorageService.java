package com.nhd.product_service.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileStorageService {
    private final Path root = Paths.get("uploads");

    public String saveFile(MultipartFile file) {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = root.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    public List<String> saveListFiles(List<MultipartFile> files) {
        return files.stream()
                .map(this::saveFile)
                .collect(Collectors.toList());
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty())
            return;

        try {
            String filename = fileUrl.replace("/uploads/", "");
            Path path = Paths.get("uploads").resolve(filename);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete file: " + fileUrl, e);
        }
    }
}
