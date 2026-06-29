# Getting Started

This guide will help you install and configure EligiusConnector.

## Installation

### Download

Download the latest `EligiusConnector-x.x.x.jar` from one of these sources:

- [GitHub Releases](https://github.com/Eligiusmc/EligiusConnector/releases)
- [Modrinth](https://modrinth.com/plugin/eligiusconnector)
- [Hangar](https://hangar.papermc.io/eligius/EligiusConnector)

### Install

1. Stop your server (if running).
2. Place the `EligiusConnector-x.x.x.jar` file into your `plugins/` directory.
3. Start your server.
4. The plugin will generate default configuration files.

## Discord Bot Setup

### Create a Bot

1. Go to the [Discord Developer Portal](https://discord.com/developers/applications).
2. Click "New Application" and give it a name.
3. Go to the "Bot" section and click "Add Bot".
4. Copy the bot token (you'll need this later).

### Bot Permissions

The bot needs the following permissions:

- Send Messages
- Read Message History
- Embed Links
- Attach Files
- Use External Emojis
- Manage Messages (for chat filtering)
- View Channels
- Send Messages in Threads

### Invite Bot

1. Go to the "OAuth2" > "URL Generator" section.
2. Select the "bot" scope.
3. Select the permissions listed above.
4. Copy the generated URL and open it in your browser.
5. Select your server and authorize the bot.

## Configuration

### Main Config

Edit `plugins/EligiusConnector/config.yml`:

```yaml
BotToken: "your-bot-token-here"
ServerId: "your-discord-server-id"
```

### Channels

Configure the Discord channels:

```yaml
Channels:
  global: "your-global-channel-id"
  admin: "your-admin-channel-id"

DiscordConsoleChannelId: "your-console-channel-id"
```

### Database

Choose your database:

```yaml
Database:
  Type: "sqlite"  # sqlite, mysql, redis
```

## Verification

1. Check the console for the EligiusConnector logo and version.
2. Verify the bot is connected in Discord.
3. Test the `/verify` command in Minecraft.
4. Test the `/verify` command in Discord.

## Next Steps

- [Configuration](/config/) - Learn about all configuration options
- [Commands](/guide/commands) - View all available commands
- [Permissions](/guide/permissions) - Set up permissions
- [Placeholders](/guide/placeholders) - Use placeholders in your configs
