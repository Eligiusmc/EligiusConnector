---
title: Configuración de Contadores Dinámicos (counters.yml)
description: Contadores de nombres de canal de Discord basados en intervalos y estado.
---

# counters.yml

Renombra canales de voz/texto de Discord dinámicamente. Dos tipos: **periodic** (basado en intervalo) y **state** (activado por eventos del servidor).

## Placeholders integrados

| Placeholder | Descripción |
|-------------|-------------|
| `%server_online%` | Jugadores online actuales |
| `%server_max_players%` | Slots máximos del servidor |
| `%server_unique_joins%` | Total de jugadores únicos que han entrado |
| `%server_ram_used%` | RAM usada (MB) |
| `%server_ram_free%` | RAM libre (MB) |
| `%server_ram_total%` | RAM total (MB) |
| `%server_uptime%` | Tiempo de actividad del servidor |
| `%server_members%` | Conteo de miembros del servidor de Discord |

## periodic

Actualiza el nombre de un canal cada N segundos usando una cadena de formato con placeholders.

```yaml
counters:
  online_players:
    type: periodic
    enabled: true
    channel: "1521280346585305268"
    format: "🟢 Online: %server_online%"
    interval: 30
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `type` | String | Debe ser `periodic` |
| `enabled` | Boolean | Activar este contador |
| `channel` | String | ID del canal de Discord a renombrar |
| `format` | String | Nombre del canal con placeholders |
| `interval` | Integer | Intervalo de actualización en segundos |

### Debounce y cooldown

Para evitar los rate limits de Discord, los renombres de canal tienen un mecanismo de debounce: si el nombre resuelto no ha cambiado, el renombre se omite. Adicionalmente, se aplica un cooldown de 120 segundos entre renombres por canal. Los contadores de alta frecuencia (ej. `interval: 5`) solo renombrarán cuando el valor realmente cambie.

## state

Reacciona a cambios de estado del servidor — renombra el canal inmediatamente cuando el estado cambia.

```yaml
counters:
  server_status:
    type: state
    enabled: true
    channel: "1521276573033173204"
    state_key: server_online
    states:
      online: "🟢 Online"
      offline: "🔴 Offline"
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `type` | String | Debe ser `state` |
| `enabled` | Boolean | Activar este contador |
| `channel` | String | ID del canal de Discord a renombrar |
| `state_key` | String | Estado observable a monitorear (`server_online`) |
| `states.online` | String | Nombre del canal cuando el estado es `true` |
| `states.offline` | String | Nombre del canal cuando el estado es `false` |

## ⚠️ Canal compartido de server_status

El counter `server_status` comparte su `channel` con `status.yml`. El counter renombra el canal, y `status.yml` publica el embed online/offline. Mantenlos siempre sincronizados — cambiar el canal en un archivo requiere cambiarlo en el otro.
