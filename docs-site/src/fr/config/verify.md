---
title: Configuration de Vérification (verify.yml)
description: Système de liaison de comptes — panneau de vérification, codes, sons, commandes post-vérification.
---

# verify.yml

Gère le flux de vérification de compte Minecraft ↔ Discord. Les joueurs tapent `/verify` en jeu, reçoivent un code alphanumérique de 6 caractères et le confirment via un bouton Discord.

## channel

```yaml
channel: "1521272231068700702"
```

Canal Discord où le panneau de vérification est publié au démarrage.

## panel

Embed de panneau statique avec un bouton **Verify** persistant.

```yaml
panel:
  embed:
    title: "🔐 Vérification de Compte"
    description: "Liez votre compte Minecraft pour débloquer toutes les fonctionnalités."
    color: "0x5865F2"
    author: "EligiusMC"
    footer: "EligiusMC"
    timestamp: true
    fields:
      - name: "📋 Comment se vérifier"
        value: |
          1. Rejoignez le serveur Minecraft
          2. Tapez `/verify` dans le chat
          3. Cliquez sur **Verify** ci-dessous
          4. Entrez le code Discord dans le jeu
        inline: false
  buttons:
    verify: true
```

| Champ | Type | Description |
|-------|------|-------------|
| `panel.embed.*` | — | Champs d'embed standard (voir player-events.md) |
| `panel.buttons.verify` | Booléen | Afficher le bouton Verify |

## code

```yaml
code:
  expiry: 5
```

| Champ | Type | Description |
|-------|------|-------------|
| `expiry` | Entier | Minutes avant expiration d'un code généré |

Les codes sont toujours composés de 6 caractères alphanumériques (A-Z, 0-9).

## sounds

```yaml
sounds:
  enabled: true
  success:
    sound: "entity.player.levelup"
    volume: 1.0
    pitch: 1.0
  error:
    sound: "entity.villager.no"
    volume: 1.0
    pitch: 1.0
```

| Champ | Type | Description |
|-------|------|-------------|
| `enabled` | Booléen | Interrupteur principal des sons |
| `success.sound` | Chaîne | Clé de son Bukkit en cas de succès |
| `success.volume` | Flottant | Volume du son (0.0–1.0) |
| `success.pitch` | Flottant | Hauteur du son |
| `error.sound` | Chaîne | Clé de son Bukkit en cas d'échec/expiration |
| `error.volume` | Flottant | Volume du son d'erreur |
| `error.pitch` | Flottant | Hauteur du son d'erreur |

## post_verify

Commandes exécutées sur le serveur Minecraft après une vérification réussie.

```yaml
post_verify:
  - "give %player_name% minecraft:diamond 1"
  - "say %player_name% a vérifié son compte !"
  - "lp user %player_name% parent add verified"
```

| Placeholder | Se résout en |
|-------------|-------------|
| `%player_name%` | Nom d'utilisateur Minecraft |
| `{player}` | Nom d'utilisateur Minecraft |
| `{uuid}` | UUID du joueur |

Les styles `%player_name%` (Bukkit) et `{player}` (accolades) sont tous deux supportés.

## code_embed

Embed éphémère montré à l'utilisateur Discord quand il clique sur le bouton **Verify**.

```yaml
code_embed:
  title: "🔐 Code de Vérification"
  description: "Utilisez le code ci-dessous pour lier votre compte Minecraft."
  color: 5814783
  thumbnail: "https://minotar.net/avatar/server/128"
  author: "EligiusMC"
  author_icon: "https://minotar.net/avatar/EligiusMC/128"
  footer: "EligiusMC"
  timestamp: true
  fields:
    - name: "🔑 Votre Code"
      value: "`{code}`"
      inline: false
```

`{code}` est remplacé par le code à 6 caractères généré.

## dm_embed

DM envoyé à l'utilisateur Discord après une vérification réussie.

```yaml
dm_embed:
  title: "✅ Compte Lié"
  description: "Votre compte Discord a été lié à **{player}** !"
  color: 5814783
  thumbnail: "https://minotar.net/avatar/{player}/128"
  footer: "EligiusMC"
  timestamp: true
```

| Placeholder | Se résout en |
|-------------|-------------|
| `{player}` | Nom d'utilisateur Minecraft qui vient d'être lié |
