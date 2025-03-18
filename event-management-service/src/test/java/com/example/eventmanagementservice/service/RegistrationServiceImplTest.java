//package com.example.eventmanagementservice.service;
//
//import com.example.eventmanagementservice.entity.Event;
//import com.example.eventmanagementservice.entity.Registration;
//import com.example.eventmanagementservice.enums.EventStatus;
//import com.example.eventmanagementservice.repository.EventRepository;
//import com.example.eventmanagementservice.repository.RegistrationRepository;
//import com.example.eventmanagementservice.service.impl.RegistrationServiceImpl;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.kafka.core.KafkaTemplate;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class RegistrationServiceImplTest {
//
//    @InjectMocks
//    private RegistrationServiceImpl registrationService;
//
//    @Mock
//    private RegistrationRepository registrationRepository;
//
//    @Mock
//    private EventRepository eventRepository;
//
//    @Mock
//    private TicketService ticketService;
//
//    @Mock
//    private KafkaTemplate<String, Object> kafkaTemplate;
//
//    private Event event;
//    private Registration registration;
//
//    @BeforeEach
//    void setUp() {
//        event = Event.builder()
//                .id(1L)
//                .title("Test Event")
//                .description("Test Description")
//                .date(LocalDateTime.now().plusDays(1))
//                .maxParticipants(100)
//                .status(EventStatus.PUBLISHED)
//                .build();
//
//        registration = Registration.builder()
//                .id(1L)
//                .username("TestName")
//                .event(event)
//                .build();
//    }
//
//    @Test
//    void registerUserForEvent_ShouldThrowException_WhenEventNotFound() {
//        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> registrationService.registerUserForEvent(1L))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessage("Мероприятие не найдено");
//    }
//
//    @Test
//    void unregisterUserFromEvent_ShouldDeleteRegistration() {
//        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
//
//        registrationService.unregisterUserFromEvent(1L);
//
//        verify(registrationRepository, times(1)).delete(registration);
//    }
//
//    @Test
//    void unregisterUserFromEvent_ShouldThrowException_WhenRegistrationNotFound() {
//        when(registrationRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> registrationService.unregisterUserFromEvent(1L))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessage("Регистрация не найдена");
//    }
//}
