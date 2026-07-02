---
title: Configuración de Perfil (profile.yml)
description: Paneles de Discord para perfil, inventario, ubicación y saldo de economía.
---

# profile.yml

Proporciona paneles interactivos en Discord para ver estadísticas de jugadores, inventario, coordenadas y saldo de economía. Accesible mediante el canal **Player Panel** con botones.

## channel

```yaml
channel: "1521391141436653668"
```

Canal de Discord donde se publica el embed del panel.

## panel

Panel principal con botones que activan sub-comandos.

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

| Botón | Acción |
|-------|--------|
| `profile` | Mostrar perfil completo del jugador |
| `inventory` | Mostrar inventario categorizado |
| `whereami` | Mostrar ubicación, bioma, bloque |
| `money` | Mostrar saldo de Vault |

## profile

Embed online con estadísticas completas. Embed alternativo offline cuando el jugador no está en el servidor.

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
    # ... misma estructura, menos campos
```

### Placeholders de perfil

| Placeholder | Descripción |
|-------------|-------------|
| `{player_name}` | Nombre del jugador |
| `{player_uuid}` | UUID del jugador |
| `{player_linked_status}` | Estado de vinculación con Discord |
| `{player_kills}` | Muertes totales |
| `{player_deaths}` | Muertes sufridas totales |
| `{player_kd}` | Ratio kills/deaths |
| `{player_playtime}` | Tiempo de juego formateado |
| `{player_health}` / `{player_max_health}` | HP actual / máximo |
| `{player_health_bar}` | Barra de vida visual |
| `{player_food}` / `{player_max_food}` | Comida actual / máxima |
| `{player_food_bar}` | Barra de comida visual |
| `{player_xp_bar}` / `{player_level}` | Barra de XP + nivel |
| `{player_world}` | Nombre del mundo |
| `{player_direction}` | Dirección cardinal |
| `{player_x}` / `{player_y}` / `{player_z}` | Coordenadas |
| `{player_gamemode}` | Modo de juego |
| `{player_ping}` | Latencia en ms |

Cuando el jugador está online, se usa la sección `embed`. Cuando está offline, se muestra la sección `offline`.

## inventory

Vista categorizada del inventario.

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

| Opción | Tipo | Descripción |
|--------|------|-------------|
| `empty_category_hidden` | Boolean | Ocultar categorías sin ítems |
| `max_items_per_category` | Integer | Máximo de ítems listados por categoría |

Placeholders de inventario: `{inv_weapons}`, `{inv_armor}`, `{inv_tools}`, `{inv_blocks}`, `{inv_food}`, `{inv_potions}`, `{inv_misc}`, `{inv_used_slots}`, `{inv_total_slots}`.

## whereami

Ubicación, bioma y bloque actual.

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

| Placeholder | Descripción |
|-------------|-------------|
| `{player_biome}` | Nombre del bioma actual |
| `{player_block}` | Bloque sobre el que está parado el jugador |

## money

Muestra el saldo de economía de Vault.

```yaml
money:
  embed:
    title: "💰 {player_name}'s Balance"
    color: "0xF1C40F"
    fields:
      - name: "💰 Balance"
        value: "**${balance}**"
```

Requiere un plugin de economía compatible con Vault. `{balance}` es el saldo formateado del proveedor de economía.
