---
title: Configuração de Sincronização (synchronization.yml)
description: Sincronização bidirecional de grupos Minecraft ↔ cargos Discord, e apelidos.
---

# synchronization.yml

Dois sistemas de sincronização: **roles** espelha grupos LuckPerms para cargos Discord, **nickname** define apelidos Discord a partir de nomes Minecraft. Ambos são bidirecionais — cargos são atribuídos e removidos conforme os grupos mudam.

Requer Vault e LuckPerms no servidor (para detecção de grupos).

## Interruptor mestre

```yaml
enabled: true
```

Defina como `false` para desativar todos os sistemas de sincronização de uma vez.

## roles

```yaml
roles:
  enabled: true
  interval: 30
  mapping:
    verified: "1521342858945101894"
    # testing: "ROLE_ID_HERE"
    # vip: "ROLE_ID_HERE"
```

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `enabled` | Booleano | Ativar sincronização de cargos |
| `interval` | Inteiro | Frequência de verificação e sincronização (segundos) |
| `mapping` | Map | Nome do grupo LuckPerms → ID do cargo Discord |

A chave do mapeamento é o nome do grupo LuckPerms, o valor é o ID do cargo Discord. Quando um jogador vinculado tem um grupo LuckPerms mapeado, o bot atribui o cargo Discord correspondente. Quando o grupo é removido (ou o jogador desvincula), o cargo é removido. Cada jogador pode ter vários cargos de vários grupos mapeados.

## nickname

```yaml
nickname:
  enabled: true
  interval: 30
  format: "%player_name%"
```

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `enabled` | Booleano | Ativar sincronização de apelidos |
| `interval` | Inteiro | Frequência de verificação e atualização (segundos) |
| `format` | String | Formato do apelido |

O campo `format` suporta o placeholder `%player_name%`. O bot define o apelido Discord de cada membro vinculado para o formato resolvido a cada ciclo de sincronização.
