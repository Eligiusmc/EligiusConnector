---
title: Fehlerbehebung und häufige Fehler
description: Lösungen für häufige EligiusConnector-Fehler und Probleme.
---

# Fehlerbehebung und häufige Fehler

## 🔴 Bot-Token-Probleme

**Fehler:** `Bot token not configured!`

Setzen Sie Ihren Discord-Bot-Token in `config.yml`:
```yaml
bot:
  token: "IHR_TOKEN_HIER"
```

**Fehler:** `Failed to initialize Discord`

Überprüfen Sie:
- Der Token ist korrekt (keine zusätzlichen Leerzeichen)
- Die erforderlichen Intents sind im Discord Developer Portal aktiviert
- Der Bot wurde auf Ihren Server eingeladen

## 🔴 Datenbankfehler

**Fehler:** `no such column: '...'`

Löschen Sie `plugins/EligiusConnector/connector.db` und starten Sie neu. Das Plugin erstellt die Datenbank mit dem korrekten Schema neu. **Dadurch gehen die Daten verknüpfter Konten verloren.**

## 🔴 Verifizierungsprobleme

**Fehler:** `Invalid code or expired`

Codes laufen nach 5 Minuten ab. Generieren Sie einen neuen Code, indem Sie in Discord auf die Schaltfläche **Verify** klicken.

**Fehler:** `Account already linked`

Ihr Discord-Konto ist bereits mit einem Minecraft-Konto verknüpft. Verwenden Sie zuerst `/unlink`.

## 🔴 Rate Limits

**Symptom:** Zähler aktualisieren nicht, Status-Embeds erscheinen nicht

Discord begrenzt die Rate von API-Aufrufen. Wenn Sie den Server häufig zum Testen neu starten, warten Sie 2-3 Minuten zwischen den Neustarts, bis die Rate Limits ablaufen. Im Produktivbetrieb mit normaler Betriebszeit ist dies kein Problem.

## 🔴 Modul funktioniert nicht

**Symptom:** Ein Modul (Chat, Verify usw.) ist deaktiviert

Überprüfen Sie:
1. Die YAML-Datei des Moduls existiert in `plugins/EligiusConnector/`
2. Das Feld `channel:` enthält eine gültige Discord-Kanal-ID (nicht den Platzhalter `CHANNEL_ID`)
3. Das Modul zeigt `ENABLED` im Konsolen-Startprotokoll an

## 🔴 Wirtschafts-Schaltfläche zeigt "Nicht verfügbar"

Installieren Sie **Vault** und **EssentialsX**. Die Wirtschafts-Schaltfläche erfordert ein Vault-kompatibles Wirtschafts-Plugin.

## 🔴 Platzhalter werden als Text angezeigt

Installieren Sie **PlaceholderAPI**. Ohne diese werden `%player_name%`, `%server_online%` und ähnliche Platzhalter nicht aufgelöst.
