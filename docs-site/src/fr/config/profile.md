---
title: Configuration du Profil (profile.yml)
description: Panneaux Discord pour le profil, l'inventaire, la localisation et le solde économique.
---

# profile.yml

Fournit des panneaux Discord interactifs pour voir les stats du joueur, l'inventaire, les coordonnées et le solde économique. Accessible via le canal **Player Panel** avec des boutons.

## channel

```yaml
channel: "1521391141436653668"
```

Canal Discord où l'embed du panneau est publié.

## panel

Panneau principal avec des boutons qui déclenchent des sous-commandes.

```yaml
panel:
  embed:
    title: "👤 Panneau Joueur"
    description: "Sélectionnez une option ci-dessous pour voir vos stats, inventaire ou position."
    color: "0x57F287"
    footer: "EligiusMC"
    timestamp: true
    fields:
      - name: "👤 Profil"
        value: "Voir votre profil complet — stats, santé, position et plus."
        inline: true
      # ... inventory, whereami, money
  buttons:
    profile: true
    inventory: true
    whereami: true
    money: true
```

| Bouton | Action |
|--------|--------|
| `profile` | Afficher le profil complet du joueur |
| `inventory` | Afficher l'inventaire catégorisé |
| `whereami` | Afficher la position, le biome, le bloc |
| `money` | Afficher le solde Vault |

## profile

Embed en ligne avec les stats complètes. Embed de secours hors ligne quand le joueur n'est pas sur le serveur.

```yaml
profile:
  embed:
    title: "👤 Profil de {player_name}"
    color: "0x5865F2"
    thumbnail: "https://minotar.net/avatar/{player_name}/128"
    timestamp: true
    fields:
      - name: "📋 Info"
        value: |
          **Nom:** {player_name}
          **UUID:** {player_uuid}
      - name: "☠️ Combat"
        value: |
          **Kills:** {player_kills}
          **Morts:** {player_deaths}
          **K/D:** {player_kd}
  offline:
    title: "👤 {player_name} (Hors ligne)"
    color: "0x808080"
    # ... même structure, moins de champs
```

### Placeholders du profil

| Placeholder | Description |
|-------------|-------------|
| `{player_name}` | Nom du joueur |
| `{player_uuid}` | UUID du joueur |
| `{player_linked_status}` | Statut de liaison Discord |
| `{player_kills}` | Total de kills |
| `{player_deaths}` | Total de morts |
| `{player_kd}` | Ratio kills/morts |
| `{player_playtime}` | Temps de jeu formaté |
| `{player_health}` / `{player_max_health}` | PV actuels / max |
| `{player_health_bar}` | Barre de vie visuelle |
| `{player_food}` / `{player_max_food}` | Nourriture actuelle / max |
| `{player_food_bar}` | Barre de nourriture visuelle |
| `{player_xp_bar}` / `{player_level}` | Barre XP + niveau |
| `{player_world}` | Nom du monde |
| `{player_direction}` | Direction cardinale |
| `{player_x}` / `{player_y}` / `{player_z}` | Coordonnées |
| `{player_gamemode}` | Mode de jeu |
| `{player_ping}` | Latence en ms |

Quand le joueur est en ligne, la section `embed` est utilisée. Quand il est hors ligne, la section `offline` est affichée.

## inventory

Vue d'inventaire catégorisée.

```yaml
inventory:
  embed:
    title: "🎒 Inventaire de {player_name}"
    color: "0xF39C12"
    thumbnail: "https://minotar.net/avatar/{player_name}/128"
    footer: "Slots: {inv_used_slots}/{inv_total_slots}"
    fields:
      - name: "⚔️ Armes"
        value: "{inv_weapons}"
      - name: "🛡️ Armure"
        value: "{inv_armor}"
      # ... tools, blocks, food, potions, misc
  options:
    empty_category_hidden: true
    max_items_per_category: 10
```

| Option | Type | Description |
|--------|------|-------------|
| `empty_category_hidden` | Booléen | Masquer les catégories sans objets |
| `max_items_per_category` | Entier | Max d'objets listés par catégorie |

Placeholders d'inventaire : `{inv_weapons}`, `{inv_armor}`, `{inv_tools}`, `{inv_blocks}`, `{inv_food}`, `{inv_potions}`, `{inv_misc}`, `{inv_used_slots}`, `{inv_total_slots}`.

## whereami

Position, biome et bloc actuel.

```yaml
whereami:
  embed:
    title: "📍 Position de {player_name}"
    color: "0x2ECC71"
    fields:
      - name: "📍 Position"
        value: |
          **Monde:** {player_world}
          **X:** {player_x} **Y:** {player_y} **Z:** {player_z}
          **Direction:** {player_direction}
          **Biome:** {player_biome}
          **Bloc:** {player_block}
```

| Placeholder | Description |
|-------------|-------------|
| `{player_biome}` | Nom du biome actuel |
| `{player_block}` | Bloc sur lequel le joueur se tient |

## money

Affiche le solde économique Vault.

```yaml
money:
  embed:
    title: "💰 Solde de {player_name}"
    color: "0xF1C40F"
    fields:
      - name: "💰 Solde"
        value: "**${balance}**"
```

Nécessite un plugin économique compatible Vault. `{balance}` est le solde formaté du fournisseur économique.
