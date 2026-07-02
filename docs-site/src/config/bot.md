---
title: Bot & Database Configuration (config.yml)
description: Main plugin configuration — bot token, database, language, debug.
---

# config.yml

The main configuration file. Controls the bot connection, database type, language, and debug mode.

## bot

```yaml
bot:
  token: "${DISCORD_BOT_TOKEN}"
  guild_id: "508152650978820096"
  status_rotation_minutes: 15
  game_status:
    - "🎮 EligiusMC"
    - "🟢 {online} players"
```

| Field | Type | Description |
|-------|------|-------------|
| `token` | String | Discord bot token. Use `${ENV_VAR}` for environment variables |
| `guild_id` | String | Discord server (guild) ID |
| `status_rotation_minutes` | Integer | How often the bot cycles through `game_status` entries |
| `game_status` | List | Status messages shown on the bot's profile. `{online}` resolves to current player count |

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

| Field | Type | Description |
|-------|------|-------------|
| `type` | String | `sqlite` or `mysql` |
| `mysql.*` | — | MySQL connection settings (only used when `type: mysql`) |
| `mysql.pool_size` | Integer | HikariCP connection pool size |

## language

```yaml
language: "en"
```

Default language for players who haven't set a preference. Available: `en`, `es`, `fr`, `de`, `pt`, `ru`.

## debug

```yaml
debug: false
```

When enabled, prints additional diagnostic information to the console.
