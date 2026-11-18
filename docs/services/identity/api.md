# Identity Service — API

## Назначение

Identity Service отвечает за регистрацию пользователей,
аутентификацию, авторизацию и управление профилем.

Сервис не управляет счетами и денежными средствами.

Base path: /api/v1

## 1. Регистрация

POST /auth/register

Авторизация: не требуется.

Request:

{
"email": "denis@example.com",
"password": "StrongPassword123"
}

Response: 201 Created

{
"id": "uuid",
"email": "denis@example.com",
"createdAt": "2026-11-18T10:00:00Z"
}

Ошибки:
- 400 — некорректные данные.
- 409 — пользователь с таким email уже существует.

Правила:
- Email должен быть уникальным.
- Пароль сохраняется только в виде хеша.
- Пользователь получает роль USER по умолчанию.

## 2. Вход

POST /auth/login

Авторизация: не требуется.

Request:

{
"email": "denis@example.com",
"password": "StrongPassword123"
}

Response: 200 OK

{
"accessToken": "jwt-token",
"tokenType": "Bearer",
"expiresIn": 3600
}

Ошибки:
- 400 — некорректные данные.
- 401 — неверный email или пароль.

Правила:
- После успешной аутентификации выдаётся JWT.
- JWT содержит идентификатор пользователя и его роли.
- В первой версии используем access token без refresh token.

## 3. Получение текущего пользователя

GET /users/me

Авторизация: обязательна.

Response: 200 OK

{
"id": "uuid",
"email": "denis@example.com",
"role": "USER",
"createdAt": "2026-11-18T10:00:00Z"
}

Ошибки:
- 401 — отсутствует или недействителен JWT.

## 4. Изменение профиля

PATCH /users/me

Авторизация: обязательна.

Request:

{
"email": "new-email@example.com"
}

Response: 200 OK

{
"id": "uuid",
"email": "new-email@example.com"
}

Ошибки:
- 400 — некорректные данные.
- 401 — пользователь не авторизован.
- 409 — email уже занят.

## Общие правила

- Пароли никогда не возвращаются в API.
- Идентификатор пользователя берётся из JWT.
- Пользователь не может изменять чужой профиль.
- Роль USER назначается автоматически.