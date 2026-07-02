---
title: Configuration du Statut du Serveur (status.yml)
description: Embeds de statut en ligne/hors ligne publiés au démarrage et à l'arrêt du serveur.
---

# status.yml

Publie un embed dans un canal Discord au démarrage et à l'arrêt du serveur. Au démarrage, l'embed en ligne précédent est supprimé et un nouveau est publié. À l'arrêt, tous les messages du bot sont effacés et un embed hors ligne est publié de manière synchrone.

## ⚠️ Canal partagé avec counters.yml

Le `channel` dans ce fichier **doit correspondre** au canal du compteur `server_status` dans `counters.yml`. Le compteur renomme le canal (ex. `🟢 Online` / `🔴 Offline`), et ce fichier publie l'embed de statut. Ne modifiez pas l'un sans vérifier l'autre.

## channel

```yaml
channel: "1521276573033173204"
```

ID du canal Discord utilisé à la fois pour l'embed de statut et le compteur `server_status`.

## online

```yaml
online:
  title: "🟢 Serveur En Ligne"
  description: "Le serveur Minecraft est maintenant **en ligne** et prêt à jouer !"
  color: 5763719
  thumbnail: "https://minotar.net/avatar/server/128"
  author: "Statut EligiusMC"
  author_icon: "https://minotar.net/avatar/EligiusMC/128"
  footer: "Serveur démarré"
  timestamp: true
  fields:
    - name: "📋 Serveur"
      value: "EligiusMC"
      inline: true
    - name: "🎮 Mode de jeu"
      value: "Survie"
      inline: true
    - name: "🌍 Monde"
      value: "Overworld"
      inline: true
    - name: "📢 Info"
      value: "Rejoignez-nous et commencez votre aventure !"
      inline: false
```

| Champ | Type | Description |
|-------|------|-------------|
| `title` | Chaîne | Titre de l'embed |
| `description` | Chaîne | Description de l'embed (Markdown) |
| `color` | Entier | Couleur de l'embed (décimal) |
| `thumbnail` | Chaîne | URL de la vignette |
| `author` | Chaîne | Nom de l'auteur |
| `author_icon` | Chaîne | URL de l'icône d'auteur |
| `footer` | Chaîne | Texte du pied de page |
| `timestamp` | Booléen | Afficher l'horodatage |
| `fields` | Liste | Champs d'embed en ligne |

## offline

```yaml
offline:
  title: "🔴 Serveur Hors Ligne"
  description: "Le serveur Minecraft est actuellement **hors ligne** pour maintenance."
  color: 15548997
  thumbnail: "https://minotar.net/avatar/server/128"
  author: "Statut EligiusMC"
  author_icon: "https://minotar.net/avatar/EligiusMC/128"
  footer: "Nous serons bientôt de retour"
  timestamp: true
  fields:
    - name: "📋 Statut"
      value: "En maintenance"
      inline: true
    - name: "⏰ ETA"
      value: "Bientôt"
      inline: true
```

Même structure que `online`. L'embed hors ligne est envoyé de manière synchrone pendant `onDisable` pour garantir sa livraison avant l'arrêt du client JDA.
