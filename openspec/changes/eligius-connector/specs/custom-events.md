# Spec: Custom Events

## Overview

Sistema de eventos personalizados configurables desde YAML.

## Requirements

### Functional
1. Eventos definidos en `events/<name>.yml`
2. Triggers: timer (cron) o comando
3. Hooks: on-start, during, on-end
4. Comandos ejecutados como consola
5. Notificaciones por evento
6. Duración configurable

### Non-Functional
- Async execution
- Rate limit: 1 evento por usuario por minuto
- Logging de eventos

## Technical Design

### Configuration
```yaml
# events/boss.yml
Events:
  boss:
    Enabled: true
    Trigger:
      Timer: "0 */3 * * *"
      Command: "/event start boss"
    ChannelId: "123456789012345690"
    Duration: 30
    
    OnStart:
      Messages:
        - ":skull: **¡BOSS APARECIO!**\n:clock1: Duración: {duration} minutos"
      Commands:
        - "say ¡El boss ha aparecido!"
    
    During:
      Interval: 300
      Messages:
        - ":clock1: Tiempo restante: {time_remaining}"
    
    OnEnd:
      Messages:
        - ":tada: **¡Boss derrotado!**"
      Commands:
        - "say ¡El boss ha sido derrotado!"
        - "give @a diamond 1"
```

### Commands
- `/event start <name>` — Iniciar evento (admin)
- `/event stop <name>` — Detener evento (admin)

### Permissions
- `connector.events.start` — default: op
- `connector.events.stop` — default: op

## Testing
- Unit: Parser de YAML, validación
- Integration: Ejecución de evento completo

## Acceptance Criteria
- [ ] Eventos se cargan desde YAML
- [ ] Triggers funcionan (timer/comando)
- [ ] Hooks se ejecutan
- [ ] Comandos se ejecutan
- [ ] Notificaciones se envían
