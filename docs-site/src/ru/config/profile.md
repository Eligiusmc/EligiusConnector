---
title: Конфигурация профиля (profile.yml)
description: Discord-панели для профиля, инвентаря, местоположения и экономического баланса.
---

# profile.yml

Предоставляет интерактивные Discord-панели для просмотра статистики игрока, инвентаря, координат и баланса. Доступны через канал **Player Panel** с кнопками.

## channel

```yaml
channel: "1521391141436653668"
```

Канал Discord, где публикуется эмбед панели.

## panel

Главная панель с кнопками, запускающими подкоманды.

```yaml
panel:
  embed:
    title: "👤 Панель игрока"
    description: "Выберите опцию ниже, чтобы посмотреть статистику, инвентарь или местоположение."
    color: "0x57F287"
    footer: "EligiusMC"
    timestamp: true
    fields:
      - name: "👤 Профиль"
        value: "Посмотрите полный профиль — статистика, здоровье, местоположение и другое."
        inline: true
      # ... inventory, whereami, money
  buttons:
    profile: true
    inventory: true
    whereami: true
    money: true
```

| Кнопка | Действие |
|--------|----------|
| `profile` | Показать полный профиль игрока |
| `inventory` | Показать категоризированный инвентарь |
| `whereami` | Показать местоположение, биом, блок |
| `money` | Показать баланс Vault |

## profile

Онлайн-эмбед с полной статистикой. Офлайн-эмбед, когда игрок не на сервере.

```yaml
profile:
  embed:
    title: "👤 Профиль {player_name}"
    color: "0x5865F2"
    thumbnail: "https://minotar.net/avatar/{player_name}/128"
    timestamp: true
    fields:
      - name: "📋 Инфо"
        value: |
          **Имя:** {player_name}
          **UUID:** {player_uuid}
      - name: "☠️ Бой"
        value: |
          **Убийств:** {player_kills}
          **Смертей:** {player_deaths}
          **K/D:** {player_kd}
  offline:
    title: "👤 {player_name} (Не в сети)"
    color: "0x808080"
    # ... та же структура, меньше полей
```

### Плейсхолдеры профиля

| Плейсхолдер | Описание |
|-------------|----------|
| `{player_name}` | Имя игрока |
| `{player_uuid}` | UUID игрока |
| `{player_linked_status}` | Статус привязки Discord |
| `{player_kills}` | Всего убийств |
| `{player_deaths}` | Всего смертей |
| `{player_kd}` | Соотношение убийств/смертей |
| `{player_playtime}` | Отформатированное время игры |
| `{player_health}` / `{player_max_health}` | Текущее / макс. HP |
| `{player_health_bar}` | Визуальная полоса здоровья |
| `{player_food}` / `{player_max_food}` | Текущая / макс. сытость |
| `{player_food_bar}` | Визуальная полоса сытости |
| `{player_xp_bar}` / `{player_level}` | Полоса опыта + уровень |
| `{player_world}` | Название мира |
| `{player_direction}` | Сторона света |
| `{player_x}` / `{player_y}` / `{player_z}` | Координаты |
| `{player_gamemode}` | Режим игры |
| `{player_ping}` | Задержка в мс |

Когда игрок онлайн, используется секция `embed`. Когда не в сети — секция `offline`.

## inventory

Категоризированный вид инвентаря.

```yaml
inventory:
  embed:
    title: "🎒 Инвентарь {player_name}"
    color: "0xF39C12"
    thumbnail: "https://minotar.net/avatar/{player_name}/128"
    footer: "Слоты: {inv_used_slots}/{inv_total_slots}"
    fields:
      - name: "⚔️ Оружие"
        value: "{inv_weapons}"
      - name: "🛡️ Броня"
        value: "{inv_armor}"
      # ... tools, blocks, food, potions, misc
  options:
    empty_category_hidden: true
    max_items_per_category: 10
```

| Опция | Тип | Описание |
|-------|-----|----------|
| `empty_category_hidden` | Булево | Скрывать пустые категории |
| `max_items_per_category` | Целое | Макс. предметов в категории |

Плейсхолдеры инвентаря: `{inv_weapons}`, `{inv_armor}`, `{inv_tools}`, `{inv_blocks}`, `{inv_food}`, `{inv_potions}`, `{inv_misc}`, `{inv_used_slots}`, `{inv_total_slots}`.

## whereami

Местоположение, биом и текущий блок.

```yaml
whereami:
  embed:
    title: "📍 Местоположение {player_name}"
    color: "0x2ECC71"
    fields:
      - name: "📍 Местоположение"
        value: |
          **Мир:** {player_world}
          **X:** {player_x} **Y:** {player_y} **Z:** {player_z}
          **Направление:** {player_direction}
          **Биом:** {player_biome}
          **Блок:** {player_block}
```

| Плейсхолдер | Описание |
|-------------|----------|
| `{player_biome}` | Название текущего биома |
| `{player_block}` | Блок, на котором стоит игрок |

## money

Показывает баланс экономики Vault.

```yaml
money:
  embed:
    title: "💰 Баланс {player_name}"
    color: "0xF1C40F"
    fields:
      - name: "💰 Баланс"
        value: "**${balance}**"
```

Требуется экономический плагин, совместимый с Vault. `{balance}` — отформатированный баланс от провайдера экономики.
