---
title: Configuración de Eventos de Jugadores (player_events.yml)
description: Configura los embeds de Discord para entrada, salida, muerte y logros.
---

# player_events.yml

Envía embeds de Discord para eventos de jugadores de Minecraft. Cada evento se puede activar o desactivar individualmente — configura `enabled: false` en cualquier evento para desactivar solo ese. Eliminar este archivo desactiva todos los eventos.

## Placeholders

| Placeholder | Eventos | Se resuelve a |
|-------------|---------|---------------|
| `{player}` | Todos | Nombre del jugador |
| `{online}` | join, leave | Número actual de jugadores online |
| `{max}` | join, leave | Slots máximos del servidor |
| `{first_join}` | join | `"Yes"` o `"No"` |
| `{death_message}` | death | Mensaje de muerte sin formato |
| `{health}` | death | Vida del jugador |
| `{food}` | death | Nivel de comida del jugador |
| `{world}` | death | Nombre del mundo |
| `{advancement}` | advancement | Título del logro |

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

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `enabled` | Boolean | Activar embeds de entrada |
| `channel` | String | ID del canal de Discord |
| `embed.color` | Integer | Color del embed (decimal) |
| `embed.author` | String | Línea de autor (soporta placeholders) |
| `embed.author_icon` | String | URL para el icono del autor |
| `embed.thumbnail` | String | URL para la imagen miniatura |
| `embed.title` | String | Título del embed |
| `embed.description` | String | Descripción del embed (Markdown) |
| `embed.footer` | String | Texto del footer |
| `embed.timestamp` | Boolean | Mostrar timestamp |
| `embed.fields` | List | Campos adicionales (`name`, `value`, `inline`) |

## leave

Misma estructura que join. Por defecto, un embed de despedida con la cabeza del jugador y el conteo de online.

| Campo | Color por defecto |
|-------|------------------|
| `color` | `15548997` (rojo) |
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

`{death_message}` muestra el mensaje de muerte sin formato de Minecraft. Los campos extra de muerte muestran vida, hambre y mundo.

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

`{advancement}` se resuelve al título del logro.
