# EligiusConnector

*The ultimate Discord-Minecraft bridge plugin for Bukkit, Spigot, Paper, Purpur & Folia.*

[![Paper & Spigot API](https://img.shields.io/badge/Bukkit_%7C_Spigot_%7C_Paper_%7C_Purpur-1.21+-333333?style=flat-square&logo=paper)](https://papermc.io/) [![Folia Compatible](https://img.shields.io/badge/Folia-Compatible-ff5e00?style=flat-square&logo=fastapi)](https://papermc.io/software/folia) [![Java 21](https://img.shields.io/badge/Java-21_LTS-007396?style=flat-square&logo=openjdk)](https://adoptium.net/) [![License](https://img.shields.io/github/license/Eligiusmc/EligiusConnector?style=flat-square&color=blue)](/LICENSE) [![Release](https://img.shields.io/github/v/release/Eligiusmc/EligiusConnector?style=flat-square&color=success)](https://github.com/Eligiusmc/EligiusConnector/releases) [![Wiki](https://img.shields.io/badge/Wiki-Multi--Language-blueviolet?style=flat-square&logo=gitbook)](https://eligiusmc.github.io/EligiusConnector/)

[📚 **Read the Official Wiki (EN/ES)**](https://eligiusmc.github.io/EligiusConnector/) • [🐛 **Report an Issue**](https://github.com/Eligiusmc/EligiusConnector/issues)

---

## 🌟 Overview

**EligiusConnector** is a next-generation Discord-Minecraft bridge plugin built for Minecraft 1.21 - 26.1.2+ servers running Bukkit, Spigot, Paper, Purpur or Folia. It connects your Discord server with your Minecraft server, allowing seamless communication and management.

### ✨ Key Features

- ⚡ **True Folia Support:** Built from the ground up with asynchronous, thread-safe architecture.
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

---

## 🚀 Quick Start

1. Download the latest `EligiusConnector-x.x.x.jar` from the [Releases](https://github.com/Eligiusmc/EligiusConnector/releases) page.
2. Drop it into your `plugins/` directory.
3. Start your server!
4. Visit the **[Wiki](https://eligiusmc.github.io/EligiusConnector/)** to learn how to configure.

### ⚠️ Requirements

- **Java 21 LTS** or higher
- **Minecraft 1.21 - 26.1.2+**
- **Paper, Spigot, Bukkit, Purpur, or Folia**
- **Discord Bot** (with proper permissions)

---

## 💻 Commands

### Minecraft Commands
| Command | Description |
|---------|-------------|
| `/verify <code>` | Link your Minecraft account with Discord |
| `/unlink [player]` | Unlink your account |
| `/connector reload` | Reload configuration |
| `/connector profile [player]` | View profile |
| `/connector status` | View plugin status |
| `/connector papi` | View placeholders |

### Discord Commands
| Command | Description |
|---------|-------------|
| `/verify` | Generate verification code |
| `/unlink` | Unlink your account |
| `/console <cmd>` | Execute console command |
| `/player info <name>` | View player info |
| `/player inventory <name>` | View player inventory |
| `/player location <name>` | View player location |

---

## 🔐 Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `connector.verify` | true | Link account |
| `connector.unlink.self` | true | Unlink own account |
| `connector.unlink.other` | op | Unlink other players |
| `connector.console` | false | Execute console commands |
| `connector.chat.send` | true | Send chat messages |
| `connector.profile.self` | true | View own profile |
| `connector.profile.other` | op | View other profiles |
| `connector.reload` | op | Reload configuration |
| `connector.admin` | op | Admin commands |

---

## 🎯 PlaceholderAPI

| Placeholder | Description |
|-------------|-------------|
| `%connector_linked_<player>%` | Is player linked |
| `%connector_discord_id_<player>%` | Discord ID |
| `%connector_discord_name_<player>%` | Discord name |
| `%connector_minecraft_name_<player>%` | Minecraft name |
| `%connector_health_<player>%` | Player health |
| `%connector_food_<player>%` | Player food |
| `%connector_level_<player>%` | Player level |
| `%connector_world_<player>%` | Player world |
| `%connector_x_<player>%` | Player X coordinate |
| `%connector_y_<player>%` | Player Y coordinate |
| `%connector_z_<player>%` | Player Z coordinate |

---

## 👨‍💻 For Developers & Contributors

We love open-source contributions! If you want to dive into the codebase, fix bugs, or understand our Hexagonal Architecture:

### Building from Source

```bash
# Clone the repository
git clone https://github.com/Eligiusmc/EligiusConnector.git
cd EligiusConnector

# Build the jar using Gradle
./gradlew build
```

The compiled `.jar` will be available in `build/libs/`.

### Contributing Best Practices

- **Branching:** Never push directly to `master`. All work should stem from `develop` into `feature/<name>` branches.
- **Commits:** We strictly enforce [Conventional Commits](https://www.conventionalcommits.org/) (e.g., `feat:`, `fix:`, `docs:`).
- **Documentation:** If you add a new feature, please update the multilingual Wiki.

---

## 📦 Installation

### Modrinth
[![Modrinth](https://img.shields.io/modrinth/dt/eligiusconnector?label=modrinth&logo=modrinth)](https://modrinth.com/plugin/eligiusconnector)

### Hangar
[![Hangar](https://img.shields.io/hangar/dt/eligiusconnector?label=hangar&logo=paper)](https://hangar.papermc.io/eligius/EligiusConnector)

### SpigotMC
[![SpigotMC](https://img.shields.io/badge/SpigotMC-EligiusConnector-yellow?logo=spigotmc)](https://www.spigotmc.org/resources/eligiusconnector/)

### GitHub Releases
[![GitHub Releases](https://img.shields.io/github/v/release/Eligiusmc/EligiusConnector?label=github&logo=github)](https://github.com/Eligiusmc/EligiusConnector/releases)

---

## 🤝 Community

- [Discord Server](https://discord.gg/eligius)
- [GitHub Issues](https://github.com/Eligiusmc/EligiusConnector/issues)
- [Wiki](https://eligiusmc.github.io/EligiusConnector/)

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

## 🙏 Support

If you need help, join our [Discord server](https://discord.gg/eligius) or open an [issue](https://github.com/Eligiusmc/EligiusConnector/issues).
