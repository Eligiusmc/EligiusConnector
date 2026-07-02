---
title: Configuração do Bot e Banco de Dados (config.yml)
description: Configuração principal do plugin — token do bot, banco de dados, idioma, debug.
---

# config.yml

O arquivo de configuração principal. Controla a conexão do bot, tipo de banco de dados, idioma e modo debug.

## bot

```yaml
bot:
  token: "${DISCORD_BOT_TOKEN}"
  guild_id: "508152650978820096"
  status_rotation_minutes: 15
  game_status:
    - "🎮 EligiusMC"
    - "🟢 {online} jogadores"
```

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `token` | String | Token do bot Discord. Use `${ENV_VAR}` para variáveis de ambiente |
| `guild_id` | String | ID do servidor Discord (guild) |
| `status_rotation_minutes` | Inteiro | Frequência com que o bot alterna entre as entradas `game_status` |
| `game_status` | Lista | Mensagens de status exibidas no perfil do bot. `{online}` é resolvido para a contagem atual de jogadores |

## database

```yaml
database:
  type: "sqlite"
  mysql:
    host: "localhost"
    port: 3306
    name: "eligiusconnector"
    username: "root"
    password: ""
    pool_size: 10
```

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `type` | String | `sqlite` ou `mysql` |
| `mysql.*` | — | Configurações de conexão MySQL (usadas apenas quando `type: mysql`) |
| `mysql.pool_size` | Inteiro | Tamanho do pool de conexões HikariCP |

## language

```yaml
language: "pt"
```

Idioma padrão para jogadores que não definiram preferência. Disponíveis: `en`, `es`, `fr`, `de`, `pt`, `ru`.

## debug

```yaml
debug: false
```

Quando ativado, exibe informações de diagnóstico adicionais no console.
