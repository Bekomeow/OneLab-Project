# Event Management System

## Описание проекта
**Event Management System** – консольное Java-приложение, реализованное с использованием Spring Boot и Maven. Оно предназначено для управления мероприятиями, регистрации пользователей и генерации билетов. В будущем проект может быть расширен и интегрирован с другими системами, такими как Camunda BPM для автоматизации бизнес-процессов.

## Архитектура системы
Проект состоит из трех основных сервисов, взаимодействующих через Kafka:

### 1. **AuthService (Сервис аутентификации)**
📌 **Задачи:**
- Регистрация пользователей.
- Логин пользователей (без JWT, простая логика).
- Отправка в Kafka текущего пользователя при успешном логине.

📦 **Сущности:**
- `User` (id, username, email, password, role_id).
- `Role` (id, name).

🛠 **Взаимодействие через Kafka:**
- При регистрации нового пользователя публикует событие `auth.user.registered`.
- При логине пользователя слушает `auth.user.login.request`, проверяет учетные данные и отправляет результат в `auth.user.login.response`.

### 2. **EventManagementService (Главный сервис, консольное приложение)**
📌 **Задачи:**
- Создание, редактирование и управление мероприятиями.
- Регистрация пользователей на мероприятия.
- Запрос на логин пользователя и хранение его в контексте.
- Генерация билетов.

📦 **Сущности:**
- `Event` (id, name, description, date, location, createdBy).
- `Registration` (id, user_id, event_id, status).
- `Ticket` (id, registration_id, qr_code, issue_date).

🛠 **Взаимодействие через Kafka:**
- При логине пользователя отправляет запрос в `auth.user.login.request`.
- Ожидает ответ с данными пользователя из `auth.user.login.response` и сохраняет его в контексте.
- При регистрации на мероприятие публикует событие `event.registration.created`.

### 3. **NotificationService (Сервис уведомлений)**
📌 **Задачи:**
- Подписывается на события о регистрации пользователей на мероприятия.
- Отправляет email с информацией о мероприятии и билете.

🛠 **Взаимодействие через Kafka:**
- Слушает `event.registration.created` и отправляет email пользователю.

## Функциональные возможности
### 1. Управление мероприятиями
- Создание, обновление и удаление мероприятия.
- Установка максимального количества участников.
- Просмотр списка предстоящих мероприятий.

### 2. Управление пользователями
- Регистрация новых пользователей.
- Авторизация пользователей.
- Просмотр списка всех пользователей.

### 3. Регистрация на мероприятия
- Регистрация пользователя на мероприятие.
- Отмена регистрации пользователя.

### 4. Управление билетами
- Генерация уникальных билетов при успешной регистрации.
- Хранение билетов с привязкой к пользователю и мероприятию.
- Отмена билета.

### 5. Взаимодействие с бизнес-логикой
- Проверка возможности регистрации на мероприятие (учет лимита мест).
- Автоматическое изменение статуса мероприятия:
  - `DRAFT` – черновик (создано, но не опубликовано).
  - `PUBLISHED` – мероприятие доступно для регистрации.
  - `REGISTRATION_CLOSED` – регистрация завершена.
  - `IN_PROGRESS` – мероприятие в процессе.
  - `COMPLETED` – мероприятие завершено.
  - `CANCELLED` – мероприятие отменено.
- Автоматическое изменение статуса билетов:
  - `ACTIVE` – билет был использован.
  - `USED` – билет активен.
  - `CANCELLED` – билет был возвращен.

## Взаимодействие через Kafka
| Производитель            | Kafka Topic                 | Потребитель             | Описание                          |
|--------------------------|-----------------------------|-------------------------|-----------------------------------|
| EventManagementService  | `auth.user.login.request`   | AuthService             | Запрос на логин пользователя     |
| AuthService             | `auth.user.login.response`  | EventManagementService  | Ответ с данными пользователя     |
| EventManagementService  | `event.registration.created` | NotificationService     | Уведомление о регистрации        |

## Использование
После запуска консольного приложения пользователю предлагается выбрать действие из меню:
1. **Регистрация или вход** – пользователю необходимо зарегистрироваться или войти в систему.
2. **Создание события** – вводится название события, описание и максимальное количество участников.
3. **Обновление или удаление события** – можно изменить или удалить существующее мероприятие.
4. **Просмотр всех событий** – выводится список всех предстоящих мероприятий.
5. **Просмотр списка пользователей** – отображает всех зарегистрированных пользователей.
6. **Регистрация на событие** – указывается ID пользователя и ID события, выполняется регистрация.
7. **Отмена регистрации** – вводится ID регистрации, она аннулируется.
8. **Выход** – завершает работу приложения.

## Стек технологий
- Java 17+
- Spring Boot
- Maven
- Apache Kafka
- H2 (временная база данных для тестирования)
- PostgreSQL (основная база данных)
- JUnit 5, Mockito, AssertJ

## Контакты
**Разработчик:** Toktamyssov Bekzhan

