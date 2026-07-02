---
title: Profilkonfiguration (profile.yml)
description: Discord-Panels für Profil, Inventar, Standort und Kontostand.
---

# profile.yml

Bietet interaktive Discord-Panels zum Anzeigen von Spielerstatistiken, Inventar, Koordinaten und Kontostand. Zugänglich über den **Player Panel**-Kanal mit Schaltflächen.

## channel

```yaml
channel: "1521391141436653668"
```

Discord-Kanal, in dem das Panel-Embed veröffentlicht wird.

## panel

Hauptpanel mit Schaltflächen, die Unterbefehle auslösen.

```yaml
panel:
  embed:
    title: "👤 Spieler-Panel"
    description: "Wählen Sie unten eine Option, um Ihre Statistiken, Ihr Inventar oder Ihren Standort anzuzeigen."
    color: "0x57F287"
    footer: "EligiusMC"
    timestamp: true
    fields:
      - name: "👤 Profil"
        value: "Vollständiges Profil anzeigen — Statistiken, Gesundheit, Standort und mehr."
        inline: true
      # ... inventory, whereami, money
  buttons:
    profile: true
    inventory: true
    whereami: true
    money: true
```

| Schaltfläche | Aktion |
|-------------|--------|
| `profile` | Vollständiges Spielerprofil anzeigen |
| `inventory` | Kategorisiertes Inventar anzeigen |
| `whereami` | Standort, Biom, Block anzeigen |
| `money` | Vault-Kontostand anzeigen |

## profile

Online-Embed mit vollständigen Statistiken. Offline-Fallback-Embed, wenn der Spieler nicht auf dem Server ist.

```yaml
profile:
  embed:
    title: "👤 {player_name}s Profil"
    color: "0x5865F2"
    thumbnail: "https://minotar.net/avatar/{player_name}/128"
    timestamp: true
    fields:
      - name: "📋 Info"
        value: |
          **Name:** {player_name}
          **UUID:** {player_uuid}
      - name: "☠️ Kampf"
        value: |
          **Kills:** {player_kills}
          **Tode:** {player_deaths}
          **K/D:** {player_kd}
  offline:
    title: "👤 {player_name} (Offline)"
    color: "0x808080"
    # ... gleiche Struktur, weniger Felder
```

### Profil-Platzhalter

| Platzhalter | Beschreibung |
|-------------|-------------|
| `{player_name}` | Spielername |
| `{player_uuid}` | Spieler-UUID |
| `{player_linked_status}` | Discord-Verknüpfungsstatus |
| `{player_kills}` | Gesamt-Kills |
| `{player_deaths}` | Gesamt-Tode |
| `{player_kd}` | Kill/Tod-Verhältnis |
| `{player_playtime}` | Formatierte Spielzeit |
| `{player_health}` / `{player_max_health}` | Aktuelle / max. HP |
| `{player_health_bar}` | Visuelle Gesundheitsleiste |
| `{player_food}` / `{player_max_food}` | Aktuelle / max. Nahrung |
| `{player_food_bar}` | Visuelle Nahrungsleiste |
| `{player_xp_bar}` / `{player_level}` | XP-Leiste + Level |
| `{player_world}` | Weltname |
| `{player_direction}` | Himmelsrichtung |
| `{player_x}` / `{player_y}` / `{player_z}` | Koordinaten |
| `{player_gamemode}` | Spielmodus |
| `{player_ping}` | Latenz in ms |

Wenn der Spieler online ist, wird der `embed`-Abschnitt verwendet. Wenn offline, wird der `offline`-Abschnitt angezeigt.

## inventory

Kategorisierte Inventaransicht.

```yaml
inventory:
  embed:
    title: "🎒 {player_name}s Inventar"
    color: "0xF39C12"
    thumbnail: "https://minotar.net/avatar/{player_name}/128"
    footer: "Slots: {inv_used_slots}/{inv_total_slots}"
    fields:
      - name: "⚔️ Waffen"
        value: "{inv_weapons}"
      - name: "🛡️ Rüstung"
        value: "{inv_armor}"
      # ... tools, blocks, food, potions, misc
  options:
    empty_category_hidden: true
    max_items_per_category: 10
```

| Option | Typ | Beschreibung |
|--------|-----|-------------|
| `empty_category_hidden` | Boolean | Leere Kategorien ausblenden |
| `max_items_per_category` | Integer | Max. aufgelistete Gegenstände pro Kategorie |

Inventarplatzhalter: `{inv_weapons}`, `{inv_armor}`, `{inv_tools}`, `{inv_blocks}`, `{inv_food}`, `{inv_potions}`, `{inv_misc}`, `{inv_used_slots}`, `{inv_total_slots}`.

## whereami

Standort, Biom und aktueller Block.

```yaml
whereami:
  embed:
    title: "📍 {player_name}s Standort"
    color: "0x2ECC71"
    fields:
      - name: "📍 Standort"
        value: |
          **Welt:** {player_world}
          **X:** {player_x} **Y:** {player_y} **Z:** {player_z}
          **Richtung:** {player_direction}
          **Biom:** {player_biome}
          **Block:** {player_block}
```

| Platzhalter | Beschreibung |
|-------------|-------------|
| `{player_biome}` | Aktueller Biom-Name |
| `{player_block}` | Block, auf dem der Spieler steht |

## money

Zeigt den Vault-Kontostand an.

```yaml
money:
  embed:
    title: "💰 {player_name}s Kontostand"
    color: "0xF1C40F"
    fields:
      - name: "💰 Kontostand"
        value: "**${balance}**"
```

Erfordert ein Vault-kompatibles Wirtschafts-Plugin. `{balance}` ist der formatierte Kontostand des Wirtschaftsanbieters.
