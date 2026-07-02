---
title: Konsolen-Relay-Konfiguration (console.yml)
description: Streamen Sie Server-Logs an Discord und führen Sie Befehle von einem Konsolenkanal aus.
---

# console.yml

Zwei-Wege-Konsolen-Bridge: Server-Logs werden an einen Discord-Kanal gestreamt, und in diesem Kanal eingegebene Nachrichten werden als Serverbefehle ausgeführt (sofern nicht auf der Blacklist).

## channel

```yaml
channel: "1521272226442383440"
```

Discord-Kanal-ID. Alle Server-Logs werden hierher gestreamt. Von Benutzern in diesem Kanal gesendete Nachrichten werden als Minecraft-Serverbefehle ausgeführt.

## poll_interval

```yaml
poll_interval: 2
```

Wie oft (in Sekunden) das Plugin neue Zeilen aus `logs/latest.log` liest. Niedrigere Werte geben eine Echtzeit-ähnlichere Ausgabe auf Kosten einer etwas höheren CPU-Auslastung.

## command_blacklist

```yaml
command_blacklist:
  - "stop"
  - "restart"
  - "reload"
  - "ban"
  - "ban-ip"
  - "pardon"
  - "pardon-ip"
  - "op"
  - "deop"
  - "say"
  - "gamemode"
  - "whitelist"
  - "kick"
  - "plugins"
```

Befehle in dieser Liste können nicht von Discord aus ausgeführt werden. Die Prüfung ist präfixbasiert — z. B. blockiert `ban` auch `ban-ip` und `banlist`, aber der explizite Eintrag `ban-ip` sorgt für Klarheit. Gefährliche administrative Befehle (stop, restart, op, deop) sind standardmäßig auf der Blacklist.

## Formatierung

Die Log-Ausgabe in Discord verwendet ` ```ansi `-Codeblöcke, um ANSI-Farbcodes von der Serverkonsole zu erhalten, was ein natives Konsolen-Erscheinungsbild ergibt.
