---
title: Configuração de Verificação (verify.yml)
description: Sistema de vinculação de contas — painel de verificação, códigos, sons, comandos pós-verificação.
---

# verify.yml

Gerencia o fluxo de verificação de conta Minecraft ↔ Discord. Jogadores digitam `/verify` no jogo, recebem um código alfanumérico de 6 caracteres e o confirmam via botão no Discord.

## channel

```yaml
channel: "1521272231068700702"
```

Canal Discord onde o painel de verificação é publicado na inicialização.

## panel

Embed de painel estático com um botão **Verify** persistente.

```yaml
panel:
  embed:
    title: "🔐 Verificação de Conta"
    description: "Vincule sua conta Minecraft para desbloquear todos os recursos."
    color: "0x5865F2"
    author: "EligiusMC"
    footer: "EligiusMC"
    timestamp: true
    fields:
      - name: "📋 Como verificar"
        value: |
          1. Entre no servidor Minecraft
          2. Digite `/verify` no chat
          3. Clique em **Verify** abaixo
          4. Insira o código do Discord no jogo
        inline: false
  buttons:
    verify: true
```

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `panel.embed.*` | — | Campos de embed padrão (veja player-events.md) |
| `panel.buttons.verify` | Booleano | Mostrar o botão Verify |

## code

```yaml
code:
  expiry: 5
```

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `expiry` | Inteiro | Minutos antes de um código gerado expirar |

Os códigos sempre têm 6 caracteres alfanuméricos (A-Z, 0-9).

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

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `enabled` | Booleano | Interruptor mestre de som |
| `success.sound` | String | Chave de som Bukkit no sucesso da verificação |
| `success.volume` | Float | Volume do som (0.0–1.0) |
| `success.pitch` | Float | Tom do som |
| `error.sound` | String | Chave de som Bukkit em código inválido/expirado |
| `error.volume` | Float | Volume do som de erro |
| `error.pitch` | Float | Tom do som de erro |

## post_verify

Comandos executados no servidor Minecraft após verificação bem-sucedida.

```yaml
post_verify:
  - "give %player_name% minecraft:diamond 1"
  - "say %player_name% verificou sua conta!"
  - "lp user %player_name% parent add verified"
```

| Placeholder | Resolve para |
|-------------|-------------|
| `%player_name%` | Nome de usuário Minecraft |
| `{player}` | Nome de usuário Minecraft |
| `{uuid}` | UUID do jogador |

Tanto `%player_name%` (estilo Bukkit) quanto `{player}` (estilo chaves) são suportados.

## code_embed

Embed efêmero mostrado ao usuário Discord ao clicar no botão **Verify**.

```yaml
code_embed:
  title: "🔐 Código de Verificação"
  description: "Use o código abaixo para vincular sua conta Minecraft."
  color: 5814783
  thumbnail: "https://minotar.net/avatar/server/128"
  author: "EligiusMC"
  author_icon: "https://minotar.net/avatar/EligiusMC/128"
  footer: "EligiusMC"
  timestamp: true
  fields:
    - name: "🔑 Seu Código"
      value: "`{code}`"
      inline: false
```

`{code}` é substituído pelo código de 6 caracteres gerado.

## dm_embed

DM enviada ao usuário Discord após verificação bem-sucedida.

```yaml
dm_embed:
  title: "✅ Conta Vinculada"
  description: "Sua conta Discord foi vinculada a **{player}**!"
  color: 5814783
  thumbnail: "https://minotar.net/avatar/{player}/128"
  footer: "EligiusMC"
  timestamp: true
```

| Placeholder | Resolve para |
|-------------|-------------|
| `{player}` | Nome de usuário Minecraft recém-vinculado |
