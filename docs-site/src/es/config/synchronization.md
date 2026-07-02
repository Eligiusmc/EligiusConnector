---
title: Configuración de Sincronización (synchronization.yml)
description: Sincronización bidireccional de grupos de Minecraft ↔ roles de Discord, y apodos.
---

# synchronization.yml

Dos sistemas de sincronización: **roles** refleja los grupos de LuckPerms a roles de Discord, **nickname** establece los apodos de Discord desde los nombres de usuario de Minecraft. Ambos son bidireccionales — los roles se asignan y eliminan a medida que los grupos cambian.

Requiere Vault y LuckPerms en el servidor (para detección de grupos).

## Interruptor maestro

```yaml
enabled: true
```

Configura en `false` para desactivar todos los sistemas de sincronización a la vez.

## roles

```yaml
roles:
  enabled: true
  interval: 30
  mapping:
    verified: "1521342858945101894"
    # testing: "ID_DEL_ROL"
    # vip: "ID_DEL_ROL"
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `enabled` | Boolean | Activar sincronización de roles |
| `interval` | Integer | Cada cuánto verificar y sincronizar (segundos) |
| `mapping` | Map | Nombre del grupo LuckPerms → ID del rol de Discord |

La clave del mapping es el nombre del grupo LuckPerms, el valor es el ID del rol de Discord. Cuando un jugador vinculado tiene un grupo LuckPerms mapeado, el bot asigna el rol de Discord correspondiente. Cuando el grupo se elimina (o el jugador se desvincula), el rol se remueve. Cada jugador puede tener múltiples roles de múltiples grupos mapeados.

## nickname

```yaml
nickname:
  enabled: true
  interval: 30
  format: "%player_name%"
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `enabled` | Boolean | Activar sincronización de apodos |
| `interval` | Integer | Cada cuánto verificar y actualizar (segundos) |
| `format` | String | Formato del apodo |

El campo `format` soporta el placeholder `%player_name%`. El bot establece el apodo de Discord de cada miembro vinculado al formato resuelto en cada ciclo de sincronización.
