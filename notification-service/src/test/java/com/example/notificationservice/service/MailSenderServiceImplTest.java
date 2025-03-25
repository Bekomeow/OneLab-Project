package com.example.notificationservice.service;

import com.example.commonlibrary.enums.notification.NotificationStatus;
import com.example.notificationservice.entity.NotificationLog;
import com.example.notificationservice.repository.NotificationLogRepository;
import com.example.notificationservice.service.impl.MailSenderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MailSenderServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @InjectMocks
    private MailSenderServiceImpl mailSenderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mailSenderService = new MailSenderServiceImpl(mailSender, notificationLogRepository);
    }

    @Test
    void send_ShouldLogAndSendEmailSuccessfully() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        mailSenderService.send(to, subject, body);

        ArgumentCaptor<NotificationLog> logCaptor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository).save(logCaptor.capture());

        NotificationLog savedLog = logCaptor.getValue();
        assertThat(savedLog.getRecipientEmail()).isEqualTo(to);
        assertThat(savedLog.getSubject()).isEqualTo(subject);
        assertThat(savedLog.getMessage()).isEqualTo(body);
        assertThat(savedLog.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(savedLog.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());

        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(mailCaptor.capture());

        SimpleMailMessage sentMessage = mailCaptor.getValue();
        assertThat(sentMessage.getTo()).containsExactly(to);
        assertThat(sentMessage.getSubject()).isEqualTo(subject);
        assertThat(sentMessage.getText()).isEqualTo(body);
    }

    @Test
    void send_ShouldLogFailure_WhenMailSenderThrowsException() {
        String to = "fail@example.com";
        String subject = "Fail Subject";
        String body = "Fail Body";

        doThrow(new MailException("Mail error") {}).when(mailSender).send(any(SimpleMailMessage.class));

        mailSenderService.send(to, subject, body);

        ArgumentCaptor<NotificationLog> logCaptor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository).save(logCaptor.capture());

        NotificationLog savedLog = logCaptor.getValue();
        assertThat(savedLog.getRecipientEmail()).isEqualTo(to);
        assertThat(savedLog.getSubject()).isEqualTo(subject);
        assertThat(savedLog.getMessage()).isEqualTo(body);
        assertThat(savedLog.getStatus()).isEqualTo(NotificationStatus.FAILED);
    }
}
