package com.example.notificationservice.service;

public interface MailSenderService {
    void send(String to, String subject, String body);
}
