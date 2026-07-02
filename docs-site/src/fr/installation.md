---
title: Installation et Prérequis
description: Apprenez à installer EligiusConnector sur votre serveur Minecraft.
---

# Installation et Prérequis

EligiusConnector est un pont Discord-Minecraft complet. Il se connecte à Discord via JDA et s'intègre optionnellement avec Vault, LuckPerms et PlaceholderAPI.

## 📋 Configuration Minimale

| Prérequis | Version Minimale | Raison |
|-----------|-----------------|--------|
| **Plateforme** | Bukkit, Spigot, Paper, Purpur ou Folia 1.21 - 26.1.2+ | Utilise l'API Paper moderne et les commandes Brigadier |
| **Java** | Java 21 LTS | Code source compilé au format JDK 21 |

## 📦 Dépendances

| Plugin | Requis | But |
|--------|--------|-----|
| **PlaceholderAPI** | Recommandé | Active les `%placeholders%` dans les embeds et messages. Sans lui, les placeholders s'affichent en texte brut |
| **Vault** | Optionnel | Solde économique dans le panneau de profil (bouton `/money`) |
| **LuckPerms** | Optionnel | Synchronisation des rôles — groupes Minecraft ↔ rôles Discord |
| **EssentialsX** | Optionnel | Fournit le backend économique pour Vault |

## 🚀 Étapes d'Installation

1. Téléchargez le dernier `EligiusConnector-x.x.x.jar` depuis la [page Releases](https://github.com/Eligiusmc/EligiusConnector/releases)
2. Copiez le JAR dans le dossier `plugins/` de votre serveur
3. Installez les plugins recommandés : **PlaceholderAPI**, **Vault**, **LuckPerms**, **EssentialsX**
4. Démarrez votre serveur — tous les fichiers de configuration seront générés dans `plugins/EligiusConnector/`
5. Modifiez `config.yml` et définissez votre token de bot Discord
6. Redémarrez le serveur ou exécutez `/connector reload`

![Console de démarrage](/assets/readme.png)

## ⚙️ Configuration Initiale

Après le premier démarrage, vous verrez le logo du chat Eligius MC dans la console avec l'état des modules. Toutes les configurations YAML sont générées avec des valeurs par défaut raisonnables. Le seul changement obligatoire est le token du bot Discord dans `config.yml` :

```yaml
bot:
  token: "VOTRE_TOKEN_BOT_DISCORD"
  guild_id: "VOTRE_ID_SERVEUR_DISCORD"
```

## 🔌 Configuration du Bot Discord

1. Allez sur le [Portail Développeur Discord](https://discord.com/developers/applications)
2. Créez une nouvelle application → Ajoutez un bot
3. Activez **Message Content Intent**, **Server Members Intent** et **Guild Messages Intent**
4. Copiez le token du bot et collez-le dans `config.yml`
5. Invitez le bot sur votre serveur avec les scopes `bot` et `applications.commands`
