---
title: Configuration Bot et Base de Données (config.yml)
description: Configuration principale du plugin — token du bot, base de données, langue, debug.
---

# config.yml

Le fichier de configuration principal. Contrôle la connexion du bot, le type de base de données, la langue et le mode debug.

## bot

```yaml
bot:
  token: "${DISCORD_BOT_TOKEN}"
  guild_id: "508152650978820096"
  status_rotation_minutes: 15
  game_status:
    - "🎮 EligiusMC"
    - "🟢 {online} joueurs"
```

| Champ | Type | Description |
|-------|------|-------------|
| `token` | Chaîne | Token du bot Discord. Utilisez `${ENV_VAR}` pour les variables d'environnement |
| `guild_id` | Chaîne | ID du serveur Discord (guild) |
| `status_rotation_minutes` | Entier | Fréquence de rotation du bot entre les entrées `game_status` |
| `game_status` | Liste | Messages de statut affichés sur le profil du bot. `{online}` est résolu en nombre de joueurs actuel |

## database

```yaml
database:
  type: "sqlite"
  mysql:
    host: "localhost"
    port: 3306
    name: "eligiusconnector"
    username: "root"
    password: ""
    pool_size: 10
```

| Champ | Type | Description |
|-------|------|-------------|
| `type` | Chaîne | `sqlite` ou `mysql` |
| `mysql.*` | — | Paramètres de connexion MySQL (utilisés seulement quand `type: mysql`) |
| `mysql.pool_size` | Entier | Taille du pool de connexions HikariCP |

## language

```yaml
language: "fr"
```

Langue par défaut pour les joueurs qui n'ont pas défini de préférence. Disponibles : `en`, `es`, `fr`, `de`, `pt`, `ru`.

## debug

```yaml
debug: false
```

Quand activé, affiche des informations de diagnostic supplémentaires dans la console.
