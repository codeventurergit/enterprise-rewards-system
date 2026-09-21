package com.core.rewards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync   // Spawns local background execution threads for eventual consistency
@EnableRetry   // Retry if an optimistic locking exception occurs
public class RewardsApplication {
    public static void main(String[] args) {
        SpringApplication.run(RewardsApplication.class, args);
    }
}
