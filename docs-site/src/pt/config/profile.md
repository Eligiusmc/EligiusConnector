---
title: Configuração de Perfil (profile.yml)
description: Painéis Discord para perfil, inventário, localização e saldo econômico.
---

# profile.yml

Fornece painéis interativos Discord para ver estatísticas do jogador, inventário, coordenadas e saldo econômico. Acessível via canal **Player Panel** com botões.

## channel

```yaml
channel: "1521391141436653668"
```

Canal Discord onde o embed do painel é publicado.

## panel

Painel principal com botões que acionam subcomandos.

```yaml
panel:
  embed:
    title: "👤 Painel do Jogador"
    description: "Selecione uma opção abaixo para ver suas estatísticas, inventário ou localização."
    color: "0x57F287"
    footer: "EligiusMC"
    timestamp: true
    fields:
      - name: "👤 Perfil"
        value: "Veja seu perfil completo — estatísticas, vida, localização e mais."
        inline: true
      # ... inventory, whereami, money
  buttons:
    profile: true
    inventory: true
    whereami: true
    money: true
```

| Botão | Ação |
|-------|------|
| `profile` | Mostrar perfil completo do jogador |
| `inventory` | Mostrar inventário categorizado |
| `whereami` | Mostrar localização, bioma, bloco |
| `money` | Mostrar saldo Vault |

## profile

Embed online com estatísticas completas. Embed offline alternativo quando o jogador não está no servidor.

```yaml
profile:
  embed:
    title: "👤 Perfil de {player_name}"
    color: "0x5865F2"
    thumbnail: "https://minotar.net/avatar/{player_name}/128"
    timestamp: true
    fields:
      - name: "📋 Info"
        value: |
          **Nome:** {player_name}
          **UUID:** {player_uuid}
      - name: "☠️ Combate"
        value: |
          **Kills:** {player_kills}
          **Mortes:** {player_deaths}
          **K/D:** {player_kd}
  offline:
    title: "👤 {player_name} (Offline)"
    color: "0x808080"
    # ... mesma estrutura, menos campos
```

### Placeholders do perfil

| Placeholder | Descrição |
|-------------|-----------|
| `{player_name}` | Nome do jogador |
| `{player_uuid}` | UUID do jogador |
| `{player_linked_status}` | Status de vínculo Discord |
| `{player_kills}` | Total de kills |
| `{player_deaths}` | Total de mortes |
| `{player_kd}` | Proporção kill/morte |
| `{player_playtime}` | Tempo de jogo formatado |
| `{player_health}` / `{player_max_health}` | Vida atual / máx |
| `{player_health_bar}` | Barra de vida visual |
| `{player_food}` / `{player_max_food}` | Fome atual / máx |
| `{player_food_bar}` | Barra de fome visual |
| `{player_xp_bar}` / `{player_level}` | Barra de XP + nível |
| `{player_world}` | Nome do mundo |
| `{player_direction}` | Direção cardinal |
| `{player_x}` / `{player_y}` / `{player_z}` | Coordenadas |
| `{player_gamemode}` | Modo de jogo |
| `{player_ping}` | Latência em ms |

Quando o jogador está online, a seção `embed` é usada. Quando offline, a seção `offline` é exibida.

## inventory

Visualização de inventário categorizada.

```yaml
inventory:
  embed:
    title: "🎒 Inventário de {player_name}"
    color: "0xF39C12"
    thumbnail: "https://minotar.net/avatar/{player_name}/128"
    footer: "Slots: {inv_used_slots}/{inv_total_slots}"
    fields:
      - name: "⚔️ Armas"
        value: "{inv_weapons}"
      - name: "🛡️ Armadura"
        value: "{inv_armor}"
      # ... tools, blocks, food, potions, misc
  options:
    empty_category_hidden: true
    max_items_per_category: 10
```

| Opção | Tipo | Descrição |
|-------|------|-----------|
| `empty_category_hidden` | Booleano | Ocultar categorias sem itens |
| `max_items_per_category` | Inteiro | Máx. de itens listados por categoria |

Placeholders de inventário: `{inv_weapons}`, `{inv_armor}`, `{inv_tools}`, `{inv_blocks}`, `{inv_food}`, `{inv_potions}`, `{inv_misc}`, `{inv_used_slots}`, `{inv_total_slots}`.

## whereami

Localização, bioma e bloco atual.

```yaml
whereami:
  embed:
    title: "📍 Localização de {player_name}"
    color: "0x2ECC71"
    fields:
      - name: "📍 Localização"
        value: |
          **Mundo:** {player_world}
          **X:** {player_x} **Y:** {player_y} **Z:** {player_z}
          **Direção:** {player_direction}
          **Bioma:** {player_biome}
          **Bloco:** {player_block}
```

| Placeholder | Descrição |
|-------------|-----------|
| `{player_biome}` | Nome do bioma atual |
| `{player_block}` | Bloco em que o jogador está pisando |

## money

Mostra o saldo econômico Vault.

```yaml
money:
  embed:
    title: "💰 Saldo de {player_name}"
    color: "0xF1C40F"
    fields:
      - name: "💰 Saldo"
        value: "**${balance}**"
```

Requer um plugin de economia compatível com Vault. `{balance}` é o saldo formatado do provedor de economia.
