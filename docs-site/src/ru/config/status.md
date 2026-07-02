---
title: Конфигурация статуса сервера (status.yml)
description: Эмбеды статуса онлайн/офлайн, публикуемые при запуске и остановке сервера.
---

# status.yml

Публикует эмбед в Discord-канал при запуске и остановке сервера. При запуске предыдущий онлайн-эмбед удаляется и публикуется новый. При выключении все сообщения бота очищаются и синхронно публикуется офлайн-эмбед.

## ⚠️ Общий канал с counters.yml

`channel` в этом файле **должен совпадать** с каналом счётчика `server_status` в `counters.yml`. Счётчик переименовывает канал (например, `🟢 Online` / `🔴 Offline`), а этот файл публикует статусный эмбед. Не изменяйте один без проверки другого.

## channel

```yaml
channel: "1521276573033173204"
```

ID канала Discord, используемый как для статусного эмбеда, так и для счётчика `server_status`.

## online

```yaml
online:
  title: "🟢 Сервер онлайн"
  description: "Сервер Minecraft теперь **онлайн** и готов к игре!"
  color: 5763719
  thumbnail: "https://minotar.net/avatar/server/128"
  author: "Статус EligiusMC"
  author_icon: "https://minotar.net/avatar/EligiusMC/128"
  footer: "Сервер запущен"
  timestamp: true
  fields:
    - name: "📋 Сервер"
      value: "EligiusMC"
      inline: true
    - name: "🎮 Режим игры"
      value: "Выживание"
      inline: true
    - name: "🌍 Мир"
      value: "Overworld"
      inline: true
    - name: "📢 Инфо"
      value: "Присоединяйтесь и начните своё приключение!"
      inline: false
```

| Поле | Тип | Описание |
|------|-----|----------|
| `title` | Строка | Заголовок эмбеда |
| `description` | Строка | Описание эмбеда (Markdown) |
| `color` | Целое | Цвет эмбеда (десятичное) |
| `thumbnail` | Строка | URL миниатюры |
| `author` | Строка | Имя автора |
| `author_icon` | Строка | URL иконки автора |
| `footer` | Строка | Текст нижнего колонтитула |
| `timestamp` | Булево | Показать временную метку |
| `fields` | Список | Встроенные поля эмбеда |

## offline

```yaml
offline:
  title: "🔴 Сервер офлайн"
  description: "Сервер Minecraft сейчас **офлайн** на техническом обслуживании."
  color: 15548997
  thumbnail: "https://minotar.net/avatar/server/128"
  author: "Статус EligiusMC"
  author_icon: "https://minotar.net/avatar/EligiusMC/128"
  footer: "Мы скоро вернёмся"
  timestamp: true
  fields:
    - name: "📋 Статус"
      value: "На обслуживании"
      inline: true
    - name: "⏰ ETA"
      value: "Скоро"
      inline: true
```

Та же структура, что и `online`. Офлайн-эмбед отправляется синхронно во время `onDisable`, чтобы гарантировать доставку до завершения работы клиента JDA.
