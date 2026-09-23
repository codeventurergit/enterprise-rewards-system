package com.core.rewards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(excludeName = {
    "io.awspring.cloud.autoconfigure.sqs.SqsAutoConfiguration",
    "io.awspring.cloud.autoconfigure.core.AwsAutoConfiguration",
    "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class RewardsApplication {
    public static void main(String[] args) {
        SpringApplication.run(RewardsApplication.class, args);
    }
}
