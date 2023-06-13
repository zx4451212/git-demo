package com.czx.gogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GogoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GogoApplication.class, args);
    }

}
