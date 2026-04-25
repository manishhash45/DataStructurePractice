package mk.dad.games;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SnakeAndLadderApplication {
    public static void main(String[] args) {
        SpringApplication.run(SnakeAndLadderApplication.class, args);
        log.info("Snake and Ladder Game Service is running on http://localhost:8080");
    }
}