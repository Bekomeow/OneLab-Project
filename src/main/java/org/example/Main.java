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
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        EventService eventService = context.getBean(EventService.class);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Выберите действие:");
            System.out.println("1. Создать событие");
            System.out.println("2. Зарегистрировать пользователя");
            System.out.println("3. Зарегистрировать пользователя на событие");
            System.out.println("4. Отменить регистрацию");
            System.out.println("5. Список всех событий");
            System.out.println("6. Выйти");
            System.out.print("Введите номер действия: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Введите название события: ");
                    String eventName = scanner.nextLine();
                    EventDTO eventDTO = EventDTO.builder()
                            .date(LocalDate.now())
                            .maxParticipants(5)
                            .status(EventStatus.PUBLISHED)
                            .name(eventName)
                            .tickets(new ArrayList<>())
                            .build();
                    EventDTO event = eventService.createEvent(eventDTO);
                    System.out.println("Событие создано: " + eventName + " , ID: " + event.getId());
                    break;
                case 2:
                    System.out.print("Введите имя пользователя: ");
                    String userName = scanner.nextLine();
                    UserDTO userDTO = UserDTO.builder()
                            .name(userName)
                            .ticketIds(new ArrayList<>())
                            .build();
                    UserDTO user = eventService.registerUser(userDTO);
                    System.out.println("Пользователь зарегистрирован: " + userName + " , ID: " + user.getId());
                    break;
                case 3:
                    System.out.print("Введите ID пользователя: ");
                    Long userId = scanner.nextLong();
                    System.out.print("Введите ID события: ");
                    Long eventId = scanner.nextLong();
                    TicketDTO ticket = eventService.registerUserForEvent(userId, eventId);
                    System.out.println("Билет выдан: " + ticket);
                    break;
                case 4:
                    System.out.print("Введите номер билета: ");
                    UUID ticketNumber = UUID.fromString(scanner.next());
                    eventService.cancelRegistration(ticketNumber);
                    System.out.println("Билет отменен: " + ticketNumber);
                    break;
                case 5:
                    System.out.println("Все события: \n" + eventService.getUpcomingEvents());
                    break;
                case 6:
                    System.out.println("Выход...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный ввод. Попробуйте снова.");
            }
        }
    }
}
