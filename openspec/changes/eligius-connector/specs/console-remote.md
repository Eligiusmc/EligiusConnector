# Spec: Console Remote

## Overview

Control remoto de consola del servidor desde Discord.

## Requirements

### Functional
1. Ejecutar comandos de consola desde Discord (`/console <cmd>`)
2. Output en canal de Discord con code blocks
3. Blacklist de comandos peligrosos (`/stop`, `/restart`)
4. Whitelist de comandos permitidos
5. Log de comandos ejecutados (audit)
6. Configurable por canal

### Non-Functional
- Rate limit: 5 comandos por minuto por usuario
- Output truncado a 2000 caracteres (límite de Discord)
- Async execution
- Logging completo de comandos

## Technical Design

### Configuration
```yaml
DiscordConsoleChannelId: "123456789012345680"
DiscordConsoleChannelLogRefreshRateInSeconds: 2
DiscordConsoleChannelUseCodeBlocks: true
DiscordConsoleChannelLevels: ["info", "warn", "error"]

ConsoleCommandBlacklist:
  - "stop"
  - "restart"
  - "reload"
  - "op"
  - "deop"

ConsoleCommandWhitelist: []
```

### Commands
- `/console <cmd>` (Discord) — Ejecutar comando

### Permissions
- `connector.console` — default: false
- `connector.console.stop` — default: false
- `connector.console.restart` — default: false

## Testing
- Unit: Validación de comandos, blacklist/whitelist
- Integration: Ejecución de comando, output en Discord

## Acceptance Criteria
- [ ] Comandos se ejecutan desde Discord
- [ ] Output se muestra en Discord
- [ ] Blacklist funciona
- [ ] Rate limiting funciona
- [ ] Comandos se registran en audit
