---
title: Server Status Configuration (status.yml)
description: Online/offline status embeds posted on server start and stop.
---

# status.yml

Posts an embed to a Discord channel when the server starts and stops. On startup, the previous online embed is deleted and a fresh one is posted. On shutdown, all bot messages are cleared and an offline embed is posted synchronously.

## ⚠️ Shared channel with counters.yml

The `channel` in this file **must match** the `server_status` counter channel in `counters.yml`. The counter renames the channel (e.g. `🟢 Online` / `🔴 Offline`), and this file posts the status embed. Do not modify one without checking the other.

## channel

```yaml
channel: "1521276573033173204"
```

Discord channel ID used for both the status embed and the `server_status` counter.

## online

```yaml
online:
  title: "🟢 Server Online"
  description: "The Minecraft server is now **online** and ready to play!"
  color: 5763719
  thumbnail: "https://minotar.net/avatar/server/128"
  author: "EligiusMC Status"
  author_icon: "https://minotar.net/avatar/EligiusMC/128"
  footer: "Server started"
  timestamp: true
  fields:
    - name: "📋 Server"
      value: "EligiusMC"
      inline: true
    - name: "🎮 Gamemode"
      value: "Survival"
      inline: true
    - name: "🌍 World"
      value: "Overworld"
      inline: true
    - name: "📢 Info"
      value: "Join us and start your adventure!"
      inline: false
```

| Field | Type | Description |
|-------|------|-------------|
| `title` | String | Embed title |
| `description` | String | Embed description (Markdown) |
| `color` | Integer | Embed color (decimal) |
| `thumbnail` | String | Thumbnail image URL |
| `author` | String | Author name |
| `author_icon` | String | Author icon URL |
| `footer` | String | Footer text |
| `timestamp` | Boolean | Show timestamp |
| `fields` | List | Inline embed fields |

## offline

```yaml
offline:
  title: "🔴 Server Offline"
  description: "The Minecraft server is currently **offline** for maintenance."
  color: 15548997
  thumbnail: "https://minotar.net/avatar/server/128"
  author: "EligiusMC Status"
  author_icon: "https://minotar.net/avatar/EligiusMC/128"
  footer: "We'll be back soon"
  timestamp: true
  fields:
    - name: "📋 Status"
      value: "Under maintenance"
      inline: true
    - name: "⏰ ETA"
      value: "Shortly"
      inline: true
```

Same structure as `online`. The offline embed is sent synchronously during `onDisable` to guarantee delivery before the JDA client shuts down.
