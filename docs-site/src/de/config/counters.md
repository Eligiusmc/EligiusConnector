---
title: Dynamische Zähler-Konfiguration (counters.yml)
description: Periodische und zustandsbasierte Discord-Kanalnamen-Zähler.
---

# counters.yml

Benennt Discord-Sprach-/Textkanäle dynamisch um. Zwei Typen: **periodic** (intervallbasiert) und **state** (durch Serverereignisse ausgelöst).

## Integrierte Platzhalter

| Platzhalter | Beschreibung |
|-------------|-------------|
| `%server_online%` | Aktuelle Online-Spieler |
| `%server_max_players%` | Maximale Server-Slots |
| `%server_unique_joins%` | Gesamtzahl eindeutiger Spieler |
| `%server_ram_used%` | Verwendeter RAM (MB) |
| `%server_ram_free%` | Freier RAM (MB) |
| `%server_ram_total%` | Gesamter RAM (MB) |
| `%server_uptime%` | Server-Betriebszeit |
| `%server_members%` | Discord-Server-Mitgliederanzahl |

## periodic

Aktualisiert einen Kanalnamen alle N Sekunden mit einem Format-String mit Platzhaltern.

```yaml
counters:
  online_players:
    type: periodic
    enabled: true
    channel: "1521280346585305268"
    format: "🟢 Online: %server_online%"
    interval: 30
```

| Feld | Typ | Beschreibung |
|------|-----|-------------|
| `type` | String | Muss `periodic` sein |
| `enabled` | Boolean | Diesen Zähler aktivieren |
| `channel` | String | Discord-Kanal-ID zum Umbenennen |
| `format` | String | Kanalname mit Platzhaltern |
| `interval` | Integer | Aktualisierungsintervall in Sekunden |

### Entprellung und Cooldown

Um Discord-Rate-Limits zu vermeiden, haben Kanalumbenennungen einen Entprellmechanismus: Wenn sich der aufgelöste Name nicht geändert hat, wird die Umbenennung übersprungen. Zusätzlich wird ein 120-Sekunden-Cooldown zwischen Umbenennungen pro Kanal erzwungen. Hochfrequenz-Zähler (z. B. `interval: 5`) benennen nur um, wenn sich der Wert tatsächlich ändert.

## state

Reagiert auf Serverzustandsänderungen — benennt den Kanal sofort um, wenn der Zustand wechselt.

```yaml
counters:
  server_status:
    type: state
    enabled: true
    channel: "1521276573033173204"
    state_key: server_online
    states:
      online: "🟢 Online"
      offline: "🔴 Offline"
```

| Feld | Typ | Beschreibung |
|------|-----|-------------|
| `type` | String | Muss `state` sein |
| `enabled` | Boolean | Diesen Zähler aktivieren |
| `channel` | String | Discord-Kanal-ID zum Umbenennen |
| `state_key` | String | Zu überwachender beobachtbarer Zustand (`server_online`) |
| `states.online` | String | Kanalname wenn Zustand `true` ist |
| `states.offline` | String | Kanalname wenn Zustand `false` ist |

## ⚠️ server_status-Kanalfreigabe

Der `server_status`-Zähler teilt seinen `channel` mit `status.yml`. Der Zähler benennt den Kanal um, und `status.yml` sendet das Online-/Offline-Embed. Halten Sie sie immer synchron — das Ändern des Kanals in einer Datei erfordert das Ändern in der anderen.
