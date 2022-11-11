package com.reactivebingo.api;

import com.reactivebingo.api.configs.RetryConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@ConfigurationPropertiesScan(basePackageClasses = RetryConfig.class)
@SpringBootApplication
@EnableReactiveMongoAuditing(dateTimeProviderRef = "dateTimeProvider")
public class ReactiveBingoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveBingoApiApplication.class, args);
    }

}
