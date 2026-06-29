# Commands

EligiusConnector provides various commands for both Minecraft and Discord.

## Minecraft Commands

### /verify

Link your Minecraft account with Discord.

```
/verify <code>
```

**Parameters:**
- `code` - The 6-character code generated from Discord

**Permission:** `connector.verify` (default: true)

### /unlink

Unlink your Minecraft account from Discord.

```
/unlink [player]
```

**Parameters:**
- `player` - (Optional) Name of the player to unlink (admin only)

**Permission:** `connector.unlink.self` (default: true), `connector.unlink.other` (default: op)

### /connector

Main plugin command.

```
/connector <subcommand> [args]
```

**Subcommands:**
- `reload` - Reload configuration
- `profile [player]` - View profile
- `status` - View plugin status
- `papi` - View available placeholders

**Permissions:**
- `connector.reload` (default: op)
- `connector.profile.self` (default: true)
- `connector.profile.other` (default: op)
- `connector.admin` (default: op)
- `connector.papi` (default: op)

## Discord Commands

### /verify

Generate a verification code.

```
/verify
```

**Description:** Sends a DM with a 6-character code to link your Minecraft account.

### /unlink

Unlink your Discord account from Minecraft.

```
/unlink
```

**Description:** Removes the link between your Discord and Minecraft accounts.

### /console

Execute a console command.

```
/console <command>
```

**Parameters:**
- `command` - The command to execute

**Permission:** MANAGE_SERVER (Discord permission)

### /player

View player information.

```
/player <subcommand> <name>
```

**Subcommands:**
- `info` - View player info
- `inventory` - View player inventory
- `location` - View player location
- `roles` - View player roles
- `homes` - View player homes
- `jobs` - View player jobs
- `clan` - View player clan

## Command Examples

### Link Account

1. In Discord, type: `/verify`
2. Check your DMs for the code.
3. In Minecraft, type: `/verify ABC123`

### View Profile

In Minecraft:
```
/connector profile
/connector profile Notch
```

In Discord:
```
/player info Notch
```

### Execute Console Command

In Discord:
```
/console say Hello World
/console list
```

## Cooldowns

Commands have built-in cooldowns to prevent abuse:

| Command | Cooldown |
|---------|----------|
| `/verify` | 1 minute |
| `/unlink` | 5 minutes |
| `/console` | 3 seconds |
| `/player` | 2 seconds |

## Error Messages

| Error | Meaning |
|-------|---------|
| "Invalid code format" | Code must be 6 alphanumeric characters |
| "Code expired" | Generate a new code from Discord |
| "Account already linked" | Use `/unlink` first |
| "Player not found" | Check the player name |
| "No permission" | You don't have the required permission |
