package com.unlu.alimtrack;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
@EnableRetry
@EnableTransactionManagement
public class AlimtrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlimtrackApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Establecer la zona horaria por defecto a Argentina
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
    }

}
