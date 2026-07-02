---
title: Configuração do Retransmissor de Console (console.yml)
description: Transmita logs do servidor para o Discord e execute comandos de um canal de console.
---

# console.yml

Ponte de console bidirecional: logs do servidor são transmitidos para um canal Discord, e mensagens digitadas nesse canal são executadas como comandos do servidor (se não estiverem na blacklist).

## channel

```yaml
channel: "1521272226442383440"
```

ID do canal Discord. Todos os logs do servidor são transmitidos aqui. Mensagens enviadas por usuários neste canal são executadas como comandos do servidor Minecraft.

## poll_interval

```yaml
poll_interval: 2
```

Frequência (em segundos) com que o plugin lê novas linhas de `logs/latest.log`. Valores mais baixos fornecem saída mais próxima do tempo real ao custo de uso de CPU ligeiramente maior.

## command_blacklist

```yaml
command_blacklist:
  - "stop"
  - "restart"
  - "reload"
  - "ban"
  - "ban-ip"
  - "pardon"
  - "pardon-ip"
  - "op"
  - "deop"
  - "say"
  - "gamemode"
  - "whitelist"
  - "kick"
  - "plugins"
```

Comandos nesta lista não podem ser executados do Discord. A verificação é baseada em prefixo — por exemplo, bloquear `ban` também bloqueia `ban-ip` e `banlist`, mas a entrada explícita `ban-ip` garante clareza. Comandos administrativos perigosos (stop, restart, op, deop) estão na blacklist por padrão.

## Formatação

A saída de log no Discord usa blocos de código ` ```ansi ` para preservar os códigos de cores ANSI do console do servidor, dando uma aparência nativa de console.
