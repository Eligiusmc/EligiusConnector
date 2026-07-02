---
title: Конфигурация верификации (verify.yml)
description: Система привязки аккаунтов — панель верификации, коды, звуки, пост-верификационные команды.
---

# verify.yml

Управляет процессом верификации аккаунта Minecraft ↔ Discord. Игроки вводят `/verify` в игре, получают 6-значный буквенно-цифровой код и подтверждают его через кнопку Discord.

## channel

```yaml
channel: "1521272231068700702"
```

Канал Discord, где панель верификации публикуется при запуске.

## panel

Статичный эмбед панели с постоянной кнопкой **Verify**.

```yaml
panel:
  embed:
    title: "🔐 Верификация аккаунта"
    description: "Привяжите ваш аккаунт Minecraft, чтобы разблокировать все функции."
    color: "0x5865F2"
    author: "EligiusMC"
    footer: "EligiusMC"
    timestamp: true
    fields:
      - name: "📋 Как пройти верификацию"
        value: |
          1. Зайдите на сервер Minecraft
          2. Введите `/verify` в чате
          3. Нажмите **Verify** ниже
          4. Введите код из Discord в игре
        inline: false
  buttons:
    verify: true
```

| Поле | Тип | Описание |
|------|-----|----------|
| `panel.embed.*` | — | Стандартные поля эмбеда (см. player-events.md) |
| `panel.buttons.verify` | Булево | Показать кнопку Verify |

## code

```yaml
code:
  expiry: 5
```

| Поле | Тип | Описание |
|------|-----|----------|
| `expiry` | Целое | Минуты до истечения сгенерированного кода |

Коды всегда состоят из 6 буквенно-цифровых символов (A-Z, 0-9).

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

| Поле | Тип | Описание |
|------|-----|----------|
| `enabled` | Булево | Главный выключатель звуков |
| `success.sound` | Строка | Ключ звука Bukkit при успешной верификации |
| `success.volume` | Float | Громкость звука (0.0–1.0) |
| `success.pitch` | Float | Высота звука |
| `error.sound` | Строка | Ключ звука Bukkit при ошибке/истечении кода |
| `error.volume` | Float | Громкость звука ошибки |
| `error.pitch` | Float | Высота звука ошибки |

## post_verify

Команды, выполняемые на сервере Minecraft после успешной верификации.

```yaml
post_verify:
  - "give %player_name% minecraft:diamond 1"
  - "say %player_name% верифицировал свой аккаунт!"
  - "lp user %player_name% parent add verified"
```

| Плейсхолдер | Заменяется на |
|-------------|---------------|
| `%player_name%` | Имя пользователя Minecraft |
| `{player}` | Имя пользователя Minecraft |
| `{uuid}` | UUID игрока |

Поддерживаются оба стиля: `%player_name%` (Bukkit) и `{player}` (фигурные скобки).

## code_embed

Эфемерный эмбед, показываемый пользователю Discord при нажатии кнопки **Verify**.

```yaml
code_embed:
  title: "🔐 Код верификации"
  description: "Используйте код ниже для привязки вашего аккаунта Minecraft."
  color: 5814783
  thumbnail: "https://minotar.net/avatar/server/128"
  author: "EligiusMC"
  author_icon: "https://minotar.net/avatar/EligiusMC/128"
  footer: "EligiusMC"
  timestamp: true
  fields:
    - name: "🔑 Ваш код"
      value: "`{code}`"
      inline: false
```

`{code}` заменяется на сгенерированный 6-значный код.

## dm_embed

ЛС, отправляемое пользователю Discord после успешной верификации.

```yaml
dm_embed:
  title: "✅ Аккаунт привязан"
  description: "Ваш Discord-аккаунт был привязан к **{player}**!"
  color: 5814783
  thumbnail: "https://minotar.net/avatar/{player}/128"
  footer: "EligiusMC"
  timestamp: true
```

| Плейсхолдер | Заменяется на |
|-------------|---------------|
| `{player}` | Только что привязанное имя пользователя Minecraft |
