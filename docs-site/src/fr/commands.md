---
title: Commandes et Permissions
description: Liste complète des commandes et permissions pour EligiusConnector.
---

# Commandes et Permissions

## Commandes Joueur

| Commande | Description | Permission |
|----------|-------------|------------|
| `/verify <code>` | Liez votre compte Minecraft à Discord | `connector.verify` |
| `/unlink [joueur]` | Déliez votre compte ou celui d'un autre joueur | `connector.unlink.self` / `.other` |
| `/chat <message>` | Envoyez un message au canal de chat Discord | `connector.chat` |

## Commandes Admin

| Commande | Description | Permission |
|----------|-------------|------------|
| `/connector reload` | Recharge tous les fichiers de configuration | `connector.reload` |
| `/connector status` | Affiche l'état du plugin (Discord, DB, comptes liés) | `connector.admin` |
| `/events list` | Liste les événements personnalisés disponibles | `connector.events` |
| `/events start <id>` | Démarre un événement personnalisé | `connector.events` |
| `/events stop <id>` | Arrête un événement personnalisé | `connector.events` |

## Permissions

| Permission | Défaut | Description |
|------------|--------|-------------|
| `connector.verify` | `true` | Permet de lier un compte via /verify |
| `connector.unlink.self` | `true` | Permet de délier son propre compte |
| `connector.unlink.other` | `op` | Permet de délier les comptes d'autres joueurs |
| `connector.chat` | `true` | Permet d'utiliser la commande /chat |
| `connector.chat.send` | `true` | Permet d'envoyer des messages de chat |
| `connector.reload` | `op` | Permet /connector reload |
| `connector.admin` | `op` | Permet /connector status |
| `connector.events` | `op` | Gérer les événements personnalisés |
| `connector.console` | `false` | Exécuter des commandes depuis le canal console Discord |
