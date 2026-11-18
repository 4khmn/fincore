# Scheduler Service — ERD

## 1. SCHEDULED_OPERATIONS

Хранит бизнес-информацию о запланированных переводах.

| Поле | Тип | Ограничения | Описание |
|---|---|---|---|
| id | UUID | PK | Идентификатор расписания |
| owner_user_id | UUID | NOT NULL | Владелец расписания |
| from_account_id | UUID | NOT NULL | Счёт отправителя |
| to_account_id | UUID | NOT NULL | Счёт получателя |
| amount_minor | BIGINT | NOT NULL | Сумма в минимальных единицах |
| currency | VARCHAR(3) | NOT NULL | Валюта |
| schedule_type | VARCHAR(20) | NOT NULL | ONCE или RECURRING |
| execute_at | TIMESTAMPTZ | NULL | Время разового запуска |
| cron_expression | VARCHAR(100) | NULL | Cron для повторных запусков |
| time_zone | VARCHAR(100) | NOT NULL | Часовой пояс |
| status | VARCHAR(20) | NOT NULL | Статус расписания |
| next_execution_at | TIMESTAMPTZ | NULL | Следующий запуск |
| created_at | TIMESTAMPTZ | NOT NULL | Дата создания |
| updated_at | TIMESTAMPTZ | NOT NULL | Дата обновления |

Примечания:
- Для ONCE используется execute_at.
- Для RECURRING используется cron_expression.
- next_execution_at позволяет быстро получить время следующего запуска.
- Владелец определяется по JWT.
- Счета принадлежат Ledger Service, поэтому внешних SQL-связей нет.

## 2. SCHEDULED_EXECUTIONS

Хранит историю конкретных запусков расписаний.

| Поле | Тип | Ограничения | Описание |
|---|---|---|---|
| id | UUID | PK | Идентификатор выполнения |
| scheduled_operation_id | UUID | FK, NOT NULL | Расписание |
| execution_key | VARCHAR(255) | UNIQUE, NOT NULL | Уникальный ключ запуска |
| transfer_id | UUID | NULL | Идентификатор перевода |
| status | VARCHAR(20) | NOT NULL | Статус выполнения |
| scheduled_for | TIMESTAMPTZ | NOT NULL | Плановое время запуска |
| executed_at | TIMESTAMPTZ | NULL | Фактическое время |
| created_at | TIMESTAMPTZ | NOT NULL | Дата создания |

Примечания:
- Одно расписание может иметь множество выполнений.
- execution_key уникален для конкретного запланированного запуска.
- transfer_id связывает выполнение с Transfer Service.
- Повторная обработка одного запуска не должна создавать новый перевод.

## 3. OUTBOX_EVENTS

Хранит события, которые должны быть опубликованы в Kafka.

| Поле | Тип | Ограничения | Описание |
|---|---|---|---|
| id | UUID | PK | Идентификатор события |
| aggregate_id | UUID | NOT NULL | Идентификатор расписания или выполнения |
| event_type | VARCHAR(100) | NOT NULL | Тип события |
| payload | JSONB | NOT NULL | Содержимое события |
| created_at | TIMESTAMPTZ | NOT NULL | Дата создания |
| published_at | TIMESTAMPTZ | NULL | Дата публикации |

Примечания:
- Outbox позволяет сохранить бизнес-изменение и событие
  в одной транзакции PostgreSQL.
- Отдельный publisher отправляет события в Kafka.
- Событие может быть опубликовано повторно, поэтому потребитель
  обязан поддерживать идемпотентность.

## Связи

- SCHEDULED_OPERATIONS 1:N SCHEDULED_EXECUTIONS.
- SCHEDULED_OPERATIONS 1:N OUTBOX_EVENTS — через aggregate_id
  как логическую связь, без обязательного FK.

## Таблицы Quartz

Quartz JDBC JobStore создаёт собственные служебные таблицы.

Они отвечают за:
- хранение Jobs;
- хранение Triggers;
- состояние задач;
- блокировки;
- кластерную координацию.

Эти таблицы не заменяют SCHEDULED_OPERATIONS
и SCHEDULED_EXECUTIONS.

## Важные правила

- Бизнес-расписание и Quartz Job — разные сущности.
- Отмена расписания должна синхронизироваться с Quartz.
- Повторные запуски должны иметь устойчивый execution_key.
- Изменение расписания не должно менять уже созданную историю запусков.
- Выполнение финансовой операции делегируется Transfer Service.