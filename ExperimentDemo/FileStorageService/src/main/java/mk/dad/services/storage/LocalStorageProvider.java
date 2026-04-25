package mk.dad.services.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
@ConditionalOnProperty(name = "file-storage.provider", havingValue = "local")
public class LocalStorageProvider implements StorageProvider {

    @Value("${file-storage.local.bucket-path}")
    private String bucketPath;

    @Override
    public void uploadFile(String bucketName, String fileKey, InputStream inputStream, String contentType) {
        try {
            Path directoryPath = Paths.get(bucketPath, bucketName);
            
            // Create directories if they don't exist
            Files.createDirectories(directoryPath);
            
            Path filePath = directoryPath.resolve(fileKey);
            
            // Write file to disk
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            
            log.info("File uploaded successfully: {} to bucket: {}", fileKey, bucketName);
        } catch (IOException e) {
            log.error("Error uploading file: {}", fileKey, e);
            throw new RuntimeException("Error uploading file: " + e.getMessage(), e);
        }
    }

    @Override
    public InputStream downloadFile(String bucketName, String fileKey) {
        try {
            Path filePath = Paths.get(bucketPath, bucketName, fileKey);
            
            if (!Files.exists(filePath)) {
                throw new RuntimeException("File not found: " + fileKey);
            }
            
            log.info("File downloaded: {} from bucket: {}", fileKey, bucketName);
            return new FileInputStream(filePath.toFile());
        } catch (IOException e) {
            log.error("Error downloading file: {}", fileKey, e);
            throw new RuntimeException("Error downloading file: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String fileKey) {
        try {
            Path filePath = Paths.get(bucketPath, bucketName, fileKey);
            Files.deleteIfExists(filePath);
            log.info("File deleted: {} from bucket: {}", fileKey, bucketName);
        } catch (IOException e) {
            log.error("Error deleting file: {}", fileKey, e);
            throw new RuntimeException("Error deleting file: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean fileExists(String bucketName, String fileKey) {
        Path filePath = Paths.get(bucketPath, bucketName, fileKey);
        return Files.exists(filePath);
    }

    @Override
    public String getStorageType() {
        return "LOCAL";
    }
}