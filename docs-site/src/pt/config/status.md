---
title: Configuração de Status do Servidor (status.yml)
description: Embeds de status online/offline publicados na inicialização e parada do servidor.
---

# status.yml

Publica um embed em um canal Discord quando o servidor inicia e para. Na inicialização, o embed online anterior é excluído e um novo é publicado. No desligamento, todas as mensagens do bot são limpas e um embed offline é publicado de forma síncrona.

## ⚠️ Canal compartilhado com counters.yml

O `channel` neste arquivo **deve corresponder** ao canal do contador `server_status` em `counters.yml`. O contador renomeia o canal (ex. `🟢 Online` / `🔴 Offline`), e este arquivo publica o embed de status. Não modifique um sem verificar o outro.

## channel

```yaml
channel: "1521276573033173204"
```

ID do canal Discord usado tanto para o embed de status quanto para o contador `server_status`.

## online

```yaml
online:
  title: "🟢 Servidor Online"
  description: "O servidor Minecraft está **online** e pronto para jogar!"
  color: 5763719
  thumbnail: "https://minotar.net/avatar/server/128"
  author: "Status EligiusMC"
  author_icon: "https://minotar.net/avatar/EligiusMC/128"
  footer: "Servidor iniciado"
  timestamp: true
  fields:
    - name: "📋 Servidor"
      value: "EligiusMC"
      inline: true
    - name: "🎮 Modo de Jogo"
      value: "Survival"
      inline: true
    - name: "🌍 Mundo"
      value: "Overworld"
      inline: true
    - name: "📢 Info"
      value: "Junte-se a nós e comece sua aventura!"
      inline: false
```

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `title` | String | Título do embed |
| `description` | String | Descrição do embed (Markdown) |
| `color` | Inteiro | Cor do embed (decimal) |
| `thumbnail` | String | URL da miniatura |
| `author` | String | Nome do autor |
| `author_icon` | String | URL do ícone do autor |
| `footer` | String | Texto do rodapé |
| `timestamp` | Booleano | Mostrar timestamp |
| `fields` | Lista | Campos inline do embed |

## offline

```yaml
offline:
  title: "🔴 Servidor Offline"
  description: "O servidor Minecraft está atualmente **offline** para manutenção."
  color: 15548997
  thumbnail: "https://minotar.net/avatar/server/128"
  author: "Status EligiusMC"
  author_icon: "https://minotar.net/avatar/EligiusMC/128"
  footer: "Voltaremos em breve"
  timestamp: true
  fields:
    - name: "📋 Status"
      value: "Em manutenção"
      inline: true
    - name: "⏰ ETA"
      value: "Em breve"
      inline: true
```

Mesma estrutura do `online`. O embed offline é enviado de forma síncrona durante `onDisable` para garantir a entrega antes do desligamento do cliente JDA.
