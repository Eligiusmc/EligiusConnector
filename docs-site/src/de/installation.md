---
title: Installation und Voraussetzungen
description: Erfahren Sie, wie Sie EligiusConnector auf Ihrem Minecraft-Server installieren.
---

# Installation und Voraussetzungen

EligiusConnector ist eine umfassende Discord-Minecraft-Bridge. Es verbindet sich über JDA mit Discord und integriert optional mit Vault, LuckPerms und PlaceholderAPI.

## 📋 Systemanforderungen

| Anforderung | Mindestversion | Grund |
|-------------|----------------|-------|
| **Plattform** | Bukkit, Spigot, Paper, Purpur oder Folia 1.21 - 26.1.2+ | Verwendet moderne Paper-API und Brigadier-Befehle |
| **Java** | Java 21 LTS | Quellcode im JDK-21-Format kompiliert |

## 📦 Abhängigkeiten

| Plugin | Erforderlich | Zweck |
|--------|-------------|-------|
| **PlaceholderAPI** | Empfohlen | Aktiviert `%placeholders%` in Embeds und Nachrichten. Ohne werden Platzhalter als Text angezeigt |
| **Vault** | Optional | Kontostand im Profil-Panel (Schaltfläche `/money`) |
| **LuckPerms** | Optional | Rollensynchronisation — Minecraft-Gruppen ↔ Discord-Rollen |
| **EssentialsX** | Optional | Bietet Wirtschafts-Backend für Vault |

## 🚀 Installationsschritte

1. Laden Sie die neueste `EligiusConnector-x.x.x.jar` von der [Releases-Seite](https://github.com/Eligiusmc/EligiusConnector/releases) herunter
2. Kopieren Sie die JAR in den `plugins/`-Ordner Ihres Servers
3. Installieren Sie empfohlene Plugins: **PlaceholderAPI**, **Vault**, **LuckPerms**, **EssentialsX**
4. Starten Sie Ihren Server — alle Konfigurationsdateien werden in `plugins/EligiusConnector/` generiert
5. Bearbeiten Sie `config.yml` und setzen Sie Ihren Discord-Bot-Token
6. Starten Sie den Server neu oder führen Sie `/connector reload` aus

![Startkonsole](/assets/readme.png)

## ⚙️ Ersteinrichtung

Nach dem ersten Start sehen Sie das Eligius-MC-Katzenlogo in der Konsole mit dem Modulstatus. Alle YAML-Konfigurationen werden mit sinnvollen Standardwerten generiert. Die einzige erforderliche Änderung ist der Discord-Bot-Token in `config.yml`:

```yaml
bot:
  token: "IHR_DISCORD_BOT_TOKEN"
  guild_id: "IHRE_DISCORD_SERVER_ID"
```

## 🔌 Discord-Bot-Einrichtung

1. Gehen Sie zum [Discord Developer Portal](https://discord.com/developers/applications)
2. Erstellen Sie eine neue Anwendung → Bot hinzufügen
3. Aktivieren Sie **Message Content Intent**, **Server Members Intent** und **Guild Messages Intent**
4. Kopieren Sie den Bot-Token und fügen Sie ihn in `config.yml` ein
5. Laden Sie den Bot mit den Scopes `bot` und `applications.commands` auf Ihren Server ein
