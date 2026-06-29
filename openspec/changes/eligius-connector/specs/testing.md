# Spec: Testing

## Overview

Estrategia de testing del plugin.

## Requirements

### Functional
1. Unit tests (JUnit 5 + Mockito)
2. Integration tests
3. Mock tests
4. 90% coverage

### Non-Functional
- Tests ejecutan en < 5 minutos
- Tests no dependen de red
- Tests paralelos

## Technical Design

### Directory Structure
```
src/test/java/com/eligiusmc/connector/
├── domain/
│   ├── account/
│   │   └── AccountLinkingTest.java
│   ├── chat/
│   │   └── ChatBridgeTest.java
│   ├── console/
│   │   └── ConsoleRemoteTest.java
│   ├── roles/
│   │   └── RoleSyncTest.java
│   ├── queries/
│   │   └── PlayerQueriesTest.java
│   ├── events/
│   │   └── CustomEventsTest.java
│   ├── notifications/
│   │   └── NotificationsTest.java
│   ├── filters/
│   │   └── ChatFiltersTest.java
│   ├── audit/
│   │   └── AuditLogTest.java
│   └── placeholders/
│       └── PlaceholderAPITest.java
├── infrastructure/
│   ├── database/
│   │   └── DatabaseTest.java
│   └── discord/
│       └── DiscordTest.java
└── mock/
    ├── MockPlayer.java
    ├── MockServer.java
    └── MockJDA.java
```

### Coverage
| Module | Coverage |
|--------|----------|
| Account Linking | 95% |
| Chat Bridge | 90% |
| Console Remote | 90% |
| Role Sync | 90% |
| Player Queries | 85% |
| Custom Events | 90% |
| Notifications | 85% |
| Chat Filters | 95% |
| Audit Log | 90% |
| PlaceholderAPI | 90% |

### Commands
- `./gradlew test` — Ejecutar tests
- `./gradlew test jacocoTestReport` — Generar reporte

## Testing
- Unit: Lógica de dominio
- Integration: API de Paper
- Mock: JDA, PacketEvents, LuckPerms

## Acceptance Criteria
- [ ] 90% coverage
- [ ] Todos los tests pasan
- [ ] Tests ejecutan en < 5 minutos
- [ ] Tests no dependen de red
