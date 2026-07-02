---
title: Configuration de Synchronisation (synchronization.yml)
description: Synchronisation bidirectionnelle des groupes Minecraft ↔ rôles Discord, et des pseudos.
---

# synchronization.yml

Deux systèmes de synchronisation : **roles** reflète les groupes LuckPerms vers les rôles Discord, **nickname** définit les pseudos Discord à partir des noms Minecraft. Les deux sont bidirectionnels — les rôles sont attribués et retirés selon les changements de groupes.

Nécessite Vault et LuckPerms sur le serveur (pour la détection des groupes).

## Interrupteur principal

```yaml
enabled: true
```

Définir sur `false` pour désactiver tous les systèmes de synchronisation en une fois.

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

| Champ | Type | Description |
|-------|------|-------------|
| `enabled` | Booléen | Activer la synchro des rôles |
| `interval` | Entier | Fréquence de vérification et synchronisation (secondes) |
| `mapping` | Map | Nom du groupe LuckPerms → ID du rôle Discord |

La clé du mapping est le nom du groupe LuckPerms, la valeur est l'ID du rôle Discord. Quand un joueur lié a un groupe LuckPerms mappé, le bot attribue le rôle Discord correspondant. Quand le groupe est retiré (ou le joueur délié), le rôle est retiré. Chaque joueur peut avoir plusieurs rôles provenant de plusieurs groupes mappés.

## nickname

```yaml
nickname:
  enabled: true
  interval: 30
  format: "%player_name%"
```

| Champ | Type | Description |
|-------|------|-------------|
| `enabled` | Booléen | Activer la synchro des pseudos |
| `interval` | Entier | Fréquence de vérification et mise à jour (secondes) |
| `format` | Chaîne | Format du pseudo |

Le champ `format` supporte le placeholder `%player_name%`. Le bot définit le pseudo Discord de chaque membre lié au format résolu à chaque cycle de synchronisation.
