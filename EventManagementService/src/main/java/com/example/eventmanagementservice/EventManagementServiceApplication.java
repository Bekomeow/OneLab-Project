package com.example.eventmanagementservice;

import com.example.eventmanagementservice.dto.AuthRequest;
import com.example.eventmanagementservice.dto.AuthResponse;
import com.example.eventmanagementservice.dto.EventDTO;
import com.example.eventmanagementservice.entity.Event;
import com.example.eventmanagementservice.entity.Registration;
import com.example.eventmanagementservice.listener.EventListener;
import com.example.eventmanagementservice.search.document.EventDocument;
import com.example.eventmanagementservice.search.searchService.EventSearchService;
import com.example.eventmanagementservice.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@SpringBootApplication
public class EventManagementServiceApplication {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern NON_EMPTY_PATTERN = Pattern.compile("^\\S.*$");

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(EventManagementServiceApplication.class, args);
        EventService eventService = context.getBean(EventService.class);
        RegistrationService registrationService = context.getBean(RegistrationService.class);
        TicketService ticketService = context.getBean(TicketService.class);
        AuthService authService = context.getBean(AuthService.class);
        EventSearchService eventSearchService = context.getBean(EventSearchService.class);
        EventListener eventListener = context.getBean(EventListener.class);
        AuthResponse currentUser = null;

        while (currentUser == null) {
            System.out.println("\nВыберите действие:");
            System.out.println("1. Войти");
            System.out.println("2. Выйти");
            System.out.print("Введите номер действия: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Ошибка: Введите число 1 или 2.");
                scanner.next();
                continue;
            }

            int action = scanner.nextInt();
            scanner.nextLine();

            switch (action) {
                case 1 -> {
                    String email;
                    String password;

                    do {
                        System.out.print("Введите email: ");
                        email = scanner.nextLine().trim();
                        if (!EMAIL_PATTERN.matcher(email).matches()) {
                            System.out.println("Ошибка: Некорректный email. Попробуйте снова.");
                        }
                    } while (!EMAIL_PATTERN.matcher(email).matches());

                    do {
                        System.out.print("Введите пароль (не менее 6 символов): ");
                        password = scanner.nextLine().trim();
                        if (password.length() < 6) {
                            System.out.println("Ошибка: Пароль должен содержать не менее 6 символов.");
                        }
                    } while (password.length() < 6);

                    AuthRequest authRequest = AuthRequest.builder()
                            .email(email)
                            .password(password)
                            .build();

                    authService.login(email, password);

                    try {
                        currentUser = eventListener.waitForResponse(5, TimeUnit.SECONDS);
                        if (currentUser != null) {
                            System.out.println("Пользователь зарегистрирован: " + currentUser.getEmail());
                        } else {
                            System.err.println("Ответ от Кафки не получен, попробуйте снова.");
                        }
                    } catch (InterruptedException e) {
                        System.err.println("Прервано во время ожидания ответа Кафки");
                    }
                }
                case 2 -> {
                    System.out.println("Выход...");
                    scanner.close();
                    System.exit(0);
                }
                default -> System.out.println("Ошибка: Введите число 1 или 2.");
            }
        }

        while (true) {
            System.out.print("\n-------------------------------------------------------------------\n");
            System.out.println("Выберите действие:");
            System.out.println("1. Создать событие");
            System.out.println("2. Обновить событие");
            System.out.println("3. Удалить событие");
            System.out.println("4. Просмотреть все события");
            System.out.println("5. Искать события");
            System.out.println("6. Зарегистрироваться на событие");
            System.out.println("7. Отменить регистрацию");
            System.out.println("8. Выйти");
            System.out.print("Введите номер действия: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Ошибка: Введите число от 1 до 7.");
                scanner.next(); // Очистка некорректного ввода
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    String eventName;
                    do {
                        System.out.print("\nВведите название события: ");
                        eventName = scanner.nextLine().trim();
                        if (!NON_EMPTY_PATTERN.matcher(eventName).matches()) {
                            System.out.println("Ошибка: Название события не может быть пустым.");
                        }
                    } while (!NON_EMPTY_PATTERN.matcher(eventName).matches());

                    String eventDescription;
                    do {
                        System.out.print("Введите описание события: ");
                        eventDescription = scanner.nextLine().trim();
                        if (!NON_EMPTY_PATTERN.matcher(eventDescription).matches()) {
                            System.out.println("Ошибка: Описание события не может быть пустым.");
                        }
                    } while (!NON_EMPTY_PATTERN.matcher(eventDescription).matches());

                    int maxParticipants;
                    do {
                        System.out.print("Введите максимальное количество участников (больше 0): ");
                        while (!scanner.hasNextInt()) {
                            System.out.println("Ошибка: Введите положительное число.");
                            scanner.next();
                        }
                        maxParticipants = scanner.nextInt();
                        scanner.nextLine();
                    } while (maxParticipants <= 0);

                    int daysCount;
                    do {
                        System.out.print("Через сколько дней планируется начало события (0 и более): ");
                        while (!scanner.hasNextInt()) {
                            System.out.println("Ошибка: Введите неотрицательное число.");
                            scanner.next();
                        }
                        daysCount = scanner.nextInt();
                        scanner.nextLine();
                    } while (daysCount < 0);

                    EventDTO eventDTO = EventDTO.builder()
                            .organizerId(currentUser.getId())
                            .title(eventName)
                            .description(eventDescription)
                            .date(LocalDateTime.now().plusDays(daysCount))
                            .maxParticipants(maxParticipants)
                            .build();

                    Event createdEvent = eventService.createEvent(eventDTO);

                    eventSearchService.indexEvent(createdEvent);

                    System.out.println("Событие создано: " + createdEvent);
                }
                case 2 -> {
                    Long eventId = getPositiveLong("\nВведите ID события для обновления: ");

                    if (!eventService.eventExists(eventId)) {
                        System.out.println("Ошибка: Событие с ID " + eventId + " не найдено.");
                        break;
                    }

                    String newTitle;
                    do {
                        System.out.print("Введите новое название события: ");
                        newTitle = scanner.nextLine().trim();
                        if (!NON_EMPTY_PATTERN.matcher(newTitle).matches()) {
                            System.out.println("Ошибка: Название события не может быть пустым.");
                        }
                    } while (!NON_EMPTY_PATTERN.matcher(newTitle).matches());

                    String newDescription;
                    do {
                        System.out.print("Введите новое описание события: ");
                        newDescription = scanner.nextLine().trim();
                        if (!NON_EMPTY_PATTERN.matcher(newDescription).matches()) {
                            System.out.println("Ошибка: Описание события не может быть пустым.");
                        }
                    } while (!NON_EMPTY_PATTERN.matcher(newDescription).matches());

                    EventDTO eventDTO = EventDTO.builder()
                            .id(eventId)
                            .title(newTitle)
                            .description(newDescription)
                            .maxParticipants(0)
                            .build();

                    Event updatedEvent = eventService.updateEvent(eventDTO);
                    System.out.println("Событие обновлено: " + updatedEvent);
                }
                case 3 -> {
                    Long eventId = getPositiveLong("\nВведите ID события, чтобы отменить его: ");
                    eventService.cancelEvent(eventId);
                    System.out.println("Событие отменено.");
                }
                case 4 -> {
                    List<Event> events = eventService.getUpcomingEvents();
                    System.out.println("\nСписок всех предстоящих событий:");
                    events.forEach(System.out::println);
                }
                case 5 -> {
                    System.out.print("Введите ключевые слова для поиска: ");
                    String query = scanner.nextLine().trim();

                    if (query.isEmpty()) {
                        System.out.println("Ошибка: Поисковый запрос не может быть пустым.");
                        break;
                    }

                    List<Long> eventIds = eventSearchService.searchEventIds(query);

                    if (eventIds.isEmpty()) {
                        System.out.println("По вашему запросу ничего не найдено.");
                    } else {
                        List<Event> events = eventService.findEventsByIds(eventIds);
                        System.out.println("\nНайденные события:");
                        events.forEach(System.out::println);
                    }
                }
                case 6 -> {
                    Long eventId = getPositiveLong("Введите ID события: ");
                    Registration registration = registrationService.registerUserForEvent(
                            currentUser.getId(), currentUser.getEmail(), eventId);
                    System.out.println("Пользователь зарегистрирован на событие: " + registration);
                }
                case 7 -> {
                    Long registrationId = getPositiveLong("\nВведите ID регистрации для отмены: ");
                    registrationService.unregisterUserFromEvent(registrationId);
                    System.out.println("Регистрация отменена.");
                }
                case 8 -> {
                    System.out.println("Выход...");
                    scanner.close();
                    System.exit(0);
                    return;
                }
                default -> System.out.println("Ошибка: Введите число от 1 до 7.");
            }
        }


    }

    private static Long getPositiveLong(String message) {
        Long value;
        do {
            System.out.print(message);
            while (!scanner.hasNextLong()) {
                System.out.println("Ошибка: Введите положительное число.");
                scanner.next();
            }
            value = scanner.nextLong();
            scanner.nextLine();
        } while (value <= 0);
        return value;
    }

}
