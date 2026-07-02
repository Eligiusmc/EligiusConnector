---
title: Configuración de Verificación (verify.yml)
description: Sistema de vinculación de cuentas — panel de verificación, códigos, sonidos, comandos post-verificación.
---

# verify.yml

Gestiona el flujo de verificación de cuentas Minecraft ↔ Discord. Los jugadores escriben `/verify` en el juego, reciben un código alfanumérico de 6 caracteres y lo confirman mediante un botón en Discord.

## channel

```yaml
channel: "1521272231068700702"
```

Canal de Discord donde se publica el panel de verificación al iniciar.

## panel

Embed estático del panel con un botón persistente de **Verify**.

```yaml
panel:
  embed:
    title: "🔐 Account Verification"
    description: "Link your Minecraft account to unlock all features."
    color: "0x5865F2"
    author: "EligiusMC"
    footer: "EligiusMC"
    timestamp: true
    fields:
      - name: "📋 How to verify"
        value: |
          1. Join the Minecraft server
          2. Type `/verify` in chat
          3. Click **Verify** below
          4. Enter the code from Discord in-game
        inline: false
  buttons:
    verify: true
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `panel.embed.*` | — | Campos estándar de embed (ver player-events.md) |
| `panel.buttons.verify` | Boolean | Mostrar el botón Verify |

## code

```yaml
code:
  expiry: 5
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `expiry` | Integer | Minutos antes de que un código generado expire |

Los códigos siempre son 6 caracteres alfanuméricos (A-Z, 0-9).

## sounds

```yaml
sounds:
  enabled: true
  success:
    sound: "entity.player.levelup"
    volume: 1.0
    pitch: 1.0
  error:
    sound: "entity.villager.no"
    volume: 1.0
    pitch: 1.0
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `enabled` | Boolean | Interruptor maestro de sonidos |
| `success.sound` | String | Clave de sonido Bukkit al verificar con éxito |
| `success.volume` | Float | Volumen del sonido (0.0–1.0) |
| `success.pitch` | Float | Tono del sonido |
| `error.sound` | String | Clave de sonido Bukkit al fallar/código expirado |
| `error.volume` | Float | Volumen del sonido de error |
| `error.pitch` | Float | Tono del sonido de error |

## post_verify

Comandos ejecutados en el servidor Minecraft después de una verificación exitosa.

```yaml
post_verify:
  - "give %player_name% minecraft:diamond 1"
  - "say %player_name% has verified their account!"
  - "lp user %player_name% parent add verified"
```

| Placeholder | Se resuelve a |
|-------------|---------------|
| `%player_name%` | Nombre de usuario de Minecraft |
| `{player}` | Nombre de usuario de Minecraft |
| `{uuid}` | UUID del jugador |

Tanto `%player_name%` (estilo Bukkit) como `{player}` (estilo llaves) son compatibles.

## code_embed

Embed efímero mostrado al usuario de Discord cuando hace clic en el botón **Verify**.

```yaml
code_embed:
  title: "🔐 Verification Code"
  description: "Use the code below to link your Minecraft account."
  color: 5814783
  thumbnail: "https://minotar.net/avatar/server/128"
  author: "EligiusMC"
  author_icon: "https://minotar.net/avatar/EligiusMC/128"
  footer: "EligiusMC"
  timestamp: true
  fields:
    - name: "🔑 Your Code"
      value: "`{code}`"
      inline: false
```

`{code}` se reemplaza con el código de 6 caracteres generado.

## dm_embed

DM enviado al usuario de Discord después de una verificación exitosa.

```yaml
dm_embed:
  title: "✅ Account Linked"
  description: "Your Discord account has been linked to **{player}**!"
  color: 5814783
  thumbnail: "https://minotar.net/avatar/{player}/128"
  footer: "EligiusMC"
  timestamp: true
```

| Placeholder | Se resuelve a |
|-------------|---------------|
| `{player}` | Nombre de usuario de Minecraft recién vinculado |
