---
title: Verification Configuration (verify.yml)
description: Account linking system — verify panel, codes, sounds, post-verify commands.
---

# verify.yml

Manages the Minecraft ↔ Discord account verification flow. Players type `/verify` in-game, receive a 6-character alphanumeric code, and confirm it via Discord button.

## channel

```yaml
channel: "1521272231068700702"
```

Discord channel where the verify panel is posted on startup.

## panel

Static panel embed with a persistent **Verify** button.

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

| Field | Type | Description |
|-------|------|-------------|
| `panel.embed.*` | — | Standard embed fields (see player-events.md) |
| `panel.buttons.verify` | Boolean | Show the Verify button |

## code

```yaml
code:
  expiry: 5
```

| Field | Type | Description |
|-------|------|-------------|
| `expiry` | Integer | Minutes before a generated code expires |

Codes are always 6 alphanumeric characters (A-Z, 0-9).

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

| Field | Type | Description |
|-------|------|-------------|
| `enabled` | Boolean | Master sound toggle |
| `success.sound` | String | Bukkit sound key on successful verify |
| `success.volume` | Float | Sound volume (0.0–1.0) |
| `success.pitch` | Float | Sound pitch |
| `error.sound` | String | Bukkit sound key on failed/expired code |
| `error.volume` | Float | Error sound volume |
| `error.pitch` | Float | Error sound pitch |

## post_verify

Commands executed on the Minecraft server after successful verification.

```yaml
post_verify:
  - "give %player_name% minecraft:diamond 1"
  - "say %player_name% has verified their account!"
  - "lp user %player_name% parent add verified"
```

| Placeholder | Resolves to |
|-------------|-------------|
| `%player_name%` | Minecraft username |
| `{player}` | Minecraft username |
| `{uuid}` | Player UUID |

Both `%player_name%` (Bukkit-style) and `{player}` (brace-style) are supported.

## code_embed

Ephemeral embed shown to the Discord user when they click the **Verify** button.

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

`{code}` is replaced with the generated 6-character code.

## dm_embed

DM sent to the Discord user after successful verification.

```yaml
dm_embed:
  title: "✅ Account Linked"
  description: "Your Discord account has been linked to **{player}**!"
  color: 5814783
  thumbnail: "https://minotar.net/avatar/{player}/128"
  footer: "EligiusMC"
  timestamp: true
```

| Placeholder | Resolves to |
|-------------|-------------|
| `{player}` | Minecraft username just linked |
