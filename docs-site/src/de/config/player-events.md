---
title: Spielerereignis-Konfiguration (player_events.yml)
description: Konfigurieren Sie Discord-Embeds für Beitritte, Verlassen, Tode und Fortschritte.
---

# player_events.yml

Sendet Discord-Embeds für Minecraft-Spielerereignisse. Jedes Ereignis ist unabhängig ein-/ausschaltbar — setzen Sie `enabled: false` bei einem Ereignis, um nur dieses zu deaktivieren. Das Löschen dieser Datei deaktiviert alle Ereignisse.

## Platzhalter

| Platzhalter | Ereignisse | Wird aufgelöst zu |
|-------------|-----------|-------------------|
| `{player}` | Alle | Spielername |
| `{online}` | join, leave | Aktuelle Online-Spieleranzahl |
| `{max}` | join, leave | Maximale Server-Slots |
| `{first_join}` | join | `"Yes"` oder `"No"` |
| `{death_message}` | death | Rohe Todesnachricht |
| `{health}` | death | Spielergesundheit |
| `{food}` | death | Nahrungslevel des Spielers |
| `{world}` | death | Weltname |
| `{advancement}` | advancement | Fortschrittstitel |

## join

```yaml
join:
  enabled: true
  channel: "1521276575889358960"
  embed:
    color: 5763719
    author: "{player} hat den Server betreten"
    author_icon: "https://minotar.net/avatar/{player}/128"
    thumbnail: "https://minotar.net/avatar/{player}/128"
    title: "Willkommen auf EligiusMC!"
    description: "Sagt alle Hallo zu **{player}**!"
    footer: "EligiusMC"
    timestamp: true
    fields:
      - name: "Online"
        value: "**{online}** / **{max}**"
        inline: true
      - name: "Erstes Mal"
        value: "{first_join}"
        inline: true
```

| Feld | Typ | Beschreibung |
|------|-----|-------------|
| `enabled` | Boolean | Aktiviert Beitritts-Embeds |
| `channel` | String | Discord-Kanal-ID |
| `embed.color` | Integer | Embed-Farbe (dezimal) |
| `embed.author` | String | Autorenzeile (unterstützt Platzhalter) |
| `embed.author_icon` | String | URL für Autorensymbol |
| `embed.thumbnail` | String | URL für Vorschaubild |
| `embed.title` | String | Embed-Titel |
| `embed.description` | String | Embed-Beschreibung (Markdown) |
| `embed.footer` | String | Fußzeilentext |
| `embed.timestamp` | Boolean | Zeitstempel anzeigen |
| `embed.fields` | Liste | Zusätzliche Felder (`name`, `value`, `inline`) |

## leave

Gleiche Struktur wie join. Standardmäßig ein Abschieds-Embed mit Spielerkopf und Online-Anzahl.

| Feld | Standardfarbe |
|------|--------------|
| `color` | `15548997` (rot) |
| `author` | `"{player} hat den Server verlassen"` |

## death

```yaml
death:
  enabled: true
  channel: "1521276578708066578"
  embed:
    color: 15548997
    author: "{player} ist gestorben"
    title: "{death_message}"
    fields:
      - name: "Gesundheit"
        value: "❤️ {health} HP"
        inline: true
      - name: "Hunger"
        value: "🍖 {food} / 20"
        inline: true
      - name: "Welt"
        value: "🌍 {world}"
        inline: true
```

`{death_message}` zeigt die rohe Todesnachricht aus Minecraft an. Zusätzliche todesspezifische Felder zeigen Gesundheit, Hunger und Welt.

## advancement

```yaml
advancement:
  enabled: true
  channel: "1521276581522444350"
  embed:
    color: 16766720
    author: "Fortschritt erzielt!"
    title: "{advancement}"
    description: "**{player}** hat diesen Fortschritt erzielt!"
```

`{advancement}` wird zum Fortschrittstitel aufgelöst.
