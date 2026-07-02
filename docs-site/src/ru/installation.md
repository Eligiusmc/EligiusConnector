---
title: Установка и требования
description: Узнайте, как установить EligiusConnector на ваш Minecraft-сервер.
---

# Установка и требования

EligiusConnector — это комплексный Discord-Minecraft мост. Он подключается к Discord через JDA и опционально интегрируется с Vault, LuckPerms и PlaceholderAPI.

## 📋 Системные требования

| Требование | Минимальная версия | Причина |
|------------|-------------------|---------|
| **Платформа** | Bukkit, Spigot, Paper, Purpur или Folia 1.21 - 26.1.2+ | Использует современный Paper API и команды Brigadier |
| **Java** | Java 21 LTS | Исходный код скомпилирован в формате JDK 21 |

## 📦 Зависимости

| Плагин | Обязателен | Назначение |
|--------|-----------|------------|
| **PlaceholderAPI** | Рекомендуется | Включает `%placeholders%` в эмбедах и сообщениях. Без него плейсхолдеры отображаются как текст |
| **Vault** | Опционально | Баланс экономики в панели профиля (кнопка `/money`) |
| **LuckPerms** | Опционально | Синхронизация ролей — группы Minecraft ↔ роли Discord |
| **EssentialsX** | Опционально | Обеспечивает экономический бэкенд для Vault |

## 🚀 Шаги установки

1. Скачайте последнюю версию `EligiusConnector-x.x.x.jar` со [страницы релизов](https://github.com/Eligiusmc/EligiusConnector/releases)
2. Скопируйте JAR в папку `plugins/` вашего сервера
3. Установите рекомендуемые плагины: **PlaceholderAPI**, **Vault**, **LuckPerms**, **EssentialsX**
4. Запустите сервер — все файлы конфигурации будут сгенерированы в `plugins/EligiusConnector/`
5. Отредактируйте `config.yml` и укажите токен вашего Discord-бота
6. Перезапустите сервер или выполните `/connector reload`

![Консоль запуска](/assets/readme.png)

## ⚙️ Первоначальная настройка

После первого запуска вы увидите логотип кота Eligius MC в консоли со статусом модулей. Все YAML-конфигурации генерируются с разумными значениями по умолчанию. Единственное обязательное изменение — токен Discord-бота в `config.yml`:

```yaml
bot:
  token: "ВАШ_ТОКЕН_БОТА_DISCORD"
  guild_id: "ВАШ_ID_СЕРВЕРА_DISCORD"
```

## 🔌 Настройка Discord-бота

1. Перейдите на [Портал разработчика Discord](https://discord.com/developers/applications)
2. Создайте новое приложение → Добавьте бота
3. Включите **Message Content Intent**, **Server Members Intent** и **Guild Messages Intent**
4. Скопируйте токен бота и вставьте его в `config.yml`
5. Пригласите бота на сервер с правами `bot` и `applications.commands`
