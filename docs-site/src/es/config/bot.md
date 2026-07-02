---
title: Configuración de Bot y Base de Datos (config.yml)
description: Configuración principal del plugin — token del bot, base de datos, idioma, debug.
---

# config.yml

El archivo de configuración principal. Controla la conexión del bot, tipo de base de datos, idioma y modo debug.

## bot

```yaml
bot:
  token: "${DISCORD_BOT_TOKEN}"
  guild_id: "508152650978820096"
  status_rotation_minutes: 15
  game_status:
    - "🎮 EligiusMC"
    - "🟢 {online} jugadores"
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `token` | String | Token del bot de Discord. Usa `${ENV_VAR}` para variables de entorno |
| `guild_id` | String | ID del servidor (guild) de Discord |
| `status_rotation_minutes` | Integer | Cada cuánto el bot rota entre las entradas de `game_status` |
| `game_status` | List | Mensajes de estado mostrados en el perfil del bot. `{online}` se resuelve al número actual de jugadores |

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

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `type` | String | `sqlite` o `mysql` |
| `mysql.*` | — | Configuración de conexión MySQL (solo se usa cuando `type: mysql`) |
| `mysql.pool_size` | Integer | Tamaño del pool de conexiones HikariCP |

## language

```yaml
language: "en"
```

Idioma por defecto para jugadores que no han establecido una preferencia. Disponibles: `en`, `es`, `fr`, `de`, `pt`, `ru`.

## debug

```yaml
debug: false
```

Cuando está activado, imprime información adicional de diagnóstico en la consola.
