---
title: Configuración del Chat Bridge (chat.yml)
description: Configura el bridge de chat Discord ↔ Minecraft, webhooks y filtros.
---

# chat.yml

Controla el bridge de chat bidireccional, la apariencia de los webhooks y los filtros anti-abuso.

## channel

```yaml
channel: "1521272219223851028"
```

El canal de Discord donde aparecen los mensajes MC ↔ DC y donde `/chat` envía mensajes.

## bridge

```yaml
bridge:
  mc_to_discord: true
  discord_to_mc: true
```

| Campo | Descripción |
|-------|-------------|
| `mc_to_discord` | Enviar chat de Minecraft a Discord |
| `discord_to_mc` | Enviar mensajes de Discord al chat de Minecraft |

## format

```yaml
format:
  mc_to_discord: "{player}: {message}"
  discord_to_mc: "[DC] {player}: {message}"
```

| Placeholder | Se resuelve a |
|-------------|---------------|
| `{player}` | Nombre del jugador |
| `{message}` | Contenido del mensaje de chat |

## webhook

```yaml
webhook:
  enabled: true
  name: "Minecraft"
  avatar: "https://minotar.net/avatar/%player_name%/128"
```

Usa webhooks de Discord para los mensajes MC → DC, mostrando las cabezas y apodos de los jugadores.

## filter

```yaml
filter:
  enabled: true
```

Interruptor maestro para desactivar todos los filtros a la vez.

### filter.spam

| Campo | Por defecto | Descripción |
|-------|-------------|-------------|
| `enabled` | `true` | Activar anti-spam |
| `max_messages` | `5` | Mensajes permitidos en la ventana de tiempo |
| `time_window_seconds` | `10` | Ventana de tiempo en segundos |

### filter.profanity

| Campo | Descripción |
|-------|-------------|
| `enabled` | Activar filtro de groserías |
| `words` | Lista de palabras bloqueadas (sin distinción de mayúsculas) |

### filter.links

| Campo | Descripción |
|-------|-------------|
| `enabled` | Activar filtro de enlaces |
| `mode` | `whitelist` (solo dominios listados permitidos) o `blacklist` (dominios listados bloqueados) |
| `domains` | Lista de dominios para el modo seleccionado |

### filter.caps

| Campo | Por defecto | Descripción |
|-------|-------------|-------------|
| `enabled` | `true` | Activar filtro de mayúsculas |
| `max_percentage` | `70` | Porcentaje máximo de caracteres en mayúscula |
| `min_length` | `10` | Longitud mínima del mensaje para verificar |

### filter.punishment

```yaml
punishment:
  enabled: true
  thresholds:
    2: "mute:300"
    5: "tempban:3600"
    10: "banip"
```

Castigos escalonados para reincidentes. Acciones: `warn`, `delete`, `mute:<seconds>`, `tempban:<seconds>`, `banip`.
