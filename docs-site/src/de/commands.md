---
title: Befehle und Berechtigungen
description: Vollständige Liste der Befehle und Berechtigungen für EligiusConnector.
---

# Befehle und Berechtigungen

## Spielerbefehle

| Befehl | Beschreibung | Berechtigung |
|--------|-------------|-------------|
| `/verify <code>` | Verknüpfen Sie Ihr Minecraft-Konto mit Discord | `connector.verify` |
| `/unlink [Spieler]` | Trennen Sie Ihr Konto oder das eines anderen Spielers | `connector.unlink.self` / `.other` |
| `/chat <Nachricht>` | Senden Sie eine Nachricht an den Discord-Chat-Kanal | `connector.chat` |

## Admin-Befehle

| Befehl | Beschreibung | Berechtigung |
|--------|-------------|-------------|
| `/connector reload` | Alle Konfigurationsdateien neu laden | `connector.reload` |
| `/connector status` | Plugin-Status anzeigen (Discord, DB, verknüpfte Konten) | `connector.admin` |
| `/events list` | Verfügbare benutzerdefinierte Ereignisse auflisten | `connector.events` |
| `/events start <id>` | Ein benutzerdefiniertes Ereignis starten | `connector.events` |
| `/events stop <id>` | Ein benutzerdefiniertes Ereignis stoppen | `connector.events` |

## Berechtigungen

| Berechtigung | Standard | Beschreibung |
|-------------|----------|-------------|
| `connector.verify` | `true` | Erlaubt das Verknüpfen eines Kontos via /verify |
| `connector.unlink.self` | `true` | Erlaubt das Trennen des eigenen Kontos |
| `connector.unlink.other` | `op` | Erlaubt das Trennen anderer Spieler |
| `connector.chat` | `true` | Erlaubt die Nutzung des /chat-Befehls |
| `connector.chat.send` | `true` | Erlaubt das Senden von Chat-Nachrichten |
| `connector.reload` | `op` | Erlaubt /connector reload |
| `connector.admin` | `op` | Erlaubt /connector status |
| `connector.events` | `op` | Benutzerdefinierte Ereignisse verwalten |
| `connector.console` | `false` | Befehle vom Discord-Konsolenkanal ausführen |
