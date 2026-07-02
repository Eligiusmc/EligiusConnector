---
title: Configuração de Eventos de Jogador (player_events.yml)
description: Configure embeds Discord para entradas, saídas, mortes e conquistas.
---

# player_events.yml

Envia embeds Discord para eventos de jogadores Minecraft. Cada evento é independentemente ativável — defina `enabled: false` em qualquer evento para desativá-lo. Excluir este arquivo desativa todos os eventos.

## Placeholders

| Placeholder | Eventos | Resolve para |
|-------------|---------|-------------|
| `{player}` | Todos | Nome do jogador |
| `{online}` | join, leave | Contagem atual de jogadores online |
| `{max}` | join, leave | Slots máximos do servidor |
| `{first_join}` | join | `"Yes"` ou `"No"` |
| `{death_message}` | death | Mensagem de morte bruta |
| `{health}` | death | Vida do jogador |
| `{food}` | death | Nível de fome do jogador |
| `{world}` | death | Nome do mundo |
| `{advancement}` | advancement | Título da conquista |

## join

```yaml
join:
  enabled: true
  channel: "1521276575889358960"
  embed:
    color: 5763719
    author: "{player} entrou no servidor"
    author_icon: "https://minotar.net/avatar/{player}/128"
    thumbnail: "https://minotar.net/avatar/{player}/128"
    title: "Bem-vindo ao EligiusMC!"
    description: "Todos digam olá para **{player}**!"
    footer: "EligiusMC"
    timestamp: true
    fields:
      - name: "Online"
        value: "**{online}** / **{max}**"
        inline: true
      - name: "Primeira Vez"
        value: "{first_join}"
        inline: true
```

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `enabled` | Booleano | Ativa embeds de entrada |
| `channel` | String | ID do canal Discord |
| `embed.color` | Inteiro | Cor do embed (decimal) |
| `embed.author` | String | Linha do autor (suporta placeholders) |
| `embed.author_icon` | String | URL do ícone do autor |
| `embed.thumbnail` | String | URL da miniatura |
| `embed.title` | String | Título do embed |
| `embed.description` | String | Descrição do embed (Markdown) |
| `embed.footer` | String | Texto do rodapé |
| `embed.timestamp` | Booleano | Mostrar timestamp |
| `embed.fields` | Lista | Campos adicionais (`name`, `value`, `inline`) |

## leave

Mesma estrutura do join. Por padrão, um embed de despedida com cabeça do jogador e contagem online.

| Campo | Cor padrão |
|-------|-----------|
| `color` | `15548997` (vermelho) |
| `author` | `"{player} saiu do servidor"` |

## death

```yaml
death:
  enabled: true
  channel: "1521276578708066578"
  embed:
    color: 15548997
    author: "{player} morreu"
    title: "{death_message}"
    fields:
      - name: "Vida"
        value: "❤️ {health} PV"
        inline: true
      - name: "Fome"
        value: "🍖 {food} / 20"
        inline: true
      - name: "Mundo"
        value: "🌍 {world}"
        inline: true
```

`{death_message}` exibe a mensagem de morte bruta do Minecraft. Campos extras mostram vida, fome e mundo.

## advancement

```yaml
advancement:
  enabled: true
  channel: "1521276581522444350"
  embed:
    color: 16766720
    author: "Conquista Alcançada!"
    title: "{advancement}"
    description: "**{player}** conquistou este avanço!"
```

`{advancement}` é resolvido para o título da conquista.
