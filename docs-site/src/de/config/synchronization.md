---
title: Synchronisationskonfiguration (synchronization.yml)
description: Bidirektionale Synchronisation von Minecraft-Gruppen ↔ Discord-Rollen und Nicknamen.
---

# synchronization.yml

Zwei Synchronisationssysteme: **roles** spiegelt LuckPerms-Gruppen auf Discord-Rollen, **nickname** setzt Discord-Nicknamen aus Minecraft-Benutzernamen. Beide sind bidirektional — Rollen werden zugewiesen und entfernt, wenn sich Gruppen ändern.

Erfordert Vault und LuckPerms auf dem Server (zur Gruppenerkennung).

## Hauptschalter

```yaml
enabled: true
```

Auf `false` setzen, um alle Synchronisationssysteme auf einmal zu deaktivieren.

## roles

```yaml
roles:
  enabled: true
  interval: 30
  mapping:
    verified: "1521342858945101894"
    # testing: "ROLE_ID_HERE"
    # vip: "ROLE_ID_HERE"
```

| Feld | Typ | Beschreibung |
|------|-----|-------------|
| `enabled` | Boolean | Rollensynchronisation aktivieren |
| `interval` | Integer | Wie oft geprüft und synchronisiert wird (Sekunden) |
| `mapping` | Map | LuckPerms-Gruppenname → Discord-Rollen-ID |

Der Mapping-Schlüssel ist der LuckPerms-Gruppenname, der Wert die Discord-Rollen-ID. Wenn ein verknüpfter Spieler eine zugeordnete LuckPerms-Gruppe hat, weist der Bot die entsprechende Discord-Rolle zu. Wenn die Gruppe entfernt wird (oder der Spieler die Verknüpfung aufhebt), wird die Rolle entfernt. Jeder Spieler kann mehrere Rollen aus mehreren zugeordneten Gruppen haben.

## nickname

```yaml
nickname:
  enabled: true
  interval: 30
  format: "%player_name%"
```

| Feld | Typ | Beschreibung |
|------|-----|-------------|
| `enabled` | Boolean | Nickname-Synchronisation aktivieren |
| `interval` | Integer | Wie oft geprüft und aktualisiert wird (Sekunden) |
| `format` | String | Nickname-Format |

Das `format`-Feld unterstützt den `%player_name%`-Platzhalter. Der Bot setzt den Discord-Nicknamen jedes verknüpften Mitglieds in jedem Synchronisationszyklus auf das aufgelöste Format.
