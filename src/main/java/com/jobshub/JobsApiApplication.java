package com.jobshub;

import org.springframework.scheduling.annotation.EnableScheduling; // we'll close status automatically
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableScheduling
public class JobsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobsApiApplication.class, args);
    }

}
