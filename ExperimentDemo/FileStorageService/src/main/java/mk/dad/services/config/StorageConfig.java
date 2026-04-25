package mk.dad.services.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class StorageConfig {

    @Value("${file-storage.provider}")
    private String provider;

    @Value("${file-storage.local.bucket-path:}")
    private String localBucketPath;

    @Value("${file-storage.aws.bucket-name:}")
    private String awsBucketName;

    public void initializeStorage() {
        log.info("===========================================");
        log.info("File Storage Service Initialized");
        log.info("Storage Provider: {}", provider.toUpperCase());
        if ("local".equalsIgnoreCase(provider)) {
            log.info("Local Bucket Path: {}", localBucketPath);
        } else {
            log.info("AWS S3 Bucket: {}", awsBucketName);
        }
        log.info("===========================================");
    }
}