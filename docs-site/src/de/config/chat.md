---
title: Chat-Bridge-Konfiguration (chat.yml)
description: Konfigurieren Sie die Discord ↔ Minecraft Chat-Bridge, Webhooks und Filter.
---

# chat.yml

Steuert die bidirektionale Chat-Bridge, das Webhook-Erscheinungsbild und Anti-Missbrauch-Filter.

## channel

```yaml
channel: "1521272219223851028"
```

Der Discord-Kanal, in dem MC ↔ DC Nachrichten erscheinen und wohin `/chat` Nachrichten sendet.

## bridge

```yaml
bridge:
  mc_to_discord: true
  discord_to_mc: true
```

| Feld | Beschreibung |
|------|-------------|
| `mc_to_discord` | Sendet Minecraft-Chat an Discord |
| `discord_to_mc` | Sendet Discord-Nachrichten an den Minecraft-Chat |

## format

```yaml
format:
  mc_to_discord: "{player}: {message}"
  discord_to_mc: "[DC] {player}: {message}"
```

| Platzhalter | Wird aufgelöst zu |
|-------------|-------------------|
| `{player}` | Spielername |
| `{message}` | Chat-Nachrichteninhalt |

## webhook

```yaml
webhook:
  enabled: true
  name: "Minecraft"
  avatar: "https://minotar.net/avatar/%player_name%/128"
```

Verwendet Discord-Webhooks für MC → DC Nachrichten und zeigt Spielerköpfe und Nicknamen an.

## filter

```yaml
filter:
  enabled: true
```

Hauptschalter, um alle Filter auf einmal zu deaktivieren.

### filter.spam

| Feld | Standard | Beschreibung |
|------|----------|-------------|
| `enabled` | `true` | Aktiviert Anti-Spam |
| `max_messages` | `5` | Erlaubte Nachrichten im Zeitfenster |
| `time_window_seconds` | `10` | Zeitfenster in Sekunden |

### filter.profanity

| Feld | Beschreibung |
|------|-------------|
| `enabled` | Aktiviert den Obszönitätenfilter |
| `words` | Liste gesperrter Wörter (Groß-/Kleinschreibung ignorierend) |

### filter.links

| Feld | Beschreibung |
|------|-------------|
| `enabled` | Aktiviert den Linkfilter |
| `mode` | `whitelist` (nur aufgelistete Domains erlaubt) oder `blacklist` (aufgelistete Domains gesperrt) |
| `domains` | Domain-Liste für den ausgewählten Modus |

### filter.caps

| Feld | Standard | Beschreibung |
|------|----------|-------------|
| `enabled` | `true` | Aktiviert den Großbuchstabenfilter |
| `max_percentage` | `70` | Maximaler Prozentsatz an Großbuchstaben |
| `min_length` | `10` | Minimale Nachrichtenlänge zur Prüfung |

### filter.punishment

```yaml
punishment:
  enabled: true
  thresholds:
    2: "mute:300"
    5: "tempban:3600"
    10: "banip"
```

Eskalierende Strafen für Wiederholungstäter. Aktionen: `warn`, `delete`, `mute:<Sekunden>`, `tempban:<Sekunden>`, `banip`.
