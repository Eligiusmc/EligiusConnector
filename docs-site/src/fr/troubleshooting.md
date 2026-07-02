---
title: Dépannage et Erreurs Courantes
description: Solutions pour les erreurs et problèmes courants d'EligiusConnector.
---

# Dépannage et Erreurs Courantes

## 🔴 Problèmes de Token du Bot

**Erreur :** `Bot token not configured!`

Définissez votre token de bot Discord dans `config.yml` :
```yaml
bot:
  token: "VOTRE_TOKEN_ICI"
```

**Erreur :** `Failed to initialize Discord`

Vérifiez que :
- Le token est correct (pas d'espaces supplémentaires)
- Les intents requis sont activés dans le Portail Développeur Discord
- Le bot a été invité sur votre serveur

## 🔴 Erreurs de Base de Données

**Erreur :** `no such column: '...'`

Supprimez `plugins/EligiusConnector/connector.db` et redémarrez. Le plugin recréera la base de données avec le schéma correct. **Cela supprimera les données des comptes liés.**

## 🔴 Problèmes de Vérification

**Erreur :** `Invalid code or expired`

Les codes expirent après 5 minutes. Générez un nouveau code en cliquant sur le bouton **Verify** dans Discord.

**Erreur :** `Account already linked`

Votre compte Discord est déjà lié à un compte Minecraft. Utilisez d'abord `/unlink`.

## 🔴 Limites de Débit

**Symptôme :** Les compteurs ne se mettent pas à jour, les embeds de statut n'apparaissent pas

Discord limite le débit des appels API. Si vous redémarrez fréquemment le serveur pour les tests, attendez 2-3 minutes entre les redémarrages pour que les limites expirent. En production avec une disponibilité normale, ce n'est pas un problème.

## 🔴 Module Non Fonctionnel

**Symptôme :** Un module (chat, verify, etc.) est désactivé

Vérifiez :
1. Le fichier YAML du module existe dans `plugins/EligiusConnector/`
2. Le champ `channel:` contient un ID de canal Discord valide (pas le placeholder `CHANNEL_ID`)
3. Le module affiche `ENABLED` dans le journal de démarrage de la console

## 🔴 Le Bouton Économie Affiche "Non Disponible"

Installez **Vault** et **EssentialsX**. Le bouton économie nécessite un plugin économique compatible Vault.

## 🔴 Les Placeholders S'affichent en Texte Brut

Installez **PlaceholderAPI**. Sans lui, `%player_name%`, `%server_online%` et les placeholders similaires ne seront pas résolus.
