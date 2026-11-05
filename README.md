# autoline-parser

Поиск новых компаний-дилеров на autoline.info и уведомления (Telegram, email).
Spring Boot (без web), PostgreSQL из проекта `infra-postgres`, Flyway-миграции.

## Переменные окружения

### База данных (роли и пароли — из infra-postgres/.env)

| Переменная | Значение по умолчанию | Описание |
|---|---|---|
| `DB_HOST` | `localhost` | хост Postgres (в docker-сети — `postgres`) |
| `DB_PORT` | `5432` | порт Postgres |
| `DB_NAME` | `autoline` | имя БД |
| `DB_APP_USER` | `autoline_app` | рантайм-пользователь (DML) |
| `DB_APP_PASSWORD` | — | пароль `autoline_app` (=`AUTOLINE_APP_PASSWORD` в infra-postgres) |
| `DB_MIG_USER` | `autoline_mig` | пользователь Flyway (DDL, владелец БД) |
| `DB_MIG_PASSWORD` | — | пароль `autoline_mig` (=`AUTOLINE_MIG_PASSWORD` в infra-postgres) |

### Уведомления

| Переменная | Описание |
|---|---|
| `TELEGRAM_BOT_TOKEN` | токен бота; **не задан — бот не поднимается** (локально так и запускаем) |
| `TELEGRAM_ADMIN_ID` | id админа (пока для приветствия при старте) |
| `TELEGRAM_ALLOWED_IDS` | id разрешенных пользователей бота (позже заменит таблица `users`) |
| `MAIL_TOKEN`, `MAIL_LOGIN`, `MAIL_SENDER` | почта Яндекса (отправка выключена `app.mail-send-enabled=false`) |

### Служебные

| Переменная | По умолчанию | Описание |
|---|---|---|
| `APP_SCHEDULER_ENABLED` | `true` | выключить расписания сканирований |
| `APP_STARTUP_SCAN_ENABLED` | `false` | выполнить один FULL-скан сразу при старте; прокидывается в контейнер через compose (на проде — та же переменная в `/opt/autoline/.env`) |

## Локальный запуск

Отдельный профиль не нужен: локальный запуск отличается от прода только ENV.

1. Поднять PostgreSQL: в проекте `infra-postgres` выполнить `docker compose up -d`
   (создаст БД `autoline` и роли `autoline_mig`/`autoline_app`).
2. Экспортировать пароли из `infra-postgres/.env`:
   ```
   export DB_APP_PASSWORD=<AUTOLINE_APP_PASSWORD>
   export DB_MIG_PASSWORD=<AUTOLINE_MIG_PASSWORD>
   ```
3. Запуск: без расписаний, без бота (токен не задан), один FULL-скан при старте:
   ```
   APP_SCHEDULER_ENABLED=false APP_STARTUP_SCAN_ENABLED=true ./gradlew bootRun
   ```
   Если БД пустая, скан выполнит базовое наполнение (все компании сохранятся,
   уведомления не отправляются). Повторные запуски шлют уведомления только
   при наличии новых компаний — почта выключена, бот не задан, т.е. видно в логах.

## Первый запуск на проде

После деплоя первый FULL-скан (07:00/19:00 по Минску) наполнит базовое
наполнение без уведомлений; китайские сканы до этого пропускаются.
