package mk.dad.services;

import lombok.extern.slf4j.Slf4j;
import mk.dad.services.config.StorageConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
        
        StorageConfig storageConfig = context.getBean(StorageConfig.class);
        storageConfig.initializeStorage();
        
        log.info("File Storage Service is running on http://localhost:8080");
    }
}