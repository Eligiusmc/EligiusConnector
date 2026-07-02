---
title: Verifizierungskonfiguration (verify.yml)
description: Kontoverknüpfungssystem — Verifizierungspanel, Codes, Sounds, Post-Verify-Befehle.
---

# verify.yml

Verwaltet den Minecraft ↔ Discord Verifizierungsablauf. Spieler geben `/verify` im Spiel ein, erhalten einen 6-stelligen alphanumerischen Code und bestätigen ihn über einen Discord-Button.

## channel

```yaml
channel: "1521272231068700702"
```

Discord-Kanal, in dem das Verifizierungspanel beim Start veröffentlicht wird.

## panel

Statisches Panel-Embed mit einem dauerhaften **Verify**-Button.

```yaml
panel:
  embed:
    title: "🔐 Kontoverifizierung"
    description: "Verknüpfen Sie Ihr Minecraft-Konto, um alle Funktionen freizuschalten."
    color: "0x5865F2"
    author: "EligiusMC"
    footer: "EligiusMC"
    timestamp: true
    fields:
      - name: "📋 So verifizieren Sie sich"
        value: |
          1. Betreten Sie den Minecraft-Server
          2. Geben Sie `/verify` im Chat ein
          3. Klicken Sie unten auf **Verify**
          4. Geben Sie den Code von Discord im Spiel ein
        inline: false
  buttons:
    verify: true
```

| Feld | Typ | Beschreibung |
|------|-----|-------------|
| `panel.embed.*` | — | Standard-Embed-Felder (siehe player-events.md) |
| `panel.buttons.verify` | Boolean | Verify-Button anzeigen |

## code

```yaml
code:
  expiry: 5
```

| Feld | Typ | Beschreibung |
|------|-----|-------------|
| `expiry` | Integer | Minuten, bevor ein generierter Code abläuft |

Codes bestehen immer aus 6 alphanumerischen Zeichen (A-Z, 0-9).

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

| Feld | Typ | Beschreibung |
|------|-----|-------------|
| `enabled` | Boolean | Haupt-Sound-Schalter |
| `success.sound` | String | Bukkit-Sound-Key bei erfolgreicher Verifizierung |
| `success.volume` | Float | Lautstärke (0.0–1.0) |
| `success.pitch` | Float | Tonhöhe |
| `error.sound` | String | Bukkit-Sound-Key bei Fehler/Abgelaufenem Code |
| `error.volume` | Float | Fehler-Sound-Lautstärke |
| `error.pitch` | Float | Fehler-Sound-Tonhöhe |

## post_verify

Befehle, die nach erfolgreicher Verifizierung auf dem Minecraft-Server ausgeführt werden.

```yaml
post_verify:
  - "give %player_name% minecraft:diamond 1"
  - "say %player_name% hat sein Konto verifiziert!"
  - "lp user %player_name% parent add verified"
```

| Platzhalter | Wird aufgelöst zu |
|-------------|-------------------|
| `%player_name%` | Minecraft-Benutzername |
| `{player}` | Minecraft-Benutzername |
| `{uuid}` | Spieler-UUID |

Sowohl `%player_name%` (Bukkit-Stil) als auch `{player}` (Klammer-Stil) werden unterstützt.

## code_embed

Ephemeres Embed, das dem Discord-Benutzer beim Klicken auf den **Verify**-Button angezeigt wird.

```yaml
code_embed:
  title: "🔐 Verifizierungscode"
  description: "Verwenden Sie den Code unten, um Ihr Minecraft-Konto zu verknüpfen."
  color: 5814783
  thumbnail: "https://minotar.net/avatar/server/128"
  author: "EligiusMC"
  author_icon: "https://minotar.net/avatar/EligiusMC/128"
  footer: "EligiusMC"
  timestamp: true
  fields:
    - name: "🔑 Ihr Code"
      value: "`{code}`"
      inline: false
```

`{code}` wird durch den generierten 6-stelligen Code ersetzt.

## dm_embed

DM, die nach erfolgreicher Verifizierung an den Discord-Benutzer gesendet wird.

```yaml
dm_embed:
  title: "✅ Konto verknüpft"
  description: "Ihr Discord-Konto wurde mit **{player}** verknüpft!"
  color: 5814783
  thumbnail: "https://minotar.net/avatar/{player}/128"
  footer: "EligiusMC"
  timestamp: true
```

| Platzhalter | Wird aufgelöst zu |
|-------------|-------------------|
| `{player}` | Gerade verknüpfter Minecraft-Benutzername |
