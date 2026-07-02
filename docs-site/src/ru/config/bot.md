---
title: Конфигурация бота и базы данных (config.yml)
description: Основная конфигурация плагина — токен бота, база данных, язык, отладка.
---

# config.yml

Основной файл конфигурации. Управляет подключением бота, типом базы данных, языком и режимом отладки.

## bot

```yaml
bot:
  token: "${DISCORD_BOT_TOKEN}"
  guild_id: "508152650978820096"
  status_rotation_minutes: 15
  game_status:
    - "🎮 EligiusMC"
    - "🟢 {online} игроков"
```

| Поле | Тип | Описание |
|------|-----|----------|
| `token` | Строка | Токен Discord-бота. Используйте `${ENV_VAR}` для переменных окружения |
| `guild_id` | Строка | ID сервера Discord (guild) |
| `status_rotation_minutes` | Целое | Как часто бот переключается между записями `game_status` |
| `game_status` | Список | Статусные сообщения в профиле бота. `{online}` заменяется на текущее количество игроков |

## database

```yaml
database:
  type: "sqlite"
  mysql:
    host: "localhost"
    port: 3306
    name: "eligiusconnector"
    username: "root"
    password: ""
    pool_size: 10
```

| Поле | Тип | Описание |
|------|-----|----------|
| `type` | Строка | `sqlite` или `mysql` |
| `mysql.*` | — | Настройки подключения MySQL (используются только при `type: mysql`) |
| `mysql.pool_size` | Целое | Размер пула соединений HikariCP |

## language

```yaml
language: "ru"
```

Язык по умолчанию для игроков, не указавших предпочтение. Доступны: `en`, `es`, `fr`, `de`, `pt`, `ru`.

## debug

```yaml
debug: false
```

При включении выводит дополнительную диагностическую информацию в консоль.
