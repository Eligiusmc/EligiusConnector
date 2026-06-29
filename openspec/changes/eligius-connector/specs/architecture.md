# Spec: Architecture

## Overview

Arquitectura hexagonal del plugin EligiusConnector.

## Requirements

### Functional
1. Hexagonal Architecture (Ports & Adapters)
2. Separación de dominio de infraestructura
3. Inyección de dependencias manual
4. Multi-server via Redis

### Non-Functional
- Thread-safe para Folia
- Async I/O
- Zero-lag

## Technical Design

### Directory Structure
```
src/main/java/com/eligiusmc/connector/
├── domain/
│   ├── account/        # Vinculación de cuentas
│   ├── chat/           # Chat compartido
│   ├── console/        # Control de consola
│   ├── roles/          # Sincronización de roles
│   ├── queries/        # Consultas de jugadores
│   ├── events/         # Sistema de eventos personalizados
│   ├── notifications/  # Notificaciones MC
│   ├── filters/        # Filtros de chat
│   ├── audit/          # Log de auditoría
│   ├── placeholders/   # PlaceholderAPI integration
│   └── config/         # Gestión de configuración
├── infrastructure/
│   ├── discord/        # Adaptador JDA
│   ├── database/       # SQLite, MySQL, Redis
│   ├── minecraft/      # Adaptador Paper/Bukkit
│   ├── packets/        # Adaptador PacketEvents
│   ├── luckperms/      # Adaptador LuckPerms
│   └── placeholderapi/ # Adaptador PAPI
└── ports/
    ├── in/             # Command handlers, event listeners
    └── out/            # Database, Discord API, LuckPerms, PAPI
```

### Dependencies
| Dependency | Type | Usage |
|------------|------|-------|
| JDA | Required | Discord API |
| PacketEvents | Required | Inventory virtual, packets |
| LuckPerms | Required | Permissions (role sync) |
| PlaceholderAPI | Soft | Placeholders in config |
| Redis | No | Multi-server sync |
| PlotProtectionStones | No | Integration homes |
| AdvancedJobs | No | Integration jobs |

## Testing
- Unit: Architecture validation
- Integration: Full stack

## Acceptance Criteria
- [ ] Hexagonal architecture implemented
- [ ] Domain separated from infrastructure
- [ ] Dependencies injected manually
- [ ] Multi-server works via Redis
