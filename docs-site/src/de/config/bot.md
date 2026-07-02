---
title: Bot- und Datenbankkonfiguration (config.yml)
description: Hauptkonfiguration des Plugins — Bot-Token, Datenbank, Sprache, Debug.
---

# config.yml

Die Hauptkonfigurationsdatei. Steuert die Bot-Verbindung, den Datenbanktyp, die Sprache und den Debug-Modus.

## bot

```yaml
bot:
  token: "${DISCORD_BOT_TOKEN}"
  guild_id: "508152650978820096"
  status_rotation_minutes: 15
  game_status:
    - "🎮 EligiusMC"
    - "🟢 {online} Spieler"
```

| Feld | Typ | Beschreibung |
|------|-----|-------------|
| `token` | String | Discord-Bot-Token. Verwenden Sie `${ENV_VAR}` für Umgebungsvariablen |
| `guild_id` | String | Discord-Server-ID (Guild) |
| `status_rotation_minutes` | Integer | Wie oft der Bot die `game_status`-Einträge durchläuft |
| `game_status` | Liste | Statusmeldungen im Bot-Profil. `{online}` wird zur aktuellen Spieleranzahl aufgelöst |

## database

```yaml
database:
  type: "sqlite"
  mysql:
    host: "localhost"
    port: 3306
    name: "eligiusconnector"
    username: "root"
    password: ""
    pool_size: 10
```

| Feld | Typ | Beschreibung |
|------|-----|-------------|
| `type` | String | `sqlite` oder `mysql` |
| `mysql.*` | — | MySQL-Verbindungseinstellungen (nur verwendet wenn `type: mysql`) |
| `mysql.pool_size` | Integer | HikariCP-Verbindungspool-Größe |

## language

```yaml
language: "de"
```

Standardsprache für Spieler, die keine Präferenz festgelegt haben. Verfügbar: `en`, `es`, `fr`, `de`, `pt`, `ru`.

## debug

```yaml
debug: false
```

Wenn aktiviert, werden zusätzliche Diagnoseinformationen in der Konsole ausgegeben.
