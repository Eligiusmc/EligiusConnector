---
title: Configuração do Chat Bridge (chat.yml)
description: Configure a ponte de chat Discord ↔ Minecraft, webhooks e filtros.
---

# chat.yml

Controla a ponte de chat bidirecional, aparência dos webhooks e filtros anti-abuso.

## channel

```yaml
channel: "1521272219223851028"
```

O canal Discord onde as mensagens MC ↔ DC aparecem e para onde o `/chat` envia mensagens.

## bridge

```yaml
bridge:
  mc_to_discord: true
  discord_to_mc: true
```

| Campo | Descrição |
|-------|-----------|
| `mc_to_discord` | Envia o chat do Minecraft para o Discord |
| `discord_to_mc` | Envia mensagens do Discord para o chat do Minecraft |

## format

```yaml
format:
  mc_to_discord: "{player}: {message}"
  discord_to_mc: "[DC] {player}: {message}"
```

| Placeholder | Resolve para |
|-------------|-------------|
| `{player}` | Nome do jogador |
| `{message}` | Conteúdo da mensagem de chat |

## webhook

```yaml
webhook:
  enabled: true
  name: "Minecraft"
  avatar: "https://minotar.net/avatar/%player_name%/128"
```

Usa webhooks do Discord para mensagens MC → DC, exibindo cabeças de jogadores e apelidos.

## filter

```yaml
filter:
  enabled: true
```

Interruptor mestre para desativar todos os filtros de uma vez.

### filter.spam

| Campo | Padrão | Descrição |
|-------|--------|-----------|
| `enabled` | `true` | Ativa o anti-spam |
| `max_messages` | `5` | Mensagens permitidas na janela de tempo |
| `time_window_seconds` | `10` | Janela de tempo em segundos |

### filter.profanity

| Campo | Descrição |
|-------|-----------|
| `enabled` | Ativa o filtro de palavrões |
| `words` | Lista de palavras bloqueadas (não sensível a maiúsculas) |

### filter.links

| Campo | Descrição |
|-------|-----------|
| `enabled` | Ativa o filtro de links |
| `mode` | `whitelist` (apenas domínios listados permitidos) ou `blacklist` (domínios listados bloqueados) |
| `domains` | Lista de domínios para o modo selecionado |

### filter.caps

| Campo | Padrão | Descrição |
|-------|--------|-----------|
| `enabled` | `true` | Ativa o filtro de maiúsculas |
| `max_percentage` | `70` | Porcentagem máxima de caracteres maiúsculos |
| `min_length` | `10` | Comprimento mínimo da mensagem para verificar |

### filter.punishment

```yaml
punishment:
  enabled: true
  thresholds:
    2: "mute:300"
    5: "tempban:3600"
    10: "banip"
```

Punições progressivas para reincidentes. Ações: `warn`, `delete`, `mute:<segundos>`, `tempban:<segundos>`, `banip`.
