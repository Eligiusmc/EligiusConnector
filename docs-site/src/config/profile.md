---
title: Profile Configuration (profile.yml)
description: Discord panels for profile, inventory, location, and economy balance.
---

# profile.yml

Provides interactive Discord panels to view player stats, inventory, coordinates, and economy balance. Accessible via the **Player Panel** channel with buttons.

## channel

```yaml
channel: "1521391141436653668"
```

Discord channel where the panel embed is posted.

## panel

Main panel with buttons that trigger sub-commands.

```yaml
panel:
  embed:
    title: "👤 Player Panel"
    description: "Select an option below to view your stats, inventory, or location."
    color: "0x57F287"
    footer: "EligiusMC"
    timestamp: true
    fields:
      - name: "👤 Profile"
        value: "View your full profile — stats, health, location and more."
        inline: true
      # ... inventory, whereami, money
  buttons:
    profile: true
    inventory: true
    whereami: true
    money: true
```

| Button | Action |
|--------|--------|
| `profile` | Show full player profile |
| `inventory` | Show categorized inventory |
| `whereami` | Show location, biome, block |
| `money` | Show Vault balance |

## profile

Online embed with full stats. Offline fallback embed when the player is not on the server.

```yaml
profile:
  embed:
    title: "👤 {player_name}'s Profile"
    color: "0x5865F2"
    thumbnail: "https://minotar.net/avatar/{player_name}/128"
    timestamp: true
    fields:
      - name: "📋 Info"
        value: |
          **Name:** {player_name}
          **UUID:** {player_uuid}
      - name: "☠️ Combat"
        value: |
          **Kills:** {player_kills}
          **Deaths:** {player_deaths}
          **K/D:** {player_kd}
  offline:
    title: "👤 {player_name} (Offline)"
    color: "0x808080"
    # ... same structure, fewer fields
```

### Profile placeholders

| Placeholder | Description |
|-------------|-------------|
| `{player_name}` | Player name |
| `{player_uuid}` | Player UUID |
| `{player_linked_status}` | Discord link status |
| `{player_kills}` | Total kills |
| `{player_deaths}` | Total deaths |
| `{player_kd}` | Kill/death ratio |
| `{player_playtime}` | Formatted playtime |
| `{player_health}` / `{player_max_health}` | Current / max HP |
| `{player_health_bar}` | Visual health bar |
| `{player_food}` / `{player_max_food}` | Current / max food |
| `{player_food_bar}` | Visual food bar |
| `{player_xp_bar}` / `{player_level}` | XP bar + level |
| `{player_world}` | World name |
| `{player_direction}` | Cardinal direction |
| `{player_x}` / `{player_y}` / `{player_z}` | Coordinates |
| `{player_gamemode}` | Gamemode |
| `{player_ping}` | Latency in ms |

When the player is online, the `embed` section is used. When offline, the `offline` section is shown.

## inventory

Categorized inventory view.

```yaml
inventory:
  embed:
    title: "🎒 {player_name}'s Inventory"
    color: "0xF39C12"
    thumbnail: "https://minotar.net/avatar/{player_name}/128"
    footer: "Slots: {inv_used_slots}/{inv_total_slots}"
    fields:
      - name: "⚔️ Weapons"
        value: "{inv_weapons}"
      - name: "🛡️ Armor"
        value: "{inv_armor}"
      # ... tools, blocks, food, potions, misc
  options:
    empty_category_hidden: true
    max_items_per_category: 10
```

| Option | Type | Description |
|--------|------|-------------|
| `empty_category_hidden` | Boolean | Hide categories with no items |
| `max_items_per_category` | Integer | Max items listed per category |

Inventory placeholders: `{inv_weapons}`, `{inv_armor}`, `{inv_tools}`, `{inv_blocks}`, `{inv_food}`, `{inv_potions}`, `{inv_misc}`, `{inv_used_slots}`, `{inv_total_slots}`.

## whereami

Location, biome, and current block.

```yaml
whereami:
  embed:
    title: "📍 {player_name}'s Location"
    color: "0x2ECC71"
    fields:
      - name: "📍 Location"
        value: |
          **World:** {player_world}
          **X:** {player_x} **Y:** {player_y} **Z:** {player_z}
          **Direction:** {player_direction}
          **Biome:** {player_biome}
          **Block:** {player_block}
```

| Placeholder | Description |
|-------------|-------------|
| `{player_biome}` | Current biome name |
| `{player_block}` | Block the player is standing on |

## money

Shows Vault economy balance.

```yaml
money:
  embed:
    title: "💰 {player_name}'s Balance"
    color: "0xF1C40F"
    fields:
      - name: "💰 Balance"
        value: "**${balance}**"
```

Requires a Vault-compatible economy plugin. `{balance}` is the formatted balance from the economy provider.
