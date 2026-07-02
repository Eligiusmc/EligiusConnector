---
title: Configuration du Chat Bridge (chat.yml)
description: Configurez le pont de chat Discord ↔ Minecraft, les webhooks et les filtres.
---

# chat.yml

Contrôle le pont de chat bidirectionnel, l'apparence des webhooks et les filtres anti-abus.

## channel

```yaml
channel: "1521272219223851028"
```

Le canal Discord où les messages MC ↔ DC apparaissent et où `/chat` envoie les messages.

## bridge

```yaml
bridge:
  mc_to_discord: true
  discord_to_mc: true
```

| Champ | Description |
|-------|-------------|
| `mc_to_discord` | Envoie le chat Minecraft vers Discord |
| `discord_to_mc` | Envoie les messages Discord vers le chat Minecraft |

## format

```yaml
format:
  mc_to_discord: "{player}: {message}"
  discord_to_mc: "[DC] {player}: {message}"
```

| Placeholder | Se résout en |
|-------------|-------------|
| `{player}` | Nom du joueur |
| `{message}` | Contenu du message de chat |

## webhook

```yaml
webhook:
  enabled: true
  name: "Minecraft"
  avatar: "https://minotar.net/avatar/%player_name%/128"
```

Utilise les webhooks Discord pour les messages MC → DC, affichant les têtes et pseudos des joueurs.

## filter

```yaml
filter:
  enabled: true
```

Interrupteur principal pour désactiver tous les filtres en une fois.

### filter.spam

| Champ | Défaut | Description |
|-------|--------|-------------|
| `enabled` | `true` | Active l'anti-spam |
| `max_messages` | `5` | Messages autorisés dans la fenêtre de temps |
| `time_window_seconds` | `10` | Fenêtre de temps en secondes |

### filter.profanity

| Champ | Description |
|-------|-------------|
| `enabled` | Active le filtre de grossièretés |
| `words` | Liste de mots bloqués (insensible à la casse) |

### filter.links

| Champ | Description |
|-------|-------------|
| `enabled` | Active le filtre de liens |
| `mode` | `whitelist` (seuls les domaines listés sont autorisés) ou `blacklist` (les domaines listés sont bloqués) |
| `domains` | Liste de domaines pour le mode sélectionné |

### filter.caps

| Champ | Défaut | Description |
|-------|--------|-------------|
| `enabled` | `true` | Active le filtre de majuscules |
| `max_percentage` | `70` | Pourcentage maximum de caractères en majuscules |
| `min_length` | `10` | Longueur minimale du message à vérifier |

### filter.punishment

```yaml
punishment:
  enabled: true
  thresholds:
    2: "mute:300"
    5: "tempban:3600"
    10: "banip"
```

Sanctions progressives pour les récidivistes. Actions : `warn`, `delete`, `mute:<secondes>`, `tempban:<secondes>`, `banip`.
