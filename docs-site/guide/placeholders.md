# Placeholders

EligiusConnector provides PlaceholderAPI integration for use in configs, messages, and other plugins.

## Available Placeholders

### Account Status

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%connector_linked_<player>%` | Is player linked | `true` |
| `%connector_linked_count%` | Total linked accounts | `150` |

### Discord Information

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%connector_discord_id_<player>%` | Discord ID | `123456789012345678` |
| `%connector_discord_name_<player>%` | Discord username | `User#1234` |
| `%connector_discord_roles_<player>%` | Discord roles | `Admin, Mod` |

### Minecraft Information

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%connector_minecraft_name_<player>%` | Minecraft username | `Notch` |
| `%connector_minecraft_uuid_<player>%` | Minecraft UUID | `069a79f4-44e9-4726-a5be-fca90e38aaf5` |

### Player Stats

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%connector_health_<player>%` | Player health | `20` |
| `%connector_food_<player>%` | Player food level | `20` |
| `%connector_level_<player>%` | Player level | `30` |
| `%connector_xp_<player>%` | Player XP | `15.5` |

### Location

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%connector_world_<player>%` | Player world | `world` |
| `%connector_x_<player>%` | X coordinate | `100` |
| `%connector_y_<player>%` | Y coordinate | `64` |
| `%connector_z_<player>%` | Z coordinate | `-200` |
| `%connector_location_<player>%` | Full location | `world, 100, 64, -200` |

### Clan Integration

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%connector_clan_<player>%` | Player's clan | `Warriors` |
| `%connector_clan_tag_<player>%` | Player's clan tag | `[WAR]` |
| `%connector_clan_leader_<player>%` | Clan leader | `Notch` |

### LuckPerms Integration

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%connector_group_<player>%` | LuckPerms primary group | `admin` |
| `%connector_groups_<player>%` | All LuckPerms groups | `admin, vip, default` |
| `%connector_prefix_<player>%` | LuckPerms prefix | `[Admin]` |
| `%connector_suffix_<player>%` | LuckPerms suffix | `✦` |

## Usage Examples

### In config.yml

```yaml
# Bot status with placeholders
DiscordGameStatus:
  - "Playing with %playerlist_online% players"
  - "Watching %connector_linked_count% linked accounts"

# Avatar URL
AvatarUrl: "https://minotar.net/avatar/%player_name%/128"
```

### In chat.yml

```yaml
# Minecraft to Discord format
MinecraftToDiscordFormat: "[MC] %player_displayname%: {message}"

# Discord to Minecraft format
DiscordToMinecraftFormat: "[DC] %username%: {message}"
```

### In notifications.yml

```yaml
# Join message
Message: ":arrow_right: **%player_displayname%** has joined.\n🌍 World: %player_world%\n👥 Online: %playerlist_online%"

# Death message
Message: ":skull: **%player_displayname%** has died.\n❤️ Health: %connector_health_%player_name%%"
```

### In synchronization.yml

```yaml
# Nickname format
Format: "[%connector_clan_tag_%player_name%%] %player_displayname%"
```

### In events.yml

```yaml
# Event messages
OnStart:
  Messages:
    - ":skull: **BOSS SPAWNED!**\n⏱️ Duration: {duration} minutes\n👥 Players: %playerlist_online%"
```

## External Placeholder Support

EligiusConnector supports placeholders from other plugins:

### Vault

| Placeholder | Description |
|-------------|-------------|
| `%vault_prefix%` | Vault prefix |
| `%vault_suffix%` | Vault suffix |
| `%vault_group%` | Primary vault group |

### LuckPerms

| Placeholder | Description |
|-------------|-------------|
| `%luckperms_group%` | Primary LuckPerms group |
| `%luckperms_prefix%` | LuckPerms prefix |
| `%luckperms_suffix%` | LuckPerms suffix |

### ClansX

| Placeholder | Description |
|-------------|-------------|
| `%clans_clan_name%` | Clan name |
| `%clans_clan_tag%` | Clan tag |
| `%clans_clan_leader%` | Clan leader |

### Essentials

| Placeholder | Description |
|-------------|-------------|
| `%essentials_nick%` | Player nickname |
| `%essentials_balance%` | Player balance |

## Placeholder Caching

EligiusConnector caches placeholders to improve performance:

```yaml
PlaceholderAPI:
  Enabled: true
  CachePlaceholders: true
  CacheTimeInSeconds: 30
```

**Cache behavior:**
- Placeholders are cached for the specified time
- Cache is cleared automatically after the timeout
- Use `/connector papi` to view available placeholders

## Troubleshooting

### Placeholder not working

1. Check if PlaceholderAPI is installed
2. Verify the placeholder syntax
3. Check the placeholder is available: `/papi list`
4. Check the expansion is loaded: `/papi ecloud list`

### Placeholder shows raw text

1. Ensure PlaceholderAPI is installed
2. Check the placeholder syntax is correct
3. Verify the expansion is registered

### Performance issues

1. Enable placeholder caching
2. Increase cache time
3. Use specific placeholders instead of wildcards
