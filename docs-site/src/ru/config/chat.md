---
title: Конфигурация чат-моста (chat.yml)
description: Настройте мост чата Discord ↔ Minecraft, вебхуки и фильтры.
---

# chat.yml

Управляет двусторонним чат-мостом, внешним видом вебхуков и фильтрами защиты от злоупотреблений.

## channel

```yaml
channel: "1521272219223851028"
```

Канал Discord, где появляются сообщения MC ↔ DC и куда `/chat` отправляет сообщения.

## bridge

```yaml
bridge:
  mc_to_discord: true
  discord_to_mc: true
```

| Поле | Описание |
|------|----------|
| `mc_to_discord` | Отправляет чат Minecraft в Discord |
| `discord_to_mc` | Отправляет сообщения Discord в чат Minecraft |

## format

```yaml
format:
  mc_to_discord: "{player}: {message}"
  discord_to_mc: "[DC] {player}: {message}"
```

| Плейсхолдер | Заменяется на |
|-------------|---------------|
| `{player}` | Имя игрока |
| `{message}` | Содержимое сообщения чата |

## webhook

```yaml
webhook:
  enabled: true
  name: "Minecraft"
  avatar: "https://minotar.net/avatar/%player_name%/128"
```

Использует вебхуки Discord для сообщений MC → DC, отображая головы и ники игроков.

## filter

```yaml
filter:
  enabled: true
```

Главный выключатель для отключения всех фильтров сразу.

### filter.spam

| Поле | По умолчанию | Описание |
|------|-------------|----------|
| `enabled` | `true` | Включает анти-спам |
| `max_messages` | `5` | Разрешённые сообщения в окне времени |
| `time_window_seconds` | `10` | Временное окно в секундах |

### filter.profanity

| Поле | Описание |
|------|----------|
| `enabled` | Включает фильтр ненормативной лексики |
| `words` | Список заблокированных слов (без учёта регистра) |

### filter.links

| Поле | Описание |
|------|----------|
| `enabled` | Включает фильтр ссылок |
| `mode` | `whitelist` (разрешены только перечисленные домены) или `blacklist` (перечисленные домены заблокированы) |
| `domains` | Список доменов для выбранного режима |

### filter.caps

| Поле | По умолчанию | Описание |
|------|-------------|----------|
| `enabled` | `true` | Включает фильтр заглавных букв |
| `max_percentage` | `70` | Максимальный процент заглавных символов |
| `min_length` | `10` | Минимальная длина сообщения для проверки |

### filter.punishment

```yaml
punishment:
  enabled: true
  thresholds:
    2: "mute:300"
    5: "tempban:3600"
    10: "banip"
```

Нарастающие наказания для повторных нарушителей. Действия: `warn`, `delete`, `mute:<секунды>`, `tempban:<секунды>`, `banip`.
