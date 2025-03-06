package org.example;

import org.example.dto.EventDTO;
import org.example.dto.UserDTO;
import org.example.entity.Event;
import org.example.entity.Registration;
import org.example.entity.User;
import org.example.service.EventService;
import org.example.service.RegistrationService;
import org.example.service.TicketService;
import org.example.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
public class EventApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(EventApplication.class, args);
        EventService eventService = context.getBean(EventService.class);
        RegistrationService registrationService = context.getBean(RegistrationService.class);
        TicketService ticketService = context.getBean(TicketService.class);
        UserService userService = context.getBean(UserService.class);
        Scanner scanner = new Scanner(System.in);

        User currentUser = null;

        while (currentUser == null) {
            System.out.println("\nВыберите действие:");
            System.out.println("1. Зарегистрироваться");
            System.out.println("2. Войти");
            System.out.println("3. Выйти");
            System.out.print("Введите номер действия: ");

            int action = scanner.nextInt();
            scanner.nextLine();

            switch (action) {
                case 1 -> {
                    System.out.print("\nВведите имя пользователя: ");
                    String username = scanner.nextLine();
                    System.out.print("Введите email: ");
                    String email = scanner.nextLine();
                    System.out.print("Введите пароль: ");
                    String password = scanner.nextLine();

                    UserDTO userDTO = UserDTO.builder()
                            .username(username)
                            .email(email)
                            .password(password)
                            .build();

                    currentUser = userService.registerUser(userDTO);
                    System.out.println("Пользователь зарегистрирован: " + currentUser);
                }
                case 2 -> {
                    System.out.print("\nВведите email: ");
                    String email = scanner.nextLine();
                    System.out.print("Введите пароль: ");
                    String password = scanner.nextLine();

                    Optional<User> userOptional = userService.login(email, password);
                    if (userOptional.isPresent()) {
                        currentUser = userOptional.get();
                        System.out.println("Добро пожаловать, " + currentUser.getUsername() + "!");
                    } else {
                        System.out.println("Неверный email или пароль. Попробуйте снова.");
                    }
                }
                case 3 -> {
                    System.out.println("Выход...");
                    scanner.close();
                    System.exit(0);
                }
                default -> System.out.println("Неверный ввод. Попробуйте снова.");
            }
        }

        while (true) {
            System.out.print("\n-------------------------------------------------------------------\n");
            System.out.println("Выберите действие:");
            System.out.println("1. Создать событие");
            System.out.println("2. Обновить событие");
            System.out.println("3. Удалить событие");
            System.out.println("4. Просмотреть все события");
            System.out.println("5. Просмотреть всех пользователей");
            System.out.println("6. Зарегистрировать пользователя на событие");
            System.out.println("7. Отменить регистрацию");
            System.out.println("8. Выйти");
            System.out.print("Введите номер действия: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("\nВведите название события: ");
                    String eventName = scanner.nextLine();
                    System.out.print("Введите описание события: ");
                    String eventDescription = scanner.nextLine();
                    System.out.print("Введите максимальное количество участников: ");
                    int maxParticipants = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Через сколько дней планируется начало события (количество дней): ");
                    int daysCount = scanner.nextInt();
                    scanner.nextLine();

                    EventDTO eventDTO = EventDTO.builder()
                            .organizerId(currentUser.getId())
                            .title(eventName)
                            .description(eventDescription)
                            .date(LocalDateTime.now().plusDays(daysCount))
                            .maxParticipants(maxParticipants)
                            .build();

                    Event createdEvent = eventService.createEvent(eventDTO);
                    System.out.println("Событие создано: " + createdEvent);
                }
                case 2 -> {
                    System.out.print("\nВведите ID события для обновления: ");
                    Long eventId = scanner.nextLong();
                    scanner.nextLine();
                    System.out.print("Введите новое название события: ");
                    String newTitle = scanner.nextLine();
                    System.out.print("Введите новое описание события: ");
                    String newDescription = scanner.nextLine();

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
                    System.out.print("\nВведите ID события, чтобы отменить его: ");
                    Long eventId = scanner.nextLong();
                    eventService.cancelEvent(eventId);
                    System.out.println("Событие отменено.");
                }
                case 4 -> {
                    List<Event> events = eventService.getUpcomingEvents();
                    System.out.println("\nСписок всех предстоящих событий:");
                    events.forEach(System.out::println);
                }
                case 5 -> {
                    List<User> users = userService.getAllUsers();
                    System.out.println("\nСписок всех пользователей:");
                    users.forEach(System.out::println);
                }
                case 6 -> {
                    System.out.print("\nВведите ID пользователя: ");
                    Long userId = scanner.nextLong();
                    System.out.print("Введите ID события: ");
                    Long eventId = scanner.nextLong();

                    Registration registration = registrationService.registerUserForEvent(userId, eventId);
                    System.out.println("Пользователь зарегистрирован на событие: " + registration);
                }
                case 7 -> {
                    System.out.print("\nВведите ID регистрации для отмены: ");
                    Long registrationId = scanner.nextLong();
                    registrationService.unregisterUserFromEvent(registrationId);
                    System.out.println("Регистрация отменена.");
                }
                case 8 -> {
                    System.out.println("Выход...");
                    scanner.close();
                    System.exit(0);
                    return;
                }
                default -> System.out.println("Неверный ввод. Попробуйте снова.");
            }
        }
    }
}
