---
title: Chat Bridge Configuration (chat.yml)
description: Configure the Discord ↔ Minecraft chat bridge, webhooks, and filters.
---

# chat.yml

Controls the bidirectional chat bridge, webhook appearance, and anti-abuse filters.

## channel

```yaml
channel: "1521272219223851028"
```

The Discord channel where MC ↔ DC messages appear and where `/chat` sends messages.

## bridge

```yaml
bridge:
  mc_to_discord: true
  discord_to_mc: true
```

| Field | Description |
|-------|-------------|
| `mc_to_discord` | Send Minecraft chat to Discord |
| `discord_to_mc` | Send Discord messages to Minecraft chat |

## format

```yaml
format:
  mc_to_discord: "{player}: {message}"
  discord_to_mc: "[DC] {player}: {message}"
```

| Placeholder | Resolves to |
|-------------|-------------|
| `{player}` | Player name |
| `{message}` | Chat message content |

## webhook

```yaml
webhook:
  enabled: true
  name: "Minecraft"
  avatar: "https://minotar.net/avatar/%player_name%/128"
```

Uses Discord webhooks for MC → DC messages, showing player heads and nicknames.

## filter

```yaml
filter:
  enabled: true
```

Master switch to disable all filters at once.

### filter.spam

| Field | Default | Description |
|-------|---------|-------------|
| `enabled` | `true` | Enable anti-spam |
| `max_messages` | `5` | Messages allowed in the time window |
| `time_window_seconds` | `10` | Time window in seconds |

### filter.profanity

| Field | Description |
|-------|-------------|
| `enabled` | Enable profanity filter |
| `words` | List of blocked words (case-insensitive) |

### filter.links

| Field | Description |
|-------|-------------|
| `enabled` | Enable link filter |
| `mode` | `whitelist` (only listed domains allowed) or `blacklist` (listed domains blocked) |
| `domains` | Domain list for the selected mode |

### filter.caps

| Field | Default | Description |
|-------|---------|-------------|
| `enabled` | `true` | Enable caps filter |
| `max_percentage` | `70` | Maximum % of uppercase characters |
| `min_length` | `10` | Minimum message length to check |

### filter.punishment

```yaml
punishment:
  enabled: true
  thresholds:
    2: "mute:300"
    5: "tempban:3600"
    10: "banip"
```

Escalating punishments for repeat offenders. Actions: `warn`, `delete`, `mute:<seconds>`, `tempban:<seconds>`, `banip`.
