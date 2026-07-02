---
title: Configuration des Compteurs Dynamiques (counters.yml)
description: Compteurs de noms de canaux Discord périodiques et basés sur l'état.
---

# counters.yml

Renomme dynamiquement les canaux vocaux/textuels Discord. Deux types : **periodic** (basé sur intervalle) et **state** (déclenché par événements serveur).

## Placeholders intégrés

| Placeholder | Description |
|-------------|-------------|
| `%server_online%` | Joueurs en ligne actuels |
| `%server_max_players%` | Slots maximum du serveur |
| `%server_unique_joins%` | Total de joueurs uniques ayant rejoint |
| `%server_ram_used%` | RAM utilisée (Mo) |
| `%server_ram_free%` | RAM libre (Mo) |
| `%server_ram_total%` | RAM totale (Mo) |
| `%server_uptime%` | Uptime du serveur |
| `%server_members%` | Nombre de membres du serveur Discord |

## periodic

Met à jour un nom de canal toutes les N secondes en utilisant une chaîne de format avec des placeholders.

```yaml
counters:
  online_players:
    type: periodic
    enabled: true
    channel: "1521280346585305268"
    format: "🟢 En ligne: %server_online%"
    interval: 30
```

| Champ | Type | Description |
|-------|------|-------------|
| `type` | Chaîne | Doit être `periodic` |
| `enabled` | Booléen | Activer ce compteur |
| `channel` | Chaîne | ID du canal Discord à renommer |
| `format` | Chaîne | Nom du canal avec placeholders |
| `interval` | Entier | Intervalle de mise à jour en secondes |

### Debounce et cooldown

Pour éviter les limites de débit de Discord, les renommages de canaux ont un mécanisme de debounce : si le nom résolu n'a pas changé, le renommage est ignoré. De plus, un cooldown de 120 secondes est appliqué entre les renommages par canal. Les compteurs à haute fréquence (ex. `interval: 5`) ne renommeront que lorsque la valeur change réellement.

## state

Réagit aux changements d'état du serveur — renomme le canal immédiatement quand l'état change.

```yaml
counters:
  server_status:
    type: state
    enabled: true
    channel: "1521276573033173204"
    state_key: server_online
    states:
      online: "🟢 En ligne"
      offline: "🔴 Hors ligne"
```

| Champ | Type | Description |
|-------|------|-------------|
| `type` | Chaîne | Doit être `state` |
| `enabled` | Booléen | Activer ce compteur |
| `channel` | Chaîne | ID du canal Discord à renommer |
| `state_key` | Chaîne | État observable à surveiller (`server_online`) |
| `states.online` | Chaîne | Nom du canal quand l'état est `true` |
| `states.offline` | Chaîne | Nom du canal quand l'état est `false` |

## ⚠️ Partage de canal server_status

Le compteur `server_status` partage son `channel` avec `status.yml`. Le compteur renomme le canal, et `status.yml` publie l'embed en ligne/hors ligne. Gardez-les toujours synchronisés — changer le canal dans un fichier nécessite de le changer dans l'autre.
