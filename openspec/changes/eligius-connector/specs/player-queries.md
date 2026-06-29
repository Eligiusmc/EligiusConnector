# Spec: Player Queries

## Overview

Sistema de consultas de información de jugadores desde Discord.

## Requirements

### Functional
1. Ver perfil de jugador (`/player info <name>`)
2. Ver inventario virtual (`/player inventory <name>`)
3. Ver ubicación (`/player location <name>`)
4. Ver roles (`/player roles <name>`)
5. Ver homes (`/player homes <name>`)
6. Ver jobs (`/player jobs <name>`)
7. Ver clan (`/player clan <name>`)
8. Soporte para PlaceholderAPI

### Non-Functional
- Rate limit: 5 consultas por minuto por usuario
- Output en embeds de Discord
- Datos en tiempo real

## Technical Design

### Commands
- `/player info <name>` — Perfil general
- `/player inventory <name>` — Inventario virtual
- `/player location <name>` — Ubicación
- `/player roles <name>` — Roles
- `/player homes <name>` — Homes
- `/player jobs <name>` — Jobs
- `/player clan <name>` — Clan

### Permissions
- `connector.profile.self` — default: true
- `connector.profile.other` — default: op
- `connector.inventory.self` — default: true
- `connector.inventory.other` — default: op
- `connector.location.self` — default: true
- `connector.location.other` — default: op
- `connector.roles.self` — default: true
- `connector.homes.self` — default: true
- `connector.homes.other` — default: op
- `connector.jobs.self` — default: true
- `connector.jobs.other` — default: op

## Testing
- Unit: Formateo de datos, validación
- Integration: Consulta de datos reales

## Acceptance Criteria
- [ ] Info de jugador se muestra
- [ ] Inventario virtual funciona
- [ ] Ubicación se muestra
- [ ] Roles se muestran
- [ ] Homes se muestran
- [ ] Jobs se muestran
- [ ] Clan se muestra
