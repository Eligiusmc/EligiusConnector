# Spec: Account Linking

## Overview

Sistema de vinculación de cuentas entre Discord y Minecraft.

## Requirements

### Functional
1. Bot genera código alfanumérico en Discord (`/verify`)
2. Jugador ingresa código en MC (`/verify <code>`)
3. Vinculación almacenada en DB (SQLite/MySQL/Redis)
4. 1 Discord = 1 MC, 1 MC = 1 Discord
5. Desvinculación solo por admins (`/unlink`)
6. Re-vinculación permitida después de desvincular

### Non-Functional
- Código expira en 5 minutos
- Rate limit: 3 intentos por minuto por usuario
- Cifrado de datos sensibles en DB

## Technical Design

### Data Model
```
accounts:
  discord_id: BIGINT PRIMARY KEY
  minecraft_uuid: UUID UNIQUE
  minecraft_name: VARCHAR(16)
  linked_at: TIMESTAMP
  linked_by: VARCHAR(16)
```

### Flow
```
1. Discord: /verify → Bot genera código, envía DM
2. MC: /verify ABC123 → Plugin valida código
3. Plugin almacena vinculación en DB
4. Plugin notifica éxito en ambos canales
```

### Commands
- `/verify` (Discord) — Generar código
- `/verify <code>` (MC) — Vincular cuenta
- `/unlink` (MC) — Desvincular (admin only)
- `/unlink <player>` (MC) — Desvincular otro (admin)

### Permissions
- `connector.verify` — default: true
- `connector.unlink.self` — default: true
- `connector.unlink.other` — default: op

## Testing
- Unit: Validación de código, rate limiting
- Integration: Flujo completo MC ↔ Discord

## Acceptance Criteria
- [ ] Código se genera en Discord
- [ ] Vinculación funciona en MC
- [ ] Unlink funciona para admins
- [ ] Rate limiting funciona
- [ ] Código expira después de 5 minutos
