//package com.example.notificationservice.listener;
//
//import com.example.notificationservice.dto.EventRegistration;
//import com.example.notificationservice.service.MailSenderService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.time.Month;
//
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class NotificationListenerTest {
//
//    @Mock
//    private MailSenderService mailSenderService;
//
//    @InjectMocks
//    private NotificationListener notificationListener;
//
//    private EventRegistration registration;
//
//    @BeforeEach
//    void setUp() {
//        registration = EventRegistration.builder()
//                .email("test@example.com")
//                .title("Spring Boot Workshop")
//                .description("An advanced workshop on Spring Boot.")
//                .date(LocalDateTime.of(2025, Month.MARCH, 15, 10, 0))
//                .maxParticipants(50)
//                .build();
//    }
//
//    @Test
//    void listen_ShouldCallMailSenderService() {
//        notificationListener.listen(registration);
//
//        verify(mailSenderService, times(1)).send(
//                eq("test@example.com"),
//                eq("Регистрации на событие: Spring Boot Workshop"),
//                anyString()
//        );
//    }
//
//    @Test
//    void listen_ShouldLogMessage() {
//        notificationListener.listen(registration);
//
//        System.out.println("Тест: проверка логирования");
//        Mockito.verify(mailSenderService).send(anyString(), anyString(), anyString());
//    }
//}
