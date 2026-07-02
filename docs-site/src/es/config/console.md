---
title: Configuración del Relay de Consola (console.yml)
description: Transmite logs del servidor a Discord y ejecuta comandos desde un canal de consola.
---

# console.yml

Bridge de consola bidireccional: los logs del servidor se transmiten a un canal de Discord, y los mensajes escritos en ese canal se ejecutan como comandos del servidor (si no están en la lista negra).

## channel

```yaml
channel: "1521272226442383440"
```

ID del canal de Discord. Todos los logs del servidor se transmiten aquí. Los mensajes enviados por usuarios en este canal se ejecutan como comandos del servidor Minecraft.

## poll_interval

```yaml
poll_interval: 2
```

Cada cuánto (en segundos) el plugin lee nuevas líneas de `logs/latest.log`. Valores más bajos dan salida más en tiempo real a costa de un uso de CPU ligeramente mayor.

## command_blacklist

```yaml
command_blacklist:
  - "stop"
  - "restart"
  - "reload"
  - "ban"
  - "ban-ip"
  - "pardon"
  - "pardon-ip"
  - "op"
  - "deop"
  - "say"
  - "gamemode"
  - "whitelist"
  - "kick"
  - "plugins"
```

Los comandos en esta lista no pueden ejecutarse desde Discord. La verificación es por prefijo — ej. bloquear `ban` también bloquea `ban-ip` y `banlist`, pero la entrada explícita `ban-ip` asegura claridad. Los comandos administrativos peligrosos (stop, restart, op, deop) están en la lista negra por defecto.

## Formateo

La salida de logs en Discord usa bloques de código ` ```ansi ` para preservar los códigos de color ANSI de la consola del servidor, dando una apariencia nativa de consola.
