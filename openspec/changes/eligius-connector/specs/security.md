# Spec: Security

## Overview

Medidas de seguridad para el plugin.

## Requirements

### Functional
1. Token de bot en config (nunca hardcodeado)
2. Cifrado de datos sensibles en DB
3. Rate limiting en todos los comandos
4. Validación de permisos en startup
5. Servidores autorizados por ID
6. Logging de intentos de acceso no autorizado

### Non-Functional
- Fallback seguro si token inválido
- Validación de config al inicio
- Mensajes de error no expuestos

## Technical Design

### Configuration
```yaml
Security:
  # Token del bot (nunca hardcodeado)
  BotToken: "${DISCORD_BOT_TOKEN}"
  
  # Servidores autorizados
  AuthorizedServers:
    - "123456789012345678"
  
  # Rate limiting
  RateLimits:
    Commands: 5  # por minuto por usuario
    Queries: 10  # por minuto por usuario
    Console: 3   # por minuto por usuario
  
  # Cifrado
  Encryption:
    Enabled: true
    Algorithm: "AES-256"
    Key: "${ENCRYPTION_KEY}"
```

### Startup Validation
1. Verificar token de Discord
2. Verificar permisos del bot
3. Verificar canales configurados
4. Verificar DB accesible
5. Verificar LuckPerms disponible
6. Verificar PlaceholderAPI disponible (soft)

### Commands
- `/connector security` — Ver estado de seguridad (admin)

### Permissions
- `connector.admin` — default: op

## Testing
- Unit: Validación de token, rate limiting
- Integration: Startup validation completa

## Acceptance Criteria
- [ ] Token se valida al inicio
- [ ] Permisos se verifican
- [ ] Rate limiting funciona
- [ ] Datos sensibles se cifran
- [ ] Servidores autorizados se validan
