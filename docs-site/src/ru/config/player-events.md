---
title: Конфигурация событий игроков (player_events.yml)
description: Настройте Discord-эмбеды для входов, выходов, смертей и достижений.
---

# player_events.yml

Отправляет Discord-эмбеды для событий игроков Minecraft. Каждое событие включается независимо — установите `enabled: false` для любого события, чтобы отключить только его. Удаление этого файла отключает все события.

## Плейсхолдеры

| Плейсхолдер | События | Заменяется на |
|-------------|--------|---------------|
| `{player}` | Все | Имя игрока |
| `{online}` | join, leave | Текущее количество игроков онлайн |
| `{max}` | join, leave | Максимальные слоты сервера |
| `{first_join}` | join | `"Yes"` или `"No"` |
| `{death_message}` | death | Исходное сообщение о смерти |
| `{health}` | death | Здоровье игрока |
| `{food}` | death | Уровень сытости игрока |
| `{world}` | death | Название мира |
| `{advancement}` | advancement | Название достижения |

## join

```yaml
join:
  enabled: true
  channel: "1521276575889358960"
  embed:
    color: 5763719
    author: "{player} зашёл на сервер"
    author_icon: "https://minotar.net/avatar/{player}/128"
    thumbnail: "https://minotar.net/avatar/{player}/128"
    title: "Добро пожаловать на EligiusMC!"
    description: "Все поприветствуйте **{player}**!"
    footer: "EligiusMC"
    timestamp: true
    fields:
      - name: "Онлайн"
        value: "**{online}** / **{max}**"
        inline: true
      - name: "Впервые"
        value: "{first_join}"
        inline: true
```

| Поле | Тип | Описание |
|------|-----|----------|
| `enabled` | Булево | Включает эмбеды входа |
| `channel` | Строка | ID канала Discord |
| `embed.color` | Целое | Цвет эмбеда (десятичное) |
| `embed.author` | Строка | Строка автора (поддерживает плейсхолдеры) |
| `embed.author_icon` | Строка | URL иконки автора |
| `embed.thumbnail` | Строка | URL миниатюры |
| `embed.title` | Строка | Заголовок эмбеда |
| `embed.description` | Строка | Описание эмбеда (Markdown) |
| `embed.footer` | Строка | Текст нижнего колонтитула |
| `embed.timestamp` | Булево | Показывать временную метку |
| `embed.fields` | Список | Дополнительные поля (`name`, `value`, `inline`) |

## leave

Та же структура, что и join. По умолчанию прощальный эмбед с головой игрока и количеством онлайн.

| Поле | Цвет по умолчанию |
|------|------------------|
| `color` | `15548997` (красный) |
| `author` | `"{player} покинул сервер"` |

## death

```yaml
death:
  enabled: true
  channel: "1521276578708066578"
  embed:
    color: 15548997
    author: "{player} погиб"
    title: "{death_message}"
    fields:
      - name: "Здоровье"
        value: "❤️ {health} HP"
        inline: true
      - name: "Голод"
        value: "🍖 {food} / 20"
        inline: true
      - name: "Мир"
        value: "🌍 {world}"
        inline: true
```

`{death_message}` отображает исходное сообщение о смерти из Minecraft. Дополнительные поля показывают здоровье, голод и мир.

## advancement

```yaml
advancement:
  enabled: true
  channel: "1521276581522444350"
  embed:
    color: 16766720
    author: "Достижение получено!"
    title: "{advancement}"
    description: "**{player}** получил это достижение!"
```

`{advancement}` заменяется на название достижения.
