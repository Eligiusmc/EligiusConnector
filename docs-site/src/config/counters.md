---
title: Dynamic Counters Configuration (counters.yml)
description: Periodic and state-based Discord channel name counters.
---

# counters.yml

Renames Discord voice/text channels dynamically. Two types: **periodic** (interval-based) and **state** (triggered by server events).

## Built-in placeholders

| Placeholder | Description |
|-------------|-------------|
| `%server_online%` | Current online players |
| `%server_max_players%` | Server max slots |
| `%server_unique_joins%` | Total unique players ever joined |
| `%server_ram_used%` | RAM used (MB) |
| `%server_ram_free%` | RAM free (MB) |
| `%server_ram_total%` | Total RAM (MB) |
| `%server_uptime%` | Server uptime |
| `%server_members%` | Discord server member count |

## periodic

Updates a channel name every N seconds using a format string with placeholders.

```yaml
counters:
  online_players:
    type: periodic
    enabled: true
    channel: "1521280346585305268"
    format: "🟢 Online: %server_online%"
    interval: 30
```

| Field | Type | Description |
|-------|------|-------------|
| `type` | String | Must be `periodic` |
| `enabled` | Boolean | Enable this counter |
| `channel` | String | Discord channel ID to rename |
| `format` | String | Channel name with placeholders |
| `interval` | Integer | Update interval in seconds |

### Debounce & cooldown

To avoid Discord rate limits, channel renames have a debounce mechanism: if the resolved name hasn't changed, the rename is skipped. Additionally, a 120-second cooldown is enforced between renames per channel. High-frequency counters (e.g. `interval: 5`) will only rename when the value actually changes.

## state

Reacts to server state changes — renames the channel immediately when the state flips.

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

| Field | Type | Description |
|-------|------|-------------|
| `type` | String | Must be `state` |
| `enabled` | Boolean | Enable this counter |
| `channel` | String | Discord channel ID to rename |
| `state_key` | String | Observable state to watch (`server_online`) |
| `states.online` | String | Channel name when state is `true` |
| `states.offline` | String | Channel name when state is `false` |

## ⚠️ server_status channel sharing

The `server_status` counter shares its `channel` with `status.yml`. The counter renames the channel, and `status.yml` posts the online/offline embed. Always keep them in sync — changing the channel in one file requires changing it in the other.
