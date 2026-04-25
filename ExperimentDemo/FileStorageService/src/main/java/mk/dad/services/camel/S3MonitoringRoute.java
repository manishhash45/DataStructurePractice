package mk.dad.services.camel;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class S3MonitoringRoute extends RouteBuilder {

    @Value("${file-storage.provider}")
    private String storageProvider;

    @Value("${file-storage.local.bucket-path:}")
    private String localBucketPath;

    @Value("${file-storage.aws.bucket-name:}")
    private String awsBucketName;

    @Value("${file-storage.aws.region:us-east-1}")
    private String awsRegion;

    @Autowired
    private CsvFileProcessor csvFileProcessor;

    @Override
    public void configure() throws Exception {
        // Monitor local file system if using local provider
        if ("local".equalsIgnoreCase(storageProvider)) {
            configureLocalMonitoring();
        } else if ("aws".equalsIgnoreCase(storageProvider)) {
            configureAwsMonitoring();
        }
    }

    private void configureLocalMonitoring() {
        log.info("Configuring local file system monitoring on: {}", localBucketPath);

        from("file:" + localBucketPath + "/default?noop=true&readLock=none&delay=5000")
                .routeId("local-s3-monitoring")
                .log("File received in local bucket: ${header.CamelFileName}")
                .choice()
                    .when(simple("${header.CamelFileName} ends with '.csv'"))
                        .log("CSV file detected, processing...")
                        .bean(csvFileProcessor, "processCsvFile")
                    .otherwise()
                        .log("Non-CSV file detected, forwarding to default processor")
                        .bean(new S3FileProcessor(), "processFile")
                .end()
                .end();
    }

    private void configureAwsMonitoring() {
        log.info("Configuring AWS S3 monitoring on bucket: {}", awsBucketName);

        from("aws2-s3://" + awsBucketName + "?region=" + awsRegion + "&delay=5000")
                .routeId("aws-s3-monitoring")
                .log("File received in AWS S3 bucket: ${header.CamelAwsS3Key}")
                .choice()
                    .when(simple("${header.CamelAwsS3Key} ends with '.csv'"))
                        .log("CSV file detected, processing...")
                        .bean(csvFileProcessor, "processCsvFile")
                    .otherwise()
                        .log("Non-CSV file detected, forwarding to default processor")
                        .bean(new S3FileProcessor(), "processFile")
                .end()
                .end();
    }

    @Slf4j
    public static class S3FileProcessor {
        public void processFile(org.apache.camel.Exchange exchange) {
            String fileName = (String) exchange.getIn().getHeader("CamelFileName");
            if (fileName == null) {
                fileName = (String) exchange.getIn().getHeader("CamelAwsS3Key");
            }

            byte[] fileContent = exchange.getIn().getBody(byte[].class);
            long fileSize = fileContent != null ? fileContent.length : 0;

            log.info("========================================");
            log.info("📁 FILE RECEIVED ON S3 BUCKET LISTENER");
            log.info("========================================");
            log.info("File Name: {}", fileName);
            log.info("File Size: {} bytes", fileSize);
            log.info("Timestamp: {}", System.currentTimeMillis());
            log.info("========================================");
        }
    }
}