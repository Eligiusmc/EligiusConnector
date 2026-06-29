# Primeros Pasos

Esta guía te ayudará a instalar y configurar EligiusConnector.

## Instalación

### Descarga

Descarga el último `EligiusConnector-x.x.x.jar` desde una de estas fuentes:

- [GitHub Releases](https://github.com/Eligiusmc/EligiusConnector/releases)
- [Modrinth](https://modrinth.com/plugin/eligiusconnector)
- [Hangar](https://hangar.papermc.io/eligius/EligiusConnector)

### Instalar

1. Detén tu servidor (si está ejecutándose).
2. Coloca el archivo `EligiusConnector-x.x.x.jar` en tu directorio `plugins/`.
3. Inicia tu servidor.
4. El plugin generará archivos de configuración predeterminados.

## Configuración del Bot de Discord

### Crear un Bot

1. Ve al [Portal de Desarrolladores de Discord](https://discord.com/developers/applications).
2. Haz clic en "New Application" y dale un nombre.
3. Ve a la sección "Bot" y haz clic en "Add Bot".
4. Copia el token del bot (lo necesitarás después).

### Permisos del Bot

El bot necesita los siguientes permisos:

- Enviar Mensajes
- Leer Historial de Mensajes
- Adjuntar Archivos
- Usar Emojis Externos
- Gestionar Mensajes (para filtrado de chat)
- Ver Canales
- Enviar Mensajes en Hilos

### Invitar Bot

1. Ve a la sección "OAuth2" > "URL Generator".
2. Selecciona el alcance "bot".
3. Selecciona los permisos listados arriba.
4. Copia la URL generada y ábrela en tu navegador.
5. Selecciona tu servidor y autoriza al bot.

## Configuración

### Configuración Principal

Edita `plugins/EligiusConnector/config.yml`:

```yaml
BotToken: "tu-token-de-bot-aquí"
ServerId: "tu-id-de-servidor-discord"
```

### Canales

Configura los canales de Discord:

```yaml
Channels:
  global: "tu-id-de-canal-global"
  admin: "tu-id-de-canal-admin"

DiscordConsoleChannelId: "tu-id-de-canal-consola"
```

### Base de Datos

Elige tu base de datos:

```yaml
Database:
  Type: "sqlite"  # sqlite, mysql, redis
```

## Verificación

1. Verifica el logo y versión de EligiusConnector en la consola.
2. Verifica que el bot esté conectado en Discord.
3. Prueba el comando `/verify` en Minecraft.
4. Prueba el comando `/verify` en Discord.

## Próximos Pasos

- [Configuración](/es/config/) - Aprende todas las opciones de configuración
- [Comandos](/es/guia/comandos) - Ver todos los comandos disponibles
- [Permisos](/es/guia/permisos) - Configurar permisos
- [Placeholders](/es/guia/placeholders) - Usar placeholders en tus configs
