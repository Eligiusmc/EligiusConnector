# Permissions

EligiusConnector uses a comprehensive permission system to control access to features.

## Permission Nodes

### Account Linking

| Permission | Default | Description |
|------------|---------|-------------|
| `connector.verify` | true | Allows linking account |
| `connector.unlink.self` | true | Allows unlinking own account |
| `connector.unlink.other` | op | Allows unlinking other players |

### Console Remote

| Permission | Default | Description |
|------------|---------|-------------|
| `connector.console` | false | Allows executing console commands |
| `connector.console.stop` | false | Allows stopping the server |
| `connector.console.restart` | false | Allows restarting the server |

### Chat

| Permission | Default | Description |
|------------|---------|-------------|
| `connector.chat.send` | true | Allows sending chat messages |
| `connector.chat.filter.bypass` | op | Bypass chat filters |

### Player Queries

| Permission | Default | Description |
|------------|---------|-------------|
| `connector.profile.self` | true | View own profile |
| `connector.profile.other` | op | View other players profile |
| `connector.inventory.self` | true | View own inventory |
| `connector.inventory.other` | op | View other players inventory |
| `connector.location.self` | true | View own location |
| `connector.location.other` | op | View other players location |

### Roles

| Permission | Default | Description |
|------------|---------|-------------|
| `connector.roles.self` | true | View own roles |
| `connector.roles.sync` | op | Force role synchronization |

### Homes & Jobs

| Permission | Default | Description |
|------------|---------|-------------|
| `connector.homes.self` | true | View own homes |
| `connector.homes.other` | op | View other players homes |
| `connector.jobs.self` | true | View own jobs |
| `connector.jobs.other` | op | View other players jobs |

### Events

| Permission | Default | Description |
|------------|---------|-------------|
| `connector.events.start` | op | Start custom events |
| `connector.events.stop` | op | Stop custom events |

### Notifications

| Permission | Default | Description |
|------------|---------|-------------|
| `connector.notifications.receive` | true | Receive notifications |

### Admin

| Permission | Default | Description |
|------------|---------|-------------|
| `connector.reload` | op | Reload configuration |
| `connector.admin` | op | Admin commands |
| `connector.papi` | op | View available placeholders |

## Setting Permissions

### LuckPerms

```bash
# Grant permission
lp user <player> permission set connector.verify true

# Grant group permission
lp group <group> permission set connector.console true

# Check permissions
lp user <player> permission list
```

### PermissionsEx

```bash
# Grant permission
pex user <player> add connector.verify

# Grant group permission
pex group <group> add connector.console
```

### Bukkit Permissions

Edit `plugins/PermissionEx/permissions.yml` or use the `/op` command.

## Permission Examples

### Admin Setup

```bash
lp group admin permission set connector.admin true
lp group admin permission set connector.console true
lp group admin permission set connector.console.stop true
lp group admin permission set connector.console.restart true
```

### Moderator Setup

```bash
lp group mod permission set connector.chat.filter.bypass true
lp group mod permission set connector.profile.other true
lp group mod permission set connector.inventory.other true
```

### VIP Setup

```bash
lp group vip permission set connector.chat.send true
lp group vip permission set connector.profile.self true
```

### Default Setup

```bash
lp group default permission set connector.verify true
lp group default permission set connector.unlink.self true
lp group default permission set connector.chat.send true
lp group default permission set connector.profile.self true
```

## Permission Inheritance

Permissions can be inherited from parent groups:

```bash
# Set parent group
lp group mod parent add default

# Now mod inherits all default permissions
```

## Debugging Permissions

### Check Player Permissions

```bash
# In Minecraft
/connector status

# In console
lp user <player> permission list
```

### Check Group Permissions

```bash
lp group <group> permission list
```

### Test Permission

```bash
# Check if player has permission
lp user <player> has connector.console
```

## Common Issues

| Issue | Solution |
|-------|----------|
| "No permission" | Check if the player has the required permission |
| Permission not working | Verify the permission node is correct |
| Group permissions not applying | Check parent group inheritance |
| Console commands not working | Ensure `connector.console` is granted |
