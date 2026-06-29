# Spec: PlaceholderAPI

## Overview

Integración con PlaceholderAPI para soporte de placeholders.

## Requirements

### Functional
1. Registrar expansión `connector`
2. Placeholders propios (connector_*)
3. Soporte para placeholders externos en config
4. Cache de placeholders
5. Configurable desde config

### Non-Functional
- Async execution
- Rate limit: 1000 resoluciones por minuto
- Cache configurable

## Technical Design

### Configuration
```yaml
PlaceholderAPI:
  Enabled: true  # Soft dependency
  CachePlaceholders: true
  CacheTimeInSeconds: 30
```

### Nuestra Expansion
```
%connector_linked_<player>%          → true/false
%connector_discord_id_<player>%      → Discord ID
%connector_discord_name_<player>%    → Discord username
%connector_discord_roles_<player>%   → Roles de Discord
%connector_minecraft_name_<player>%  → Nombre de MC
%connector_clan_<player>%            → Clan del jugador
%connector_clan_tag_<player>%        → Tag del clan
%connector_group_<player>%           → Grupo LuckPerms
%connector_health_<player>%          → Vida
%connector_food_<player>%            → Comida
%connector_level_<player>%           → Nivel
%connector_world_<player>%           → Mundo
%connector_x_<player>%               → X
%connector_y_<player>%               → Y
%connector_z_<player>%               → Z
```

### Placeholders Externos Soportados
| Plugin | Placeholders |
|--------|--------------|
| LuckPerms | `%luckperms_group%`, `%luckperms_meta_<key>%` |
| ClansX | `%clans_clan_name%`, `%clans_clan_tag%` |
| Factions | `%factions_faction%`, `%factions_title%` |
| Essentials | `%essentials_nick%`, `%essentials_balance%` |
| WorldGuard | `%worldguard_region%` |

### Commands
- `/connector papi` — Info de placeholders disponibles (admin)

### Permissions
- `connector.papi` — default: op

## Testing
- Unit: Resolución de placeholders, cache
- Integration: Placeholders en config

## Acceptance Criteria
- [ ] Expansión se registra
- [ ] Placeholders propios funcionan
- [ ] Placeholders externos funcionan
- [ ] Cache funciona
- [ ] Config soporta placeholders
