# Spec: Chat Bridge

## Overview

Sistema de chat compartido entre Minecraft y Discord.

## Requirements

### Functional
1. Mensajes MC → Discord en canal configurado
2. Mensajes Discord → MC en chat del servidor
3. Formato configurable con PlaceholderAPI
4. Filtros activables/desactivables
5. Punición escalada por filtros
6. Soporte para webhooks (opcional)

### Non-Functional
- Async para no bloquear el server
- Rate limit por usuario
- Logging de mensajes para auditoría

## Technical Design

### Configuration
```yaml
MinecraftToDiscordFormat: "[MC] %player_displayname%: {message}"
DiscordToMinecraftFormat: "[DC] %username%: {message}"
```

### Filters
| Filter | Action | Escalation |
|--------|--------|------------|
| Spam | Block | Mute → TempBan → BanIP |
| Profanity | Block | Warn → Mute → TempBan |
| Links | Delete | Delete → Warn → Mute |
| Caps | Delete | Delete → Warn |

### Commands
- None (chat is automatic)

### Permissions
- `connector.chat.send` — default: true
- `connector.chat.filter.bypass` — default: op

## Testing
- Unit: Formateo de mensajes, filtros
- Integration: Mensaje MC → Discord, Discord → MC

## Acceptance Criteria
- [ ] Mensajes se envían en ambas direcciones
- [ ] Formato configurable funciona
- [ ] Filtros bloquean mensajes
- [ ] Escalada de punición funciona
