package com.example.notificationservice;

import com.example.notificationservice.service.MailSenderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(NotificationServiceApplication.class, args);
    }

}
