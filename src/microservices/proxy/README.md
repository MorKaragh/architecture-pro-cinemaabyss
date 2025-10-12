# Proxy Service

API Gateway с балансировкой нагрузки для постепенной миграции в CinemaAbyss.

## Описание

Proxy Service - это API Gateway, который обеспечивает постепенную миграцию с монолита на микросервисы. Сервис использует алгоритм балансировки нагрузки для распределения запросов между монолитом и микросервисами.

## Технические характеристики

- **Java**: 21 (Eclipse Temurin)
- **Maven**: 3.9.11
- **HTTP Server**: Java HTTP Server
- **HTTP Client**: Apache HttpClient 5.2.1
- **JSON Processing**: Jackson 2.15.2

## Функциональность

### Load Balancing
- **Gradual Migration**: Постепенная миграция трафика с монолита на микросервисы
- **Percentage-based Routing**: Маршрутизация на основе процентов
- **Health Checks**: Проверка доступности сервисов

### API Endpoints

- `GET /health` - Health check
- `GET /api/users` - Получение пользователей (с балансировкой)
- `POST /api/users` - Создание пользователей (с балансировкой)
- `GET /api/movies` - Получение фильмов (с балансировкой)
- `POST /api/movies` - Создание фильмов (с балансировкой)
- `GET /api/payments` - Получение платежей (с балансировкой)
- `POST /api/payments` - Создание платежей (с балансировкой)
- `GET /api/subscriptions` - Получение подписок (с балансировкой)
- `POST /api/subscriptions` - Создание подписок (с балансировкой)

## Конфигурация

### Переменные окружения

- `PORT` - Порт сервиса (по умолчанию: 8000)
- `MONOLITH_URL` - URL монолита (по умолчанию: http://monolith:8080)
- `MOVIES_SERVICE_URL` - URL микросервиса фильмов (по умолчанию: http://movies-service:8081)
- `EVENTS_SERVICE_URL` - URL микросервиса событий (по умолчанию: http://events-service:8082)
- `GRADUAL_MIGRATION` - Включить постепенную миграцию (по умолчанию: true)
- `MOVIES_MIGRATION_PERCENT` - Процент трафика для микросервиса фильмов (по умолчанию: 50)

## Сборка и запуск

### Локальная разработка

```bash
mvn clean package
java -jar target/proxy-service-1.0.0.jar
```

### Docker

Современный multi-stage Dockerfile с оптимизацией:

```bash
docker build -t proxy-service .
docker run -p 8000:8000 proxy-service
```

**Особенности Dockerfile:**
- Multi-stage build для уменьшения размера образа
- Кэширование Maven зависимостей
- Eclipse Temurin 21 JRE для production
- Maven 3.9.11 с Java 21 для сборки

### Docker Compose

Сервис уже настроен в `docker-compose.yml` и запускается автоматически:

```bash
docker-compose up proxy-service
```

## Архитектура

### Load Balancer
```java
public class LoadBalancer {
    private final Random random = new Random();
    
    public boolean shouldUseMicroservice(String serviceName) {
        if (!gradualMigration) return false;
        
        int percent = getMigrationPercent(serviceName);
        return random.nextInt(100) < percent;
    }
}
```

### HTTP Client
```java
public class HttpClient {
    public String get(String url) throws IOException {
        // HTTP GET запрос с обработкой ошибок
    }
    
    public String post(String url, String body) throws IOException {
        // HTTP POST запрос с обработкой ошибок
    }
}
```

## Логирование

Сервис логирует:
- Все входящие HTTP запросы
- Решения балансировки нагрузки
- Ошибки HTTP клиента
- Статистику маршрутизации

## Мониторинг

- **Health Check**: `GET /health`
- **Load Balancing Stats**: Логи содержат информацию о распределении запросов
- **Error Tracking**: Логирование ошибок при недоступности сервисов

## Тестирование

Для тестирования API используйте Postman коллекцию `CinemaAbyss.postman_collection.json` в папке `tests/postman/`.

### Примеры запросов

```bash
# Health check
curl http://localhost:8000/health

# Получение пользователей (с балансировкой)
curl http://localhost:8000/api/users

# Создание пользователя (с балансировкой)
curl -X POST http://localhost:8000/api/users \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "email": "test@example.com"}'
```