---
title: Configuração de Contadores Dinâmicos (counters.yml)
description: Contadores de nomes de canais Discord periódicos e baseados em estado.
---

# counters.yml

Renomeia canais de voz/texto Discord dinamicamente. Dois tipos: **periodic** (baseado em intervalo) e **state** (acionado por eventos do servidor).

## Placeholders integrados

| Placeholder | Descrição |
|-------------|-----------|
| `%server_online%` | Jogadores online atuais |
| `%server_max_players%` | Slots máximos do servidor |
| `%server_unique_joins%` | Total de jogadores únicos que já entraram |
| `%server_ram_used%` | RAM usada (MB) |
| `%server_ram_free%` | RAM livre (MB) |
| `%server_ram_total%` | RAM total (MB) |
| `%server_uptime%` | Tempo de atividade do servidor |
| `%server_members%` | Contagem de membros do servidor Discord |

## periodic

Atualiza o nome de um canal a cada N segundos usando uma string de formato com placeholders.

```yaml
counters:
  online_players:
    type: periodic
    enabled: true
    channel: "1521280346585305268"
    format: "🟢 Online: %server_online%"
    interval: 30
```

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `type` | String | Deve ser `periodic` |
| `enabled` | Booleano | Ativar este contador |
| `channel` | String | ID do canal Discord para renomear |
| `format` | String | Nome do canal com placeholders |
| `interval` | Inteiro | Intervalo de atualização em segundos |

### Debounce e cooldown

Para evitar limites de taxa do Discord, as renomeações de canal têm um mecanismo de debounce: se o nome resolvido não mudou, a renomeação é ignorada. Além disso, um cooldown de 120 segundos é aplicado entre renomeações por canal. Contadores de alta frequência (ex. `interval: 5`) só renomearão quando o valor realmente mudar.

## state

Reage a mudanças de estado do servidor — renomeia o canal imediatamente quando o estado muda.

```yaml
counters:
  server_status:
    type: state
    enabled: true
    channel: "1521276573033173204"
    state_key: server_online
    states:
      online: "🟢 Online"
      offline: "🔴 Offline"
```

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `type` | String | Deve ser `state` |
| `enabled` | Booleano | Ativar este contador |
| `channel` | String | ID do canal Discord para renomear |
| `state_key` | String | Estado observável a monitorar (`server_online`) |
| `states.online` | String | Nome do canal quando o estado é `true` |
| `states.offline` | String | Nome do canal quando o estado é `false` |

## ⚠️ Compartilhamento do canal server_status

O contador `server_status` compartilha seu `channel` com `status.yml`. O contador renomeia o canal, e `status.yml` publica o embed online/offline. Mantenha-os sempre sincronizados — alterar o canal em um arquivo requer alterá-lo no outro.
