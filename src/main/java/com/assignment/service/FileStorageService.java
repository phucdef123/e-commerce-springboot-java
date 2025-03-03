package com.assignment.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/"; // Save outside 'static/'

    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty!");
        }

        try {
            // Ensure the directory exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Resolve file path
            Path filePath = uploadPath.resolve(file.getOriginalFilename());

            // 🔥 FIX: Delete existing file before writing
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            // 🔥 FIX: Use try-with-resources to auto-close input stream
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/" + file.getOriginalFilename(); // Return relative path for DB storage
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể lưu file! Error: " + e.getMessage(), e);
        }
    }
}
