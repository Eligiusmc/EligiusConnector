# Placeholders

EligiusConnector proporciona integración con PlaceholderAPI para usar en configs, mensajes y otros plugins.

## Placeholders Disponibles

### Estado de Cuenta

| Placeholder | Descripción | Ejemplo |
|-------------|-------------|---------|
| `%connector_linked_<jugador>%` | ¿Está vinculado el jugador? | `true` |
| `%connector_linked_count%` | Total de cuentas vinculadas | `150` |

### Información de Discord

| Placeholder | Descripción | Ejemplo |
|-------------|-------------|---------|
| `%connector_discord_id_<jugador>%` | ID de Discord | `123456789012345678` |
| `%connector_discord_name_<jugador>%` | Nombre de usuario de Discord | `Usuario#1234` |
| `%connector_discord_roles_<jugador>%` | Roles de Discord | `Admin, Mod` |

### Información de Minecraft

| Placeholder | Descripción | Ejemplo |
|-------------|-------------|---------|
| `%connector_minecraft_name_<jugador>%` | Nombre de usuario de Minecraft | `Notch` |
| `%connector_minecraft_uuid_<jugador>%` | UUID de Minecraft | `069a79f4-44e9-4726-a5be-fca90e38aaf5` |

### Estadísticas del Jugador

| Placeholder | Descripción | Ejemplo |
|-------------|-------------|---------|
| `%connector_health_<jugador>%` | Vida del jugador | `20` |
| `%connector_food_<jugador>%` | Nivel de comida | `20` |
| `%connector_level_<jugador>%` | Nivel del jugador | `30` |
| `%connector_xp_<jugador>%` | XP del jugador | `15.5` |

### Ubicación

| Placeholder | Descripción | Ejemplo |
|-------------|-------------|---------|
| `%connector_world_<jugador>%` | Mundo del jugador | `world` |
| `%connector_x_<jugador>%` | Coordenada X | `100` |
| `%connector_y_<jugador>%` | Coordenada Y | `64` |
| `%connector_z_<jugador>%` | Coordenada Z | `-200` |
| `%connector_location_<jugador>%` | Ubicación completa | `world, 100, 64, -200` |

### Integración de Clanes

| Placeholder | Descripción | Ejemplo |
|-------------|-------------|---------|
| `%connector_clan_<jugador>%` | Clan del jugador | `Guerreros` |
| `%connector_clan_tag_<jugador>%` | Tag del clan | `[WAR]` |
| `%connector_clan_leader_<jugador>%` | Líder del clan | `Notch` |

### Integración con LuckPerms

| Placeholder | Descripción | Ejemplo |
|-------------|-------------|---------|
| `%connector_group_<jugador>%` | Grupo principal de LuckPerms | `admin` |
| `%connector_groups_<jugador>%` | Todos los grupos de LuckPerms | `admin, vip, default` |
| `%connector_prefix_<jugador>%` | Prefijo de LuckPerms | `[Admin]` |
| `%connector_suffix_<jugador>%` | Sufijo de LuckPerms | `✦` |

## Ejemplos de Uso

### En config.yml

```yaml
# Estado del bot con placeholders
DiscordGameStatus:
  - "Jugando con %playerlist_online% jugadores"
  - "Viendo %connector_linked_count% cuentas vinculadas"

# URL del avatar
AvatarUrl: "https://minotar.net/avatar/%player_name%/128"
```

### En chat.yml

```yaml
# Formato Minecraft a Discord
MinecraftToDiscordFormat: "[MC] %player_displayname%: {message}"

# Formato Discord a Minecraft
DiscordToMinecraftFormat: "[DC] %username%: {message}"
```

### En notifications.yml

```yaml
# Mensaje de unión
Message: ":arrow_right: **%player_displayname%** se ha conectado.\n🌍 Mundo: %player_world%\n👥 Online: %playerlist_online%"

# Mensaje de muerte
Message: ":skull: **%player_displayname%** ha muerto.\n❤️ Vida: %connector_health_%player_name%%"
```

### En synchronization.yml

```yaml
# Formato de nickname
Format: "[%connector_clan_tag_%player_name%%] %player_displayname%"
```

### En events.yml

```yaml
# Mensajes de eventos
OnStart:
  Messages:
    - ":skull: **¡BOSS APARECIO!**\n⏱️ Duración: {duration} minutos\n👥 Jugadores: %playerlist_online%"
```

## Soporte de Placeholders Externos

EligiusConnector soporta placeholders de otros plugins:

### Vault

| Placeholder | Descripción |
|-------------|-------------|
| `%vault_prefix%` | Prefijo de Vault |
| `%vault_suffix%` | Sufijo de Vault |
| `%vault_group%` | Grupo principal de Vault |

### LuckPerms

| Placeholder | Descripción |
|-------------|-------------|
| `%luckperms_group%` | Grupo principal de LuckPerms |
| `%luckperms_prefix%` | Prefijo de LuckPerms |
| `%luckperms_suffix%` | Sufijo de LuckPerms |

### ClansX

| Placeholder | Descripción |
|-------------|-------------|
| `%clans_clan_name%` | Nombre del clan |
| `%clans_clan_tag%` | Tag del clan |
| `%clans_clan_leader%` | Líder del clan |

### Essentials

| Placeholder | Descripción |
|-------------|-------------|
| `%essentials_nick%` | Nickname del jugador |
| `%essentials_balance%` | Balance del jugador |

## Caché de Placeholders

EligiusConnector almacena en caché los placeholders para mejorar el rendimiento:

```yaml
PlaceholderAPI:
  Enabled: true
  CachePlaceholders: true
  CacheTimeInSeconds: 30
```

**Comportamiento del caché:**
- Los placeholders se almacenan en caché por el tiempo especificado
- El caché se limpia automáticamente después del timeout
- Usa `/connector papi` para ver placeholders disponibles

## Solución de Problemas

### Placeholder no funciona

1. Verifica que PlaceholderAPI esté instalado
2. Verifica la sintaxis del placeholder
3. Verifica que el placeholder esté disponible: `/papi list`
4. Verifica que la expansión esté cargada: `/papi ecloud list`

### Placeholder muestra texto sin procesar

1. Asegúrate de que PlaceholderAPI esté instalado
2. Verifica que la sintaxis del placeholder sea correcta
3. Verifica que la expansión esté registrada

### Problemas de rendimiento

1. Habilita el caché de placeholders
2. Incrementa el tiempo de caché
3. Usa placeholders específicos en lugar de wildcards
