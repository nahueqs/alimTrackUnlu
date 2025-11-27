package com.unlu.alimtrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class AlimtrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlimtrackApplication.class, args);
    }

}
