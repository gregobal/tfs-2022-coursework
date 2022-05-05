# Площадка для организаторов мероприятий

## MVP (предварительно)

- Сообщество (организации или группы по интересам)
- Мероприятие (порождаются сообществами как конкретный элемент в разрезе календарной шкалы)
- Пользователь (подписываются на сообщества, участвуют в мероприятиях, администрируют сообщества)
- Отзыв (порождаются пользователями на мероприятия)
- Уведомление (рассылка уведомлений о новом мероприятии в сообществе пользователям подписчикам)
- Тег (поиск сообществ)

## Технологический стек 

- Scala
- ZIO
- Tapir
- Slick
- PostgreSQL

## Roadmap

- Определение MVP
- Схемы хранения данных и модели 
- Доступ к данным через DAO слой
- Public API (http server)
- Сервис рассылки уведомлений (набросы от паблика через GRPC??)
- Поиск
