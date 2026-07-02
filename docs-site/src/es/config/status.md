---
title: Configuración de Estado del Servidor (status.yml)
description: Embeds de estado online/offline publicados al iniciar y detener el servidor.
---

# status.yml

Publica un embed en un canal de Discord cuando el servidor inicia y se detiene. Al iniciar, el embed online anterior se elimina y se publica uno nuevo. Al apagar, todos los mensajes del bot se limpian y se publica un embed offline de forma síncrona.

## ⚠️ Canal compartido con counters.yml

El `channel` en este archivo **debe coincidir** con el canal del counter `server_status` en `counters.yml`. El counter renombra el canal (ej. `🟢 Online` / `🔴 Offline`), y este archivo publica el embed de estado. No modifiques uno sin revisar el otro.

## channel

```yaml
channel: "1521276573033173204"
```

ID del canal de Discord usado tanto para el embed de estado como para el counter `server_status`.

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

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `title` | String | Título del embed |
| `description` | String | Descripción del embed (Markdown) |
| `color` | Integer | Color del embed (decimal) |
| `thumbnail` | String | URL de la imagen miniatura |
| `author` | String | Nombre del autor |
| `author_icon` | String | URL del icono del autor |
| `footer` | String | Texto del footer |
| `timestamp` | Boolean | Mostrar timestamp |
| `fields` | List | Campos inline del embed |

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

Misma estructura que `online`. El embed offline se envía de forma síncrona durante `onDisable` para garantizar la entrega antes de que el cliente JDA se apague.
