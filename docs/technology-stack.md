# Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Основной язык разработки |
| Spring Boot | Создание микросервисов |
| Spring Security | Аутентификация и авторизация |
| JWT | Передача информации об авторизации |
| Spring Data JPA | Работа с реляционными БД |
| PostgreSQL | Основное хранилище финансовых данных |
| Apache Kafka | Асинхронное взаимодействие |
| gRPC | Синхронное взаимодействие сервисов |
| Redis | Кеширование и вспомогательные задачи |
| MongoDB | Документное хранилище уведомлений |
| Quartz | Управление отложенными и регулярными задачами |
| Docker | Контейнеризация |
| Docker Compose | Локальный запуск инфраструктуры |
| Kubernetes | Оркестрация контейнеров |
| Maven | Сборка и управление зависимостями |

## Infrastructure roadmap

1. Локальный запуск сервисов.
2. Dockerfile для каждого сервиса.
3. Docker Compose для локальной инфраструктуры.
4. Kafka, PostgreSQL, Redis и MongoDB.
5. Kubernetes Deployments и Services.
6. ConfigMaps и Secrets.
7. Ingress и маршрутизация.
8. Persistent Volumes.
9. Health probes и масштабирование.