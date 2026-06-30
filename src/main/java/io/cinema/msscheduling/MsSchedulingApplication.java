package io.cinema.msscheduling;

import io.cinema.config.AuditingConfig;
import io.cinema.config.RedisConfig;
import io.cinema.config.WebClientConfiguration;
import io.cinema.controller.ExceptionHandlers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;

@SpringBootApplication
@EnableReactiveMethodSecurity
@EnableCaching
@ComponentScan(basePackages = "io.cinema")
@Import({ExceptionHandlers.class, AuditingConfig.class, RedisConfig.class, WebClientConfiguration.class})
public class MsSchedulingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsSchedulingApplication.class, args);
    }

}
