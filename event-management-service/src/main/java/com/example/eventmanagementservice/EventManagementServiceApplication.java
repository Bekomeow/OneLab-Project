package com.example.eventmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
public class EventManagementServiceApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(EventManagementServiceApplication.class, args);
    }

}
