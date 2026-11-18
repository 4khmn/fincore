# API Gateway — Routes

## Назначение

API Gateway является единой внешней точкой входа FinCore.

Все клиентские HTTP-запросы проходят через Gateway.

Gateway отвечает за:
- Маршрутизацию запросов.
- Проверку JWT.
- CORS.
- Базовое ограничение частоты запросов (Rate Limiting).
- Передачу запроса соответствующему микросервису.

Gateway не содержит бизнес-логики и не изменяет финансовые данные.

## Адрес

Локальный адрес:

http://localhost:8080

Все внешние API используют префикс /api/v1.

## Маршруты

| Внешний путь | Метод | Целевой сервис | Внутренний путь |
|---|---|---|---|
| /api/v1/auth/** | Все | Identity | /api/v1/auth/** |
| /api/v1/users/** | Все | Identity | /api/v1/users/** |
| /api/v1/accounts/** | Все | Ledger | /api/v1/accounts/** |
| /api/v1/transfers/** | Все | Transfer | /api/v1/transfers/** |
| /api/v1/schedules/** | Все | Scheduler | /api/v1/schedules/** |

## Адреса сервисов в локальной разработке

| Сервис | Адрес |
|---|---|
| API Gateway | localhost:8080 |
| Identity Service | localhost:8081 |
| Ledger Service | localhost:8082 |
| Transfer Service | localhost:8083 |
| Scheduler Service | localhost:8084 |

В Docker Compose вместо localhost будут использоваться
имена сервисов внутри Docker-сети.

## Примеры маршрутизации

Запрос клиента:

POST http://localhost:8080/api/v1/auth/login

Gateway перенаправляет запрос:

Identity Service:
POST http://identity-service:8081/api/v1/auth/login

---

Запрос клиента:

GET http://localhost:8080/api/v1/accounts

Gateway перенаправляет запрос:

Ledger Service:
GET http://ledger-service:8082/api/v1/accounts

---

Запрос клиента:

POST http://localhost:8080/api/v1/transfers

Gateway перенаправляет запрос:

Transfer Service:
POST http://transfer-service:8083/api/v1/transfers

---

Запрос клиента:

POST http://localhost:8080/api/v1/schedules

Gateway перенаправляет запрос:

Scheduler Service:
POST http://scheduler-service:8084/api/v1/schedules

## Авторизация

Публичные маршруты без JWT:
- POST /api/v1/auth/register
- POST /api/v1/auth/login

Остальные маршруты требуют действительный JWT.

Gateway проверяет токен и отклоняет запросы с отсутствующим
или недействительным токеном.

Микросервисы также должны проверять JWT и права доступа
к собственным ресурсам. Gateway не является единственной
границей безопасности.

## Внутреннее взаимодействие

Gateway не маршрутизирует внутренние gRPC-вызовы.

Например:

Transfer Service -> Ledger Service

Это внутреннее взаимодействие между сервисами.

Также Gateway не маршрутизирует Kafka-сообщения.

## Ошибки Gateway

- 401 — отсутствует или недействителен JWT.
- 404 — маршрут не найден.
- 429 — превышен лимит запросов.
- 502 — целевой сервис недоступен.
- 503 — целевой сервис временно недоступен.

## Принципы

- Клиент не должен обращаться напрямую к микросервисам.
- Внешние HTTP-запросы проходят через Gateway.
- Внутренние адреса сервисов не должны быть публично доступны.
- Gateway не должен содержать финансовую бизнес-логику.