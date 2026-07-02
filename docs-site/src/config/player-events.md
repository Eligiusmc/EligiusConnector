---
title: Player Events Configuration (player_events.yml)
description: Configure join, leave, death, and advancement Discord embeds.
---

# player_events.yml

Sends Discord embeds for Minecraft player events. Each event is independently toggleable — set `enabled: false` on any event to disable just that one. Deleting this file disables all events.

## Placeholders

| Placeholder | Events | Resolves to |
|-------------|--------|-------------|
| `{player}` | All | Player name |
| `{online}` | join, leave | Current online player count |
| `{max}` | join, leave | Server max slots |
| `{first_join}` | join | `"Yes"` or `"No"` |
| `{death_message}` | death | Raw death message |
| `{health}` | death | Player health |
| `{food}` | death | Player food level |
| `{world}` | death | World name |
| `{advancement}` | advancement | Advancement title |

## join

```yaml
join:
  enabled: true
  channel: "1521276575889358960"
  embed:
    color: 5763719
    author: "{player} joined the server"
    author_icon: "https://minotar.net/avatar/{player}/128"
    thumbnail: "https://minotar.net/avatar/{player}/128"
    title: "Welcome to EligiusMC!"
    description: "Everyone say hello to **{player}**!"
    footer: "EligiusMC"
    timestamp: true
    fields:
      - name: "Online"
        value: "**{online}** / **{max}**"
        inline: true
      - name: "First Time"
        value: "{first_join}"
        inline: true
```

| Field | Type | Description |
|-------|------|-------------|
| `enabled` | Boolean | Enable join embeds |
| `channel` | String | Discord channel ID |
| `embed.color` | Integer | Embed color (decimal) |
| `embed.author` | String | Author line (supports placeholders) |
| `embed.author_icon` | String | URL for author icon |
| `embed.thumbnail` | String | URL for thumbnail image |
| `embed.title` | String | Embed title |
| `embed.description` | String | Embed description (Markdown) |
| `embed.footer` | String | Footer text |
| `embed.timestamp` | Boolean | Show timestamp |
| `embed.fields` | List | Additional fields (`name`, `value`, `inline`) |

## leave

Same structure as join. Defaults to a farewell embed with player head and online count.

| Field | Default color |
|-------|---------------|
| `color` | `15548997` (red) |
| `author` | `"{player} left the server"` |

## death

```yaml
death:
  enabled: true
  channel: "1521276578708066578"
  embed:
    color: 15548997
    author: "{player} died"
    title: "{death_message}"
    fields:
      - name: "Health"
        value: "❤️ {health} HP"
        inline: true
      - name: "Hunger"
        value: "🍖 {food} / 20"
        inline: true
      - name: "World"
        value: "🌍 {world}"
        inline: true
```

`{death_message}` displays the raw death message from Minecraft. Extra death-only fields show health, hunger, and world.

## advancement

```yaml
advancement:
  enabled: true
  channel: "1521276581522444350"
  embed:
    color: 16766720
    author: "Advancement Made!"
    title: "{advancement}"
    description: "**{player}** has earned this advancement!"
```

`{advancement}` resolves to the advancement title.
