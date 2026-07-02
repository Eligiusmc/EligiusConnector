---
title: Configuration du Relais Console (console.yml)
description: Diffusez les journaux du serveur sur Discord et exécutez des commandes depuis un canal console.
---

# console.yml

Pont console bidirectionnel : les journaux du serveur sont diffusés vers un canal Discord, et les messages tapés dans ce canal sont exécutés comme commandes serveur (sauf si blacklistés).

## channel

```yaml
channel: "1521272226442383440"
```

ID du canal Discord. Tous les journaux du serveur y sont diffusés. Les messages envoyés par les utilisateurs dans ce canal sont exécutés comme commandes du serveur Minecraft.

## poll_interval

```yaml
poll_interval: 2
```

Fréquence (en secondes) à laquelle le plugin lit les nouvelles lignes de `logs/latest.log`. Des valeurs plus basses donnent une sortie plus en temps réel au prix d'une utilisation CPU légèrement plus élevée.

## command_blacklist

```yaml
command_blacklist:
  - "stop"
  - "restart"
  - "reload"
  - "ban"
  - "ban-ip"
  - "pardon"
  - "pardon-ip"
  - "op"
  - "deop"
  - "say"
  - "gamemode"
  - "whitelist"
  - "kick"
  - "plugins"
```

Les commandes de cette liste ne peuvent pas être exécutées depuis Discord. La vérification est basée sur le préfixe — par exemple, bloquer `ban` bloque aussi `ban-ip` et `banlist`, mais l'entrée explicite `ban-ip` assure la clarté. Les commandes administratives dangereuses (stop, restart, op, deop) sont blacklistées par défaut.

## Formatage

La sortie des journaux dans Discord utilise des blocs de code ` ```ansi ` pour préserver les codes couleur ANSI de la console du serveur, donnant une apparence native de console.
