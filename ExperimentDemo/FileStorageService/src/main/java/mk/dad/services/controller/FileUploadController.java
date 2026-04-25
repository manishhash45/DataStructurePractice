package mk.dad.services.controller;

import lombok.extern.slf4j.Slf4j;
import mk.dad.services.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * API to upload file
     * POST /api/files/upload?bucketName=default
     * @param file MultipartFile to upload
     * @param bucketName bucket/folder name (optional, uses default if not provided)
     * @return ResponseEntity with file key
     */
    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "bucketName", required = false) String bucketName) {

        try {
            if (bucketName == null || bucketName.isEmpty()) {
                bucketName = fileStorageService.getDefaultBucket();
            }

            String fileKey = fileStorageService.uploadFile(file, bucketName);

            log.info("File uploaded successfully: {} to bucket: {}", fileKey, bucketName);

            return ResponseEntity.ok(new UploadResponse(
                    "File uploaded successfully",
                    fileKey,
                    file.getOriginalFilename(),
                    bucketName,
                    file.getSize()
            ));
        } catch (Exception e) {
            log.error("Error uploading file", e);
            return ResponseEntity.badRequest().body(new UploadResponse(
                    "Error uploading file: " + e.getMessage(),
                    null,
                    file.getOriginalFilename(),
                    bucketName,
                    0
            ));
        }
    }

    /**
     * API to download file
     * GET /api/files/download/{fileKey}?bucketName=default
     * @param fileKey unique file identifier
     * @param bucketName bucket/folder name (optional, uses default if not provided)
     * @return ResponseEntity with file content
     */
    @GetMapping("/download/{fileKey}")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable String fileKey,
            @RequestParam(value = "bucketName", required = false) String bucketName) {

        try {
            if (bucketName == null || bucketName.isEmpty()) {
                bucketName = fileStorageService.getDefaultBucket();
            }

            byte[] fileContent = fileStorageService.downloadFile(bucketName, fileKey);

            log.info("File downloaded: {} from bucket: {}", fileKey, bucketName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileKey + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileContent);
        } catch (Exception e) {
            log.error("Error downloading file: {}", fileKey, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * API to delete file
     * DELETE /api/files/delete/{fileKey}?bucketName=default
     * @param fileKey unique file identifier
     * @param bucketName bucket/folder name (optional, uses default if not provided)
     * @return ResponseEntity with deletion status
     */
    @DeleteMapping("/delete/{fileKey}")
    public ResponseEntity<DeleteResponse> deleteFile(
            @PathVariable String fileKey,
            @RequestParam(value = "bucketName", required = false) String bucketName) {

        try {
            if (bucketName == null || bucketName.isEmpty()) {
                bucketName = fileStorageService.getDefaultBucket();
            }

            fileStorageService.deleteFile(bucketName, fileKey);

            log.info("File deleted: {} from bucket: {}", fileKey, bucketName);

            return ResponseEntity.ok(new DeleteResponse(
                    "File deleted successfully",
                    fileKey,
                    bucketName
            ));
        } catch (Exception e) {
            log.error("Error deleting file: {}", fileKey, e);
            return ResponseEntity.badRequest().body(new DeleteResponse(
                    "Error deleting file: " + e.getMessage(),
                    fileKey,
                    bucketName
            ));
        }
    }

    /**
     * API to check if file exists
     * GET /api/files/exists/{fileKey}?bucketName=default
     * @param fileKey unique file identifier
     * @param bucketName bucket/folder name (optional, uses default if not provided)
     * @return ResponseEntity with existence status
     */
    @GetMapping("/exists/{fileKey}")
    public ResponseEntity<ExistsResponse> checkFileExists(
            @PathVariable String fileKey,
            @RequestParam(value = "bucketName", required = false) String bucketName) {

        try {
            if (bucketName == null || bucketName.isEmpty()) {
                bucketName = fileStorageService.getDefaultBucket();
            }

            boolean exists = fileStorageService.fileExists(bucketName, fileKey);

            return ResponseEntity.ok(new ExistsResponse(
                    fileKey,
                    bucketName,
                    exists
            ));
        } catch (Exception e) {
            log.error("Error checking file existence: {}", fileKey, e);
            return ResponseEntity.badRequest().body(new ExistsResponse(
                    fileKey,
                    bucketName,
                    false
            ));
        }
    }

    // Response DTOs
    public static class UploadResponse {
        public String message;
        public String fileKey;
        public String originalFileName;
        public String bucketName;
        public long fileSize;

        public UploadResponse(String message, String fileKey, String originalFileName, String bucketName, long fileSize) {
            this.message = message;
            this.fileKey = fileKey;
            this.originalFileName = originalFileName;
            this.bucketName = bucketName;
            this.fileSize = fileSize;
        }
    }

    public static class DeleteResponse {
        public String message;
        public String fileKey;
        public String bucketName;

        public DeleteResponse(String message, String fileKey, String bucketName) {
            this.message = message;
            this.fileKey = fileKey;
            this.bucketName = bucketName;
        }
    }

    public static class ExistsResponse {
        public String fileKey;
        public String bucketName;
        public boolean exists;

        public ExistsResponse(String fileKey, String bucketName, boolean exists) {
            this.fileKey = fileKey;
            this.bucketName = bucketName;
            this.exists = exists;
        }
    }
}