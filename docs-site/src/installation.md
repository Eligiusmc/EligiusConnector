---
title: Installation & Requirements
description: Learn how to install EligiusConnector on your Minecraft server.
---

# Installation & Requirements

EligiusConnector is a comprehensive Discord-Minecraft bridge. It connects to Discord via JDA and optionally integrates with Vault, LuckPerms, and PlaceholderAPI.

## 📋 System Requirements

| Requirement | Minimum Version | Reason |
|-------------|-----------------|--------|
| **Platform** | Bukkit, Spigot, Paper, Purpur or Folia 1.21 - 26.1.2+ | Uses modern Paper API and Brigadier commands |
| **Java** | Java 21 LTS | Source code compiled in JDK 21 format |

## 📦 Dependencies

| Plugin | Required | Purpose |
|--------|----------|---------|
| **PlaceholderAPI** | Recommended | Enables `%placeholders%` in embeds and messages. Without it, placeholders show as literal text |
| **Vault** | Optional | Economy balance in profile panel (`/money` button) |
| **LuckPerms** | Optional | Role synchronization — Minecraft groups ↔ Discord roles |
| **EssentialsX** | Optional | Provides economy backend for Vault |

## 🚀 Installation Steps

1. Download the latest `EligiusConnector-x.x.x.jar` from the [Releases page](https://github.com/Eligiusmc/EligiusConnector/releases)
2. Copy the JAR into your server's `plugins/` folder
3. Install recommended plugins: **PlaceholderAPI**, **Vault**, **LuckPerms**, **EssentialsX**
4. Start your server — all configuration files will be generated in `plugins/EligiusConnector/`
5. Edit `config.yml` and set your Discord bot token
6. Restart the server or run `/connector reload`

![Startup Console](/assets/readme.png)

## ⚙️ First-Time Setup

After first start, you'll see the Eligius MC cat logo in console with module status. All YAML configs are generated with sensible defaults. The only required change is the Discord bot token in `config.yml`:

```yaml
bot:
  token: "YOUR_DISCORD_BOT_TOKEN"
  guild_id: "YOUR_DISCORD_SERVER_ID"
```

## 🔌 Discord Bot Setup

1. Go to [Discord Developer Portal](https://discord.com/developers/applications)
2. Create a new application → Add a bot
3. Enable **Message Content Intent**, **Server Members Intent**, and **Guild Messages Intent**
4. Copy the bot token and paste it in `config.yml`
5. Invite the bot to your server with `bot` and `applications.commands` scopes
