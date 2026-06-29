# Spec: Audit Log

## Overview

Sistema de logging de auditoría para todas las acciones.

## Requirements

### Functional
1. Log de todas las acciones del plugin
2. Formato timestamp + action + user + details
3. Almacenamiento en DB (SQLite/MySQL)
4. Exportable a CSV
5. Configurable level de logging

### Non-Functional
- Async writing
- Rate limit: 1000 registros por minuto
- Retención configurable

## Technical Design

### Configuration
```yaml
Audit:
  Enabled: true
  Level: "info"  # info, warn, error
  RetentionDays: 30
  ExportPath: "./plugins/EligiusConnector/audit/"
```

### Data Model
```
audit_log:
  id: BIGINT PRIMARY KEY AUTO_INCREMENT
  timestamp: TIMESTAMP
  level: VARCHAR(10)
  action: VARCHAR(50)
  user: VARCHAR(16)
  user_type: VARCHAR(10)  # minecraft, discord
  details: TEXT
  ip: VARCHAR(45)
```

### Commands
- `/audit export` — Exportar a CSV (admin)
- `/audit search <query>` — Buscar registros (admin)
- `/audit clear` — Limpiar registros (admin)

### Permissions
- `connector.audit.export` — default: op
- `connector.audit.search` — default: op
- `connector.audit.clear` — default: op

## Testing
- Unit: Escritura de registros, búsqueda
- Integration: Logging completo de acción

## Acceptance Criteria
- [ ] Acciones se registran
- [ ] Exportación funciona
- [ ] Búsqueda funciona
- [ ] Retención funciona
