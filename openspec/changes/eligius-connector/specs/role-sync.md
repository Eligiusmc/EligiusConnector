# Spec: Role Sync

## Overview

Sincronización bidireccional de roles entre Discord y Minecraft (LuckPerms).

## Requirements

### Functional
1. Sincronización Discord → MC (ciclo cada X minutos)
2. Sincronización MC → Discord (opcional)
3. Solo roles específicos listados en config
4. On-link sync automático
5. Soporte para grupos de LuckPerms
6. Multi-server via Redis

### Non-Functional
- Async execution
- Rate limit: 1 sync por minuto por jugador
- Fallback a LuckPerms si Vault no disponible

## Technical Design

### Configuration
```yaml
GroupRoleSynchronization:
  Enabled: true
  MinecraftIsAuthoritative: true
  OneWay: false
  PrimaryGroupOnly: false
  OnLink: true
  CycleTime: 5
  CycleCompletely: false

  GroupsAndRolesToSync:
    "123456789012345681": "admin"      # LuckPerms group
    "123456789012345682": "mod"
    "123456789012345683": "vip"
    "123456789012345684": "default"
```

### Commands
- `/roles sync` (Discord) — Forzar sync (admin)
- `/roles sync <player>` (Discord) — Sync específico

### Permissions
- `connector.roles.sync` — default: op

## Testing
- Unit: Mapeo de roles, validación
- Integration: Sync entre Discord y LuckPerms

## Acceptance Criteria
- [ ] Roles se sincronizan automáticamente
- [ ] Solo roles listados se sincronizan
- [ ] On-link funciona
- [ ] Multi-server funciona via Redis
