# EligiusConnector

The ultimate Discord-Minecraft bridge plugin for Bukkit, Spigot, Paper, Purpur & Folia.

## Features

- 🔗 **Account Linking:** Secure code-based linking between Discord and Minecraft accounts.
- 💬 **Chat Bridge:** Real-time chat between Minecraft and Discord.
- 🖥️ **Console Remote:** Execute console commands from Discord.
- 🔄 **Role Synchronization:** Bidirectional sync with LuckPerms.
- 📊 **Player Queries:** View profiles, inventory, location from Discord.
- 🎉 **Custom Events:** Configurable events with timer and command triggers.
- 🛡️ **Chat Filters:** Escalating punishment system.
- 📝 **Audit Logging:** Complete logging of all actions.
- 🎯 **PlaceholderAPI:** Full integration with placeholders.
- 🌐 **i18n:** Multi-language support (EN, ES).
- 📊 **bStats:** Anonymous usage metrics.

## Requirements

- Java 21 LTS or higher
- Minecraft 1.21 - 26.1.2+
- Paper, Spigot, Bukkit, Purpur, or Folia
- Discord Bot (with proper permissions)

## Commands

### Minecraft
- `/verify <code>` - Link your Minecraft account with Discord
- `/unlink [player]` - Unlink your account
- `/connector reload` - Reload configuration
- `/connector profile [player]` - View profile
- `/connector status` - View plugin status

### Discord
- `/verify` - Generate verification code
- `/unlink` - Unlink your account
- `/console <cmd>` - Execute console command
- `/player info <name>` - View player info

## Permissions

- `connector.verify` - Link account (default: true)
- `connector.unlink.self` - Unlink own account (default: true)
- `connector.unlink.other` - Unlink other players (default: op)
- `connector.console` - Execute console commands (default: false)
- `connector.chat.send` - Send chat messages (default: true)
- `connector.profile.self` - View own profile (default: true)
- `connector.profile.other` - View other profiles (default: op)
- `connector.reload` - Reload configuration (default: op)

## PlaceholderAPI

- `%connector_linked_<player>%` - Is player linked
- `%connector_discord_id_<player>%` - Discord ID
- `%connector_minecraft_name_<player>%` - Minecraft name
- `%connector_health_<player>%` - Player health
- `%connector_food_<player>%` - Player food
- `%connector_level_<player>%` - Player level
- `%connector_world_<player>%` - Player world
- `%connector_x_<player>%` - Player X coordinate
- `%connector_y_<player>%` - Player Y coordinate
- `%connector_z_<player>%` - Player Z coordinate

## Support

- [GitHub](https://github.com/Eligiusmc/EligiusConnector)
- [Discord](https://discord.gg/eligius)
- [Wiki](https://eligiusmc.github.io/EligiusConnector/)

## License

MIT License
