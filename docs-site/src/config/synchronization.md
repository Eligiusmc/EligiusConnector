---
title: Synchronization Configuration (synchronization.yml)
description: Bidirectional sync of Minecraft groups ↔ Discord roles, and nicknames.
---

# synchronization.yml

Two sync systems: **roles** mirrors LuckPerms groups to Discord roles, **nickname** sets Discord nicknames from Minecraft usernames. Both are bidirectional — roles are assigned and removed as groups change.

Requires Vault and LuckPerms on the server (for group detection).

## Master switch

```yaml
enabled: true
```

Set to `false` to disable all sync systems at once.

## roles

```yaml
roles:
  enabled: true
  interval: 30
  mapping:
    verified: "1521342858945101894"
    # testing: "ROLE_ID_HERE"
    # vip: "ROLE_ID_HERE"
```

| Field | Type | Description |
|-------|------|-------------|
| `enabled` | Boolean | Enable role sync |
| `interval` | Integer | How often to check and sync (seconds) |
| `mapping` | Map | LuckPerms group name → Discord role ID |

The mapping key is the LuckPerms group name, the value is the Discord role ID. When a linked player has a mapped LuckPerms group, the bot assigns the corresponding Discord role. When the group is removed (or the player unlinks), the role is removed. Each player can have multiple roles from multiple mapped groups.

## nickname

```yaml
nickname:
  enabled: true
  interval: 30
  format: "%player_name%"
```

| Field | Type | Description |
|-------|------|-------------|
| `enabled` | Boolean | Enable nickname sync |
| `interval` | Integer | How often to check and update (seconds) |
| `format` | String | Nickname format |

The `format` field supports the `%player_name%` placeholder. The bot sets each linked member's Discord nickname to the resolved format on every sync cycle.
