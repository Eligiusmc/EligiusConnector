---
title: Comandos e Permissões
description: Lista completa de comandos e permissões do EligiusConnector.
---

# Comandos e Permissões

## Comandos de Jogador

| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/verify <código>` | Vincule sua conta Minecraft ao Discord | `connector.verify` |
| `/unlink [jogador]` | Desvincule sua conta ou a de outro jogador | `connector.unlink.self` / `.other` |
| `/chat <mensagem>` | Envie uma mensagem ao canal de chat do Discord | `connector.chat` |

## Comandos de Admin

| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/connector reload` | Recarrega todos os arquivos de configuração | `connector.reload` |
| `/connector status` | Exibe status do plugin (Discord, BD, contas vinculadas) | `connector.admin` |
| `/events list` | Lista eventos personalizados disponíveis | `connector.events` |
| `/events start <id>` | Inicia um evento personalizado | `connector.events` |
| `/events stop <id>` | Para um evento personalizado | `connector.events` |

## Permissões

| Permissão | Padrão | Descrição |
|-----------|--------|-----------|
| `connector.verify` | `true` | Permite vincular conta via /verify |
| `connector.unlink.self` | `true` | Permite desvincular a própria conta |
| `connector.unlink.other` | `op` | Permite desvincular outros jogadores |
| `connector.chat` | `true` | Permite usar o comando /chat |
| `connector.chat.send` | `true` | Permite enviar mensagens de chat |
| `connector.reload` | `op` | Permite /connector reload |
| `connector.admin` | `op` | Permite /connector status |
| `connector.events` | `op` | Gerenciar eventos personalizados |
| `connector.console` | `false` | Executar comandos do canal de console do Discord |
