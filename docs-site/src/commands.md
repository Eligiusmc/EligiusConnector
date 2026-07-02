---
title: Commands & Permissions
description: Complete list of commands and permissions for EligiusConnector.
---

# Commands & Permissions

## Player Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/verify <code>` | Link your Minecraft account to Discord | `connector.verify` |
| `/unlink [player]` | Unlink your account or another player's | `connector.unlink.self` / `.other` |
| `/chat <message>` | Send a message to the Discord chat channel | `connector.chat` |

## Admin Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/connector reload` | Reload all configuration files | `connector.reload` |
| `/connector status` | View plugin status (Discord, DB, linked accounts) | `connector.admin` |
| `/events list` | List available custom events | `connector.events` |
| `/events start <id>` | Start a custom event | `connector.events` |
| `/events stop <id>` | Stop a custom event | `connector.events` |

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `connector.verify` | `true` | Allows linking account via /verify |
| `connector.unlink.self` | `true` | Allows unlinking own account |
| `connector.unlink.other` | `op` | Allows unlinking other players |
| `connector.chat` | `true` | Allows using /chat command |
| `connector.chat.send` | `true` | Allows sending chat messages |
| `connector.reload` | `op` | Allows /connector reload |
| `connector.admin` | `op` | Allows /connector status |
| `connector.events` | `op` | Manage custom events |
| `connector.console` | `false` | Execute commands from Discord console channel |
