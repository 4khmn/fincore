# Transfer Service — ERD

## 1. TRANSFERS

Основная таблица переводов.

| Поле | Тип | Ограничения | Описание |
|---|---|---|---|
| id | UUID | PK | Идентификатор перевода |
| sender_user_id | UUID | NOT NULL | Пользователь-отправитель |
| from_account_id | UUID | NOT NULL | Счёт списания |
| to_account_id | UUID | NOT NULL | Счёт зачисления |
| amount_minor | BIGINT | NOT NULL | Сумма в минимальных единицах |
| currency | VARCHAR(3) | NOT NULL | Валюта |
| status | VARCHAR(20) | NOT NULL | Статус перевода |
| created_at | TIMESTAMPTZ | NOT NULL | Дата создания |
| updated_at | TIMESTAMPTZ | NOT NULL | Дата обновления |

Примечания:
- sender_user_id — владелец счёта отправителя.
- Получатель определяется через счёт в Ledger.
- Идентификаторы счетов не являются SQL-внешними ключами.
- amount_minor должен быть больше нуля.

## 2. IDEMPOTENCY_RECORDS

Хранит результаты обработки запросов с idempotency key.

| Поле | Тип | Ограничения | Описание |
|---|---|---|---|
| id | UUID | PK | Идентификатор записи |
| user_id | UUID | NOT NULL | Пользователь |
| idempotency_key | VARCHAR(255) | NOT NULL | Ключ запроса |
| request_hash | VARCHAR(255) | NOT NULL | Хеш параметров запроса |
| transfer_id | UUID | FK, NULL | Связанный перевод |
| created_at | TIMESTAMPTZ | NOT NULL | Дата создания |

Ограничения:
- UNIQUE(user_id, idempotency_key).
- Один ключ пользователя соответствует одному запросу.
- При повторном запросе проверяется request_hash.

## 3. TRANSFER_STATUS_HISTORY

История изменения статусов перевода.

| Поле | Тип | Ограничения | Описание |
|---|---|---|---|
| id | UUID | PK | Идентификатор записи |
| transfer_id | UUID | FK, NOT NULL | Перевод |
| status | VARCHAR(20) | NOT NULL | Новый статус |
| reason | VARCHAR(500) | NULL | Причина изменения |
| changed_at | TIMESTAMPTZ | NOT NULL | Дата изменения |

## Связи

- TRANSFERS 1:N TRANSFER_STATUS_HISTORY.
- TRANSFERS 1:0..1 IDEMPOTENCY_RECORDS.

## Важные правила

- Перевод создаётся с уникальным идентификатором.
- Повторный запрос не создаёт второй перевод.
- Изменение статуса фиксируется в истории.
- Финансовая операция проводится только через Ledger.
- Transfer Service не хранит и не обновляет баланс.
- После COMPLETED нельзя просто изменить статус на CANCELLED.
  Для возврата требуется отдельная компенсирующая операция.

## Примечание о взаимодействии с Ledger

TRANSFER.id используется как устойчивый идентификатор операции
для Ledger.

При повторном обращении Transfer Service передаёт тот же
operationId, чтобы Ledger не провёл перевод повторно.