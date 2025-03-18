# Event Management System

## Описание проекта
**Event Management System – микросервисное приложение на Java, реализованное с использованием Spring Boot, Spring Cloud, Kafka и Eureka. Оно предназначено для управления мероприятиями, регистрации пользователей и отправки уведомлений.**

## Архитектура системы
Приложение разделено на несколько сервисов, взаимодействующих через Kafka и управляемых через API Gateway.

### 1. **API Gateway (api-gateway)**
📌 **Задачи:**
- Центральная точка входа для клиентов.
- Маршрутизация запросов к соответствующим микросервисам.
- Управление аутентификацией и авторизацией.

### 2. **AuthService (Сервис аутентификации)**
📌 **Задачи:**
- Регистрация и аутентификация пользователей.
- Управление ролями (USER, MODERATOR, ADMIN).
- Логин пользователей (JWT).
- Отправка в Kafka текущего пользователя при успешном логине.

🛠 **Эндпоинты:**
# 📌 API Аутентификации и Администрирования

## 🔑 Контроллер аутентификации (`/auth`)

| Метод | URL | Описание | Тело запроса | Ответ |
|--------|--------------------------------|--------------------------|-----------------------------|-------------------------------|
| **POST** | `/auth/register` | Регистрация нового пользователя | ✅ JSON: `{ "username": "user1", "email": "user1@example.com", "password": "securepass" }` | 🔄 `200 OK` - `{ "token": "jwt_token" }` |
| **POST** | `/auth/login` | Аутентификация и получение токена | ✅ JSON: `{ "username": "user1", "password": "securepass" }` | 🔄 `200 OK` - `{ "token": "jwt_token" }` |

---

## 🛠 Контроллер администратора (`/auth/admin`)

> **❗ Все эндпоинты требуют роль `ROLE_ADMIN`**

| Метод | URL | Описание | Тело запроса | Ответ |
|--------|------------------------------------------------|--------------------------------|-------------|--------------------------------------------|
| **POST** | `/auth/admin/add-admin/{username}` | Назначить пользователя администратором | ❌ Нет | ✅ `200 OK` - `Пользователь user1 теперь администратор.` |
| **POST** | `/auth/admin/add-moderator/{username}` | Назначить пользователя модератором | ❌ Нет | ✅ `200 OK` - `Пользователь user1 теперь модератор.` |
| **DELETE** | `/auth/admin/delete-user/{username}` | Удалить пользователя | ❌ Нет | ✅ `200 OK` - `Пользователь user1 удален.` |
| **POST** | `/auth/admin/update-role/{username}?role=MODERATOR&add=true` | Добавить или удалить роль у пользователя | ❌ Нет | ✅ `200 OK` - `Роль MODERATOR была добавлена у пользователя user1` |
| **GET** | `/auth/admin/users` | Получить список всех пользователей | ❌ Нет | 🔄 `200 OK` - `[ { "username": "user1", "email": "user1@example.com", "roles": ["USER", "ADMIN"] } ]` |

---

## 🚀 Базовый URL
`http://localhost:8765`

## 🔐 Безопасность
Все эндпоинты в `/auth/admin` требуют наличия роли **`ROLE_ADMIN`**.

### 3. **EventManagementService (Главный сервис, консольное приложение)**
📌 **Задачи:**
- Создание, редактирование и управление мероприятиями.
- Регистрация пользователей на мероприятия.
- Генерация билетов.

🛠 **Взаимодействие через Kafka:**
- При регистрации на мероприятие публикует событие `event.registration.created`.

🛠 **Эндпоинты:**
# 📌 API Endpoints - Event Management Service

## 🎟 Контроллер событий (`/events`)

## Endpoints

### Create a new event
```http
POST /events
```
#### Request Body:
```json
{
  "title": "Spring Boot Workshop",
  "description": "In-depth workshop",
  "date": "2025-03-15T10:00:00",
  "maxParticipants": 50
}
```
#### Response:
`200 OK` - Event created

---

### Update an existing event
```http
PUT /events
```
#### Request Body:
```json
{
  "id": 1,
  "title": "Updated Title",
  "description": "Updated Description"
}
```
#### Response:
`200 OK` - Event updated

---

### Publish an event
```http
POST /events/{eventId}/publish
```
#### Response:
`204 No Content`

---

### Cancel an event (Admin, Moderator, Owner)
```http
POST /events/{eventId}/cancel
```
#### Response:
`204 No Content`

---

### Get upcoming events
```http
GET /events/upcoming
```
#### Response:
`200 OK` - List of events

---

### Get draft events (Moderator only)
```http
GET /events/drafts
```
#### Response:
`200 OK` - List of draft events

---

## Filtering Events

### Search by title and description
```http
GET /events/filter?title=conference&description=technology
```
#### Response:
`200 OK` - List of events

### Search between two dates
```http
GET /events/filter/date?fromDate=2025-01-01T00:00:00&toDate=2025-12-31T23:59:59
```
#### Response:
`200 OK` - List of events

### Search after a specific date (only future events)
```http
GET /events/filter/date?fromDate=2025-06-01T00:00:00
```
#### Response:
`200 OK` - List of events

### Search before a specific date
```http
GET /events/filter/date?toDate=2025-06-01T00:00:00
```
#### Response:
`200 OK` - List of events

---

## Performance Comparison

### Sequential processing
```http
GET /events/performance/sequential
```
#### Response:
`200 OK` - Performance results

### Parallel processing
```http
GET /events/performance/parallel
```
#### Response:
`200 OK` - Performance results

---

## Streaming Data

### Get the most popular events
```http
GET /events/stream/most-popular
```
#### Response:
`200 OK` - List of events

### Group events
```http
GET /events/stream/grouped
```
#### Response:
`200 OK` - Grouped events

### Partition events into categories
```http
GET /events/stream/partitioned
```
#### Response:
`200 OK` - Partitioned events
---

## 🔹 Контроллер регистрации (`/events/registrations`)

| Метод | URL | Описание | Тело запроса | Ответ |
|--------|-------------------------------------------------|---------------------------------|-------------|-----------------------------|
| **POST** | `/events/registrations/{eventId}` | Зарегистрировать пользователя на событие | ❌ Нет | 🔄 `200 OK` - Детали регистрации |
| **DELETE** | `/events/registrations/{registrationId}` | Отменить регистрацию на событие | ❌ Нет | ✅ `204 No Content` |
| **GET** | `/events/registrations` | Получить список регистраций пользователя | ❌ Нет | 🔄 `200 OK` - Список зарегистрированных событий |

---

## 🚀 Базовый URL
`http://localhost:8765`

## 🔐 Безопасность
Некоторые эндпоинты требуют аутентификации (`ROLE_USER`, `ROLE_ADMIN`, `ROLE_MODERATOR`).

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
| EventManagementService  | `event.registration.created` | NotificationService     | Уведомление о регистрации        |
| EventManagementService  | `event.status.notification` | NotificationService     | Уведомление об обновлениях статуса мероприятия (PUBLISHED/CANCELLED)        |

## Запуск инфраструктуры перед запуском EventService
Перед запуском EventService необходимо развернуть инфраструктурные компоненты: Kafka и Elasticsearch.

### Подготовка к запуску
Docker Compose файл с необходимыми сервисами находится в папке:
📂 EventService/src/main/resources

### Шаги для развертывания
- Перейти в директорию с конфигурацией Docker Compose.
- Запустить сервисы в фоновом режиме.
- Проверить, что контейнеры работают.
- После успешного запуска можно запускать EventService.


## Стек технологий
- Java 17+
- Spring Boot
- Spring Cloud Gateway
- Spring Security
- Spring Cloud Netflix Eureka
- Maven
- Apache Kafka
- Docker Compose
- H2 (временная база данных для тестирования)
- PostgreSQL (основная база данных)
- JUnit 5, Mockito, AssertJ

## Контакты
**Разработчик:** Toktamyssov Bekzhan

