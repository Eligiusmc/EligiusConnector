---
title: Serverstatus-Konfiguration (status.yml)
description: Online-/Offline-Status-Embeds, die beim Serverstart und -stopp gesendet werden.
---

# status.yml

Sendet ein Embed an einen Discord-Kanal, wenn der Server startet und stoppt. Beim Start wird das vorherige Online-Embed gelöscht und ein neues gesendet. Beim Herunterfahren werden alle Bot-Nachrichten gelöscht und ein Offline-Embed synchron gesendet.

## ⚠️ Geteilter Kanal mit counters.yml

Der `channel` in dieser Datei **muss** mit dem `server_status`-Zählerkanal in `counters.yml` übereinstimmen. Der Zähler benennt den Kanal um (z. B. `🟢 Online` / `🔴 Offline`), und diese Datei sendet das Status-Embed. Ändern Sie nicht eine, ohne die andere zu überprüfen.

## channel

```yaml
channel: "1521276573033173204"
```

Discord-Kanal-ID, die sowohl für das Status-Embed als auch den `server_status`-Zähler verwendet wird.

## online

```yaml
online:
  title: "🟢 Server Online"
  description: "Der Minecraft-Server ist jetzt **online** und bereit zum Spielen!"
  color: 5763719
  thumbnail: "https://minotar.net/avatar/server/128"
  author: "EligiusMC Status"
  author_icon: "https://minotar.net/avatar/EligiusMC/128"
  footer: "Server gestartet"
  timestamp: true
  fields:
    - name: "📋 Server"
      value: "EligiusMC"
      inline: true
    - name: "🎮 Spielmodus"
      value: "Survival"
      inline: true
    - name: "🌍 Welt"
      value: "Oberwelt"
      inline: true
    - name: "📢 Info"
      value: "Tritt bei und starte dein Abenteuer!"
      inline: false
```

| Feld | Typ | Beschreibung |
|------|-----|-------------|
| `title` | String | Embed-Titel |
| `description` | String | Embed-Beschreibung (Markdown) |
| `color` | Integer | Embed-Farbe (dezimal) |
| `thumbnail` | String | Vorschaubild-URL |
| `author` | String | Autorenname |
| `author_icon` | String | Autorensymbol-URL |
| `footer` | String | Fußzeilentext |
| `timestamp` | Boolean | Zeitstempel anzeigen |
| `fields` | Liste | Inline-Embed-Felder |

## offline

```yaml
offline:
  title: "🔴 Server Offline"
  description: "Der Minecraft-Server ist derzeit wegen Wartung **offline**."
  color: 15548997
  thumbnail: "https://minotar.net/avatar/server/128"
  author: "EligiusMC Status"
  author_icon: "https://minotar.net/avatar/EligiusMC/128"
  footer: "Wir sind bald zurück"
  timestamp: true
  fields:
    - name: "📋 Status"
      value: "In Wartung"
      inline: true
    - name: "⏰ ETA"
      value: "Demnächst"
      inline: true
```

Gleiche Struktur wie `online`. Das Offline-Embed wird synchron während `onDisable` gesendet, um die Zustellung vor dem Herunterfahren des JDA-Clients zu garantieren.
