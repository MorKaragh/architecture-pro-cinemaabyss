# Events Service

Микросервис для обработки событий в системе CinemaAbyss.

## Описание

Events Service - это Spring Boot приложение, которое обрабатывает события пользователей, фильмов и платежей. Сервис публикует события в Kafka и также может их потреблять для дополнительной обработки.

## Функциональность

### API Endpoints

- `GET /api/events/health` - Health check
- `POST /api/events/movie` - Создание события фильма
- `POST /api/events/user` - Создание события пользователя
- `POST /api/events/payment` - Создание события платежа

### Kafka Integration

Сервис интегрирован с Apache Kafka для:
- Публикации событий в топики:
  - `movie-events`
  - `user-events`
  - `payment-events`
- Потребления событий из тех же топиков

### Модели данных

#### MovieEvent
```json
{
  "movie_id": 123,
  "title": "Movie Title",
  "action": "viewed",
  "user_id": 456,
  "timestamp": "2024-01-01T12:00:00Z"
}
```

#### UserEvent
```json
{
  "user_id": 456,
  "username": "user123",
  "action": "logged_in",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

#### PaymentEvent
```json
{
  "payment_id": 789,
  "user_id": 456,
  "amount": 9.99,
  "status": "completed",
  "timestamp": "2024-01-01T12:00:00Z",
  "method_type": "credit_card"
}
```

## Технические характеристики

- **Spring Boot**: 3.2.5 (современная версия)
- **Java**: 21 (Eclipse Temurin)
- **Spring Kafka**: 3.1.2
- **Maven**: 3.9.11

## Конфигурация

### Переменные окружения

- `PORT` - Порт сервиса (по умолчанию: 8082)
- `KAFKA_BROKERS` - Адреса Kafka брокеров (по умолчанию: localhost:9092)

### application.yml

```yaml
server:
  port: 8082

spring:
  kafka:
    bootstrap-servers: ${KAFKA_BROKERS:localhost:9092}
    consumer:
      group-id: events-service-group
      auto-offset-reset: earliest
```

## Сборка и запуск

### Локальная разработка

```bash
mvn clean package
java -jar target/events-service-1.0.0.jar
```

### Docker

Современный multi-stage Dockerfile с оптимизацией:

```bash
docker build -t events-service .
docker run -p 8082:8082 events-service
```

**Особенности Dockerfile:**
- Multi-stage build для уменьшения размера образа
- Кэширование Maven зависимостей
- Eclipse Temurin 21 JRE для production
- Maven 3.9.11 с Java 21 для сборки

### Docker Compose

Сервис уже настроен в `docker-compose.yml` и запускается автоматически:

```bash
docker-compose up events-service
```

## Логирование

Сервис логирует:
- Все входящие события через API
- Публикацию событий в Kafka
- Потребление событий из Kafka
- Ошибки сериализации/десериализации

## Тестирование

Для тестирования API используйте Postman коллекцию `CinemaAbyss.postman_collection.json` в папке `tests/postman/`.
