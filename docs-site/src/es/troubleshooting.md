---
title: Solución de Problemas y Errores Comunes
description: Soluciones para errores y problemas comunes de EligiusConnector.
---

# Solución de Problemas y Errores Comunes

## 🔴 Problemas con el Token del Bot

**Error:** `Bot token not configured!`

Configura el token de tu bot de Discord en `config.yml`:
```yaml
bot:
  token: "TU_TOKEN_AQUI"
```

**Error:** `Failed to initialize Discord`

Verifica que:
- El token sea correcto (sin espacios extra)
- El bot tenga los intents requeridos activados en el Portal de Desarrolladores de Discord
- El bot haya sido invitado a tu servidor

## 🔴 Errores de Base de Datos

**Error:** `no such column: '...'`

Elimina `plugins/EligiusConnector/connector.db` y reinicia. El plugin recreará la base de datos con el esquema correcto. **Esto perderá los datos de cuentas vinculadas.**

## 🔴 Problemas de Verificación

**Error:** `Invalid code or expired`

Los códigos expiran después de 5 minutos. Genera un nuevo código haciendo clic en el botón **Verify** en Discord.

**Error:** `Account already linked`

Tu cuenta de Discord ya está vinculada a una cuenta de Minecraft. Usa `/unlink` primero.

## 🔴 Límites de Tasa (Rate Limits)

**Síntoma:** Los contadores no se actualizan, los embeds de estado no aparecen

Discord impone límites de tasa a las llamadas API. Si estás reiniciando el servidor frecuentemente para pruebas, espera 2-3 minutos entre reinicios para que los rate limits expiren. En producción con tiempo de actividad normal, esto no es un problema.

## 🔴 Módulo No Funciona

**Síntoma:** Un módulo (chat, verify, etc.) está desactivado

Verifica:
1. Que el archivo YAML del módulo exista en `plugins/EligiusConnector/`
2. Que el campo `channel:` tenga un ID de canal de Discord válido (no el placeholder `CHANNEL_ID`)
3. Que el módulo muestre `ENABLED` en el log de inicio de la consola

## 🔴 El Botón de Economía Muestra "Not Available"

Instala **Vault** y **EssentialsX**. El botón de economía requiere un plugin de economía compatible con Vault.

## 🔴 Los Placeholders se Muestran como Texto Literal

Instala **PlaceholderAPI**. Sin él, `%player_name%`, `%server_online%` y placeholders similares no se resolverán.
