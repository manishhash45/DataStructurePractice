package mk.dad.services.storage;

import java.io.InputStream;

public interface StorageProvider {
    /**
     * Upload file to storage
     * @param bucketName bucket or folder name
     * @param fileKey unique file identifier
     * @param inputStream file content
     * @param contentType MIME type
     */
    void uploadFile(String bucketName, String fileKey, InputStream inputStream, String contentType);

    /**
     * Download file from storage
     * @param bucketName bucket or folder name
     * @param fileKey unique file identifier
     * @return file content as InputStream
     */
    InputStream downloadFile(String bucketName, String fileKey);

    /**
     * Delete file from storage
     * @param bucketName bucket or folder name
     * @param fileKey unique file identifier
     */
    void deleteFile(String bucketName, String fileKey);

    /**
     * Check if file exists
     * @param bucketName bucket or folder name
     * @param fileKey unique file identifier
     * @return true if file exists
     */
    boolean fileExists(String bucketName, String fileKey);

    /**
     * Get storage type
     * @return storage type (local, aws, etc.)
     */
    String getStorageType();
}