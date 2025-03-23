package com.example.notificationservice.service.impl;

import com.example.commonlibrary.enums.notification.NotificationStatus;
import com.example.notificationservice.entity.NotificationLog;
import com.example.notificationservice.repository.NotificationLogRepository;
import com.example.notificationservice.service.MailSenderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {
    private final JavaMailSender mailSender;
    private final NotificationLogRepository notificationLogRepository;

    @Value(value = "${spring.mail.username}")
    private String from;

    public void send(String to, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        mailMessage.setFrom(from);

        NotificationLog log = new NotificationLog();
        log.setRecipientEmail(to);
        log.setSubject(subject);
        log.setMessage(body);
        log.setCreatedAt(LocalDateTime.now());

        try {
            mailSender.send(mailMessage);
            log.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            log.setStatus(NotificationStatus.FAILED);
            System.err.println("Ошибка отправки письма: " + e.getMessage());
        }

        notificationLogRepository.save(log);
    }

}
