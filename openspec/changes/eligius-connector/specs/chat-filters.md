# Spec: Chat Filters

## Overview

Sistema de filtros de chat con punición escalada.

## Requirements

### Functional
1. Múltiples filtros configurables
2. Acciones por filtro: block, delete, warn, mute, tempban, banip
3. Escalada por nivel (repeat count)
4. Duración configurable por nivel
5. Whitelist de usuarios que bypass
6. Log de filtrados

### Non-Functional
- Async execution
- Rate limit: 100 mensajes por minuto por usuario
- Logging completo

## Technical Design

### Configuration
```yaml
Filters:
  Spam:
    Enabled: true
    MaxMessagesPerMinute: 10
    Punishment:
      Enabled: true
      Levels:
        1: "mute:60"
        2: "mute:300"
        3: "tempban:3600"
        4: "banip"
  
  Profanity:
    Enabled: true
    Words: ["word1", "word2"]
    Punishment:
      Enabled: true
      Levels:
        1: "warn"
        2: "mute:300"
        3: "tempban:7200"
  
  Links:
    Enabled: true
    Domains: ["youtube.com", "twitch.tv"]
    Punishment:
      Enabled: true
      Levels:
        1: "delete"
        2: "warn"
        3: "mute:300"
  
  Caps:
    Enabled: true
    MaxPercentage: 70
    Punishment:
      Enabled: true
      Levels:
        1: "delete"
        2: "warn"
```

### Commands
- `/filter add <word>` — Agregar palabra (admin)
- `/filter remove <word>` — Remover palabra (admin)
- `/filter list` — Listar filtros (admin)

### Permissions
- `connector.chat.filter.bypass` — default: op

## Testing
- Unit: Detección de filtros, escalada
- Integration: Mensaje filtrado, punición aplicada

## Acceptance Criteria
- [ ] Filtros detectan contenido
- [ ] Escalada funciona
- [ ] Punición se aplica
- [ ] Bypass funciona para admins
