---
title: Comandos y Permisos
description: Lista completa de comandos y permisos de EligiusConnector.
---

# Comandos y Permisos

## Comandos de Jugador

| Comando | Descripción | Permiso |
|---------|-------------|---------|
| `/verify <code>` | Vincula tu cuenta de Minecraft a Discord | `connector.verify` |
| `/unlink [player]` | Desvincula tu cuenta o la de otro jugador | `connector.unlink.self` / `.other` |
| `/chat <message>` | Envía un mensaje al canal de chat de Discord | `connector.chat` |

## Comandos de Admin

| Comando | Descripción | Permiso |
|---------|-------------|---------|
| `/connector reload` | Recarga todos los archivos de configuración | `connector.reload` |
| `/connector status` | Ver estado del plugin (Discord, DB, cuentas vinculadas) | `connector.admin` |
| `/events list` | Lista los eventos personalizados disponibles | `connector.events` |
| `/events start <id>` | Inicia un evento personalizado | `connector.events` |
| `/events stop <id>` | Detiene un evento personalizado | `connector.events` |

## Permisos

| Permiso | Por defecto | Descripción |
|---------|-------------|-------------|
| `connector.verify` | `true` | Permite vincular cuenta vía /verify |
| `connector.unlink.self` | `true` | Permite desvincular cuenta propia |
| `connector.unlink.other` | `op` | Permite desvincular a otros jugadores |
| `connector.chat` | `true` | Permite usar el comando /chat |
| `connector.chat.send` | `true` | Permite enviar mensajes de chat |
| `connector.reload` | `op` | Permite /connector reload |
| `connector.admin` | `op` | Permite /connector status |
| `connector.events` | `op` | Gestionar eventos personalizados |
| `connector.console` | `false` | Ejecutar comandos desde el canal de consola de Discord |
