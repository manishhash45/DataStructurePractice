package mk.dad.services.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

@Slf4j
@Component
@ConditionalOnProperty(name = "file-storage.provider", havingValue = "aws")
public class AwsStorageProvider implements StorageProvider {

    @Value("${file-storage.aws.region}")
    private String region;

    @Value("${file-storage.aws.access-key}")
    private String accessKey;

    @Value("${file-storage.aws.secret-key}")
    private String secretKey;

    private S3Client s3Client;

    private synchronized S3Client getS3Client() {
        if (s3Client == null) {
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
            s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                    .build();
        }
        return s3Client;
    }

    @Override
    public void uploadFile(String bucketName, String fileKey, InputStream inputStream, String contentType) {
        try {
            byte[] fileContent = inputStream.readAllBytes();
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(contentType)
                    .build();
            
            getS3Client().putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(fileContent));
            
            log.info("File uploaded to AWS S3: {} in bucket: {}", fileKey, bucketName);
        } catch (Exception e) {
            log.error("Error uploading file to AWS S3: {}", fileKey, e);
            throw new RuntimeException("Error uploading file to AWS S3: " + e.getMessage(), e);
        }
    }

    @Override
    public InputStream downloadFile(String bucketName, String fileKey) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();
            
            log.info("File downloaded from AWS S3: {} from bucket: {}", fileKey, bucketName);
            return getS3Client().getObject(getObjectRequest);
        } catch (Exception e) {
            log.error("Error downloading file from AWS S3: {}", fileKey, e);
            throw new RuntimeException("Error downloading file from AWS S3: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String fileKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();
            
            getS3Client().deleteObject(deleteObjectRequest);
            log.info("File deleted from AWS S3: {} from bucket: {}", fileKey, bucketName);
        } catch (Exception e) {
            log.error("Error deleting file from AWS S3: {}", fileKey, e);
            throw new RuntimeException("Error deleting file from AWS S3: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean fileExists(String bucketName, String fileKey) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();
            
            getS3Client().headObject(headObjectRequest);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getStorageType() {
        return "AWS";
    }
}