---
title: Configuration des Événements Joueur (player_events.yml)
description: Configurez les embeds Discord pour les arrivées, départs, morts et progrès.
---

# player_events.yml

Envoie des embeds Discord pour les événements des joueurs Minecraft. Chaque événement est activable indépendamment — définissez `enabled: false` sur un événement pour le désactiver. Supprimer ce fichier désactive tous les événements.

## Placeholders

| Placeholder | Événements | Se résout en |
|-------------|-----------|-------------|
| `{player}` | Tous | Nom du joueur |
| `{online}` | join, leave | Nombre actuel de joueurs en ligne |
| `{max}` | join, leave | Slots maximum du serveur |
| `{first_join}` | join | `"Yes"` ou `"No"` |
| `{death_message}` | death | Message de mort brut |
| `{health}` | death | Santé du joueur |
| `{food}` | death | Niveau de nourriture du joueur |
| `{world}` | death | Nom du monde |
| `{advancement}` | advancement | Titre du progrès |

## join

```yaml
join:
  enabled: true
  channel: "1521276575889358960"
  embed:
    color: 5763719
    author: "{player} a rejoint le serveur"
    author_icon: "https://minotar.net/avatar/{player}/128"
    thumbnail: "https://minotar.net/avatar/{player}/128"
    title: "Bienvenue sur EligiusMC !"
    description: "Tout le monde dit bonjour à **{player}** !"
    footer: "EligiusMC"
    timestamp: true
    fields:
      - name: "En ligne"
        value: "**{online}** / **{max}**"
        inline: true
      - name: "Première fois"
        value: "{first_join}"
        inline: true
```

| Champ | Type | Description |
|-------|------|-------------|
| `enabled` | Booléen | Active les embeds d'arrivée |
| `channel` | Chaîne | ID du canal Discord |
| `embed.color` | Entier | Couleur de l'embed (décimal) |
| `embed.author` | Chaîne | Ligne d'auteur (supporte les placeholders) |
| `embed.author_icon` | Chaîne | URL de l'icône d'auteur |
| `embed.thumbnail` | Chaîne | URL de la vignette |
| `embed.title` | Chaîne | Titre de l'embed |
| `embed.description` | Chaîne | Description de l'embed (Markdown) |
| `embed.footer` | Chaîne | Texte du pied de page |
| `embed.timestamp` | Booléen | Afficher l'horodatage |
| `embed.fields` | Liste | Champs supplémentaires (`name`, `value`, `inline`) |

## leave

Même structure que join. Par défaut, un embed d'adieu avec la tête du joueur et le nombre de joueurs en ligne.

| Champ | Couleur par défaut |
|-------|-------------------|
| `color` | `15548997` (rouge) |
| `author` | `"{player} a quitté le serveur"` |

## death

```yaml
death:
  enabled: true
  channel: "1521276578708066578"
  embed:
    color: 15548997
    author: "{player} est mort"
    title: "{death_message}"
    fields:
      - name: "Santé"
        value: "❤️ {health} PV"
        inline: true
      - name: "Faim"
        value: "🍖 {food} / 20"
        inline: true
      - name: "Monde"
        value: "🌍 {world}"
        inline: true
```

`{death_message}` affiche le message de mort brut de Minecraft. Les champs supplémentaires montrent la santé, la faim et le monde.

## advancement

```yaml
advancement:
  enabled: true
  channel: "1521276581522444350"
  embed:
    color: 16766720
    author: "Progrès accompli !"
    title: "{advancement}"
    description: "**{player}** a obtenu ce progrès !"
```

`{advancement}` se résout en titre du progrès.
