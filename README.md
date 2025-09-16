# Учебный проект
поиск объявлений на сайте и формирование отчета с отправкой email


## Для запуска приложения необходимо объявить переменные окружения:

MAIL_TOKEN - токен доступа к почте

MAIL_LOGIN - логин от почты

MAIL_SENDER - email от имени которого отправляется письмо.


Локальный запуск с профилем:
./gradlew bootRun -Dspring.pro files.active=local

необходимо создать application-local.properties
в нем перечислить получателей письма:

app.emails[0] = example1@example.com

app.emails[1] = example2@example.com

и т.п.