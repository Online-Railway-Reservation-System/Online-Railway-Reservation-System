package com.railway.schedulefare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ScheduleFareServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduleFareServiceApplication.class, args);
    }
}