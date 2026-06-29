# Comandos

EligiusConnector proporciona varios comandos para Minecraft y Discord.

## Comandos de Minecraft

### /verify

Vincula tu cuenta de Minecraft con Discord.

```
/verify <código>
```

**Parámetros:**
- `código` - El código de 6 caracteres generado desde Discord

**Permiso:** `connector.verify` (predeterminado: true)

### /unlink

Desvincula tu cuenta de Minecraft de Discord.

```
/unlink [jugador]
```

**Parámetros:**
- `jugador` - (Opcional) Nombre del jugador a desvincular (solo admin)

**Permiso:** `connector.unlink.self` (predeterminado: true), `connector.unlink.other` (predeterminado: op)

### /connector

Comando principal del plugin.

```
/connector <subcomando> [args]
```

**Subcomandos:**
- `reload` - Recargar configuración
- `profile [jugador]` - Ver perfil
- `status` - Ver estado del plugin
- `papi` - Ver placeholders disponibles

**Permisos:**
- `connector.reload` (predeterminado: op)
- `connector.profile.self` (predeterminado: true)
- `connector.profile.other` (predeterminado: op)
- `connector.admin` (predeterminado: op)
- `connector.papi` (predeterminado: op)

## Comandos de Discord

### /verify

Genera un código de verificación.

```
/verify
```

**Descripción:** Envía un DM con un código de 6 caracteres para vincular tu cuenta de Minecraft.

### /unlink

Desvincula tu cuenta de Discord de Minecraft.

```
/unlink
```

**Descripción:** Elimina el enlace entre tus cuentas de Discord y Minecraft.

### /console

Ejecuta un comando de consola.

```
/console <comando>
```

**Parámetros:**
- `comando` - El comando a ejecutar

**Permiso:** MANAGE_SERVER (permiso de Discord)

### /player

Ver información del jugador.

```
/player <subcomando> <nombre>
```

**Subcomandos:**
- `info` - Ver info del jugador
- `inventory` - Ver inventario del jugador
- `location` - Ver ubicación del jugador
- `roles` - Ver roles del jugador
- `homes` - Ver homes del jugador
- `jobs` - Ver jobs del jugador
- `clan` - Ver clan del jugador

## Ejemplos de Comandos

### Vincular Cuenta

1. En Discord, escribe: `/verify`
2. Revisa tus DMs para el código.
3. En Minecraft, escribe: `/verify ABC123`

### Ver Perfil

En Minecraft:
```
/connector profile
/connector profile Notch
```

En Discord:
```
/player info Notch
```

### Ejecutar Comando de Consola

En Discord:
```
/console say Hola Mundo
/console list
```

## Enfriamientos

Los comandos tienen enfriamientos integrados para prevenir abuso:

| Comando | Enfriamiento |
|---------|--------------|
| `/verify` | 1 minuto |
| `/unlink` | 5 minutos |
| `/console` | 3 segundos |
| `/player` | 2 segundos |

## Mensajes de Error

| Error | Significado |
|-------|-------------|
| "Formato de código inválido" | El código debe ser 6 caracteres alfanuméricos |
| "Código expirado" | Genera un nuevo código desde Discord |
| "Cuenta ya vinculada" | Usa `/unlink` primero |
| "Jugador no encontrado" | Verifica el nombre del jugador |
| "Sin permiso" | No tienes el permiso requerido |
