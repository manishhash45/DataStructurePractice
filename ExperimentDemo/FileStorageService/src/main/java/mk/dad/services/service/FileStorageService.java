package mk.dad.services.service;

import lombok.extern.slf4j.Slf4j;
import mk.dad.services.storage.StorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    @Autowired
    private StorageProvider storageProvider;

    @Value("${file-storage.local.bucket-path:}")
    private String localBucketPath;

    @Value("${file-storage.aws.bucket-name:}")
    private String awsBucketName;

    /**
     * Upload file to storage
     * @param file MultipartFile to upload
     * @param bucketName bucket/folder name
     * @return unique file identifier
     */
    public String uploadFile(MultipartFile file, String bucketName) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            // Generate unique file key
            String fileKey = generateFileKey(file.getOriginalFilename());

            // Upload file
            storageProvider.uploadFile(bucketName, fileKey, file.getInputStream(), file.getContentType());

            log.info("File uploaded successfully with key: {}", fileKey);
            return fileKey;
        } catch (IOException e) {
            log.error("Error uploading file", e);
            throw new RuntimeException("Error uploading file: " + e.getMessage(), e);
        }
    }

    /**
     * Download file from storage
     * @param bucketName bucket/folder name
     * @param fileKey unique file identifier
     * @return file content as byte array
     */
    public byte[] downloadFile(String bucketName, String fileKey) {
        try {
            if (!storageProvider.fileExists(bucketName, fileKey)) {
                throw new RuntimeException("File not found: " + fileKey);
            }

            return storageProvider.downloadFile(bucketName, fileKey).readAllBytes();
        } catch (IOException e) {
            log.error("Error downloading file", e);
            throw new RuntimeException("Error downloading file: " + e.getMessage(), e);
        }
    }

    /**
     * Delete file from storage
     * @param bucketName bucket/folder name
     * @param fileKey unique file identifier
     */
    public void deleteFile(String bucketName, String fileKey) {
        storageProvider.deleteFile(bucketName, fileKey);
        log.info("File deleted: {}", fileKey);
    }

    /**
     * Check if file exists
     * @param bucketName bucket/folder name
     * @param fileKey unique file identifier
     * @return true if file exists
     */
    public boolean fileExists(String bucketName, String fileKey) {
        return storageProvider.fileExists(bucketName, fileKey);
    }

    /**
     * Get default bucket name
     * @return default bucket name based on provider
     */
    public String getDefaultBucket() {
        if ("AWS".equals(storageProvider.getStorageType())) {
            return awsBucketName;
        }
        return "default";
    }

    /**
     * Generate unique file key
     * @param originalFilename original file name
     * @return unique file key
     */
    private String generateFileKey(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        return uuid + extension;
    }
}