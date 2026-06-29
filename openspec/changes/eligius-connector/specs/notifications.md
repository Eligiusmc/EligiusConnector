# Spec: Notifications

## Overview

Sistema de notificaciones configurables para eventos del servidor.

## Requirements

### Functional
1. Notificaciones por evento (join, leave, death, etc.)
2. Canal de Discord por evento
3. Formato configurable con PlaceholderAPI
4. Display en MC (chat, actionbar, title)
5. Configurable por evento
6. On-start, during, on-end para eventos personalizados

### Non-Functional
- Async execution
- Rate limit: 1 notificación por segundo por evento
- Logging de notificaciones

## Technical Design

### Configuration
```yaml
Events:
  PlayerJoin:
    Enabled: true
    ChannelId: "123456789012345685"
    Message: ":arrow_right: **%player_displayname%** se ha conectado.\n:earth_africa: Mundo: %player_world%\n:busts_in_silhouette: Online: %playerlist_online%"
    MinecraftDisplay: "chat"
  
  PlayerLeave:
    Enabled: true
    ChannelId: "123456789012345685"
    Message: ":arrow_left: **%player_displayname%** se ha desconectado.\n:clock1: Tiempo jugado: %player_time_played%"
    MinecraftDisplay: "chat"
  
  PlayerDeath:
    Enabled: true
    ChannelId: "123456789012345686"
    Message: ":skull: **%player_displayname%** ha muerto.\n:droplet: Vida: %player_health%\n:bread: Comida: %player_food%"
    MinecraftDisplay: "chat"
```

### Commands
- None (notifications are automatic)

### Permissions
- `connector.notifications.receive` — default: true

## Testing
- Unit: Formateo de mensajes, validación
- Integration: Notificación se envía al evento

## Acceptance Criteria
- [ ] Notificaciones se envían al ocurrir eventos
- [ ] Formato configurable funciona
- [ ] Display en MC funciona
- [ ] Canales configurados funcionan
