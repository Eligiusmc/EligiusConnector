---
title: Команды и права
description: Полный список команд и прав для EligiusConnector.
---

# Команды и права

## Команды игрока

| Команда | Описание | Право |
|---------|----------|-------|
| `/verify <код>` | Привязать ваш аккаунт Minecraft к Discord | `connector.verify` |
| `/unlink [игрок]` | Отвязать ваш аккаунт или аккаунт другого игрока | `connector.unlink.self` / `.other` |
| `/chat <сообщение>` | Отправить сообщение в чат-канал Discord | `connector.chat` |

## Команды администратора

| Команда | Описание | Право |
|---------|----------|-------|
| `/connector reload` | Перезагрузить все файлы конфигурации | `connector.reload` |
| `/connector status` | Показать статус плагина (Discord, БД, привязанные аккаунты) | `connector.admin` |
| `/events list` | Список доступных пользовательских событий | `connector.events` |
| `/events start <id>` | Запустить пользовательское событие | `connector.events` |
| `/events stop <id>` | Остановить пользовательское событие | `connector.events` |

## Права

| Право | По умолчанию | Описание |
|-------|-------------|----------|
| `connector.verify` | `true` | Разрешает привязку аккаунта через /verify |
| `connector.unlink.self` | `true` | Разрешает отвязку собственного аккаунта |
| `connector.unlink.other` | `op` | Разрешает отвязку других игроков |
| `connector.chat` | `true` | Разрешает использование команды /chat |
| `connector.chat.send` | `true` | Разрешает отправку сообщений чата |
| `connector.reload` | `op` | Разрешает /connector reload |
| `connector.admin` | `op` | Разрешает /connector status |
| `connector.events` | `op` | Управление пользовательскими событиями |
| `connector.console` | `false` | Выполнение команд из консольного канала Discord |
