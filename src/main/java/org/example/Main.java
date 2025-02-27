package org.example;

import org.example.config.AppConfig;
import org.example.dto.EventDTO;
import org.example.dto.TicketDTO;
import org.example.dto.UserDTO;
import org.example.enums.EventStatus;
import org.example.service.EventService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println("Бины:");
        System.out.println("___________________________________");
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
        System.out.println("___________________________________\n");

        EventService eventService = context.getBean(EventService.class);

        EventDTO eventDTO = EventDTO.builder()
                .id(1L)
                .date(LocalDate.now())
                .maxParticipants(5)
                .status(EventStatus.PUBLISHED)
                .name("SDU Fest")
                .tickets(new ArrayList<>())
                .build();
        eventService.createEvent(eventDTO);

        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .name("Beko")
                .ticketIds(new ArrayList<>())
                .build();
        eventService.registerUser(userDTO);

        TicketDTO ticket = eventService.registerUserForEvent(1L, 1L);
        System.out.println("Билет выдан: " + ticket);

        UUID ticketNumber = ticket.getTicketNumber();
        eventService.cancelRegistration(ticketNumber);
        System.out.println("Билет отменен: " + ticketNumber);
    }
}
