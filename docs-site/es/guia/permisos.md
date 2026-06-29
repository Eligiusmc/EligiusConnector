# Permisos

EligiusConnector utiliza un sistema de permisos completo para controlar el acceso a las funciones.

## Nodos de Permisos

### Vinculación de Cuentas

| Permiso | Predeterminado | Descripción |
|---------|----------------|-------------|
| `connector.verify` | true | Permite vincular cuenta |
| `connector.unlink.self` | true | Permite desvincular cuenta propia |
| `connector.unlink.other` | op | Permite desvincular otros jugadores |

### Consola Remota

| Permiso | Predeterminado | Descripción |
|---------|----------------|-------------|
| `connector.console` | false | Permite ejecutar comandos de consola |
| `connector.console.stop` | false | Permite parar el servidor |
| `connector.console.restart` | false | Permite reiniciar el servidor |

### Chat

| Permiso | Predeterminado | Descripción |
|---------|----------------|-------------|
| `connector.chat.send` | true | Permite enviar mensajes de chat |
| `connector.chat.filter.bypass` | op | Saltar filtros de chat |

### Consultas de Jugadores

| Permiso | Predeterminado | Descripción |
|---------|----------------|-------------|
| `connector.profile.self` | true | Ver perfil propio |
| `connector.profile.other` | op | Ver perfil de otros |
| `connector.inventory.self` | true | Ver inventario propio |
| `connector.inventory.other` | op | Ver inventario de otros |
| `connector.location.self` | true | Ver ubicación propia |
| `connector.location.other` | op | Ver ubicación de otros |

### Roles

| Permiso | Predeterminado | Descripción |
|---------|----------------|-------------|
| `connector.roles.self` | true | Ver roles propios |
| `connector.roles.sync` | op | Forzar sincronización de roles |

### Homes y Jobs

| Permiso | Predeterminado | Descripción |
|---------|----------------|-------------|
| `connector.homes.self` | true | Ver homes propios |
| `connector.homes.other` | op | Ver homes de otros |
| `connector.jobs.self` | true | Ver jobs propios |
| `connector.jobs.other` | op | Ver jobs de otros |

### Eventos

| Permiso | Predeterminado | Descripción |
|---------|----------------|-------------|
| `connector.events.start` | op | Iniciar eventos personalizados |
| `connector.events.stop` | op | Detener eventos |

### Notificaciones

| Permiso | Predeterminado | Descripción |
|---------|----------------|-------------|
| `connector.notifications.receive` | true | Recibir notificaciones |

### Admin

| Permiso | Predeterminado | Descripción |
|---------|----------------|-------------|
| `connector.reload` | op | Recargar configuración |
| `connector.admin` | op | Comandos admin |
| `connector.papi` | op | Ver placeholders disponibles |

## Configurar Permisos

### LuckPerms

```bash
# Otorgar permiso
lp user <jugador> permission set connector.verify true

# Otorgar permiso de grupo
lp group <grupo> permission set connector.console true

# Verificar permisos
lp user <jugador> permission list
```

### PermissionsEx

```bash
# Otorgar permiso
pex user <jugador> add connector.verify

# Otorgar permiso de grupo
pex group <grupo> add connector.console
```

### Permisos de Bukkit

Edita `plugins/PermissionEx/permissions.yml` o usa el comando `/op`.

## Ejemplos de Permisos

### Configuración de Admin

```bash
lp group admin permission set connector.admin true
lp group admin permission set connector.console true
lp group admin permission set connector.console.stop true
lp group admin permission set connector.console.restart true
```

### Configuración de Moderador

```bash
lp group mod permission set connector.chat.filter.bypass true
lp group mod permission set connector.profile.other true
lp group mod permission set connector.inventory.other true
```

### Configuración de VIP

```bash
lp group vip permission set connector.chat.send true
lp group vip permission set connector.profile.self true
```

### Configuración por Defecto

```bash
lp group default permission set connector.verify true
lp group default permission set connector.unlink.self true
lp group default permission set connector.chat.send true
lp group default permission set connector.profile.self true
```

## Herencia de Permisos

Los permisos pueden heredarse de grupos padre:

```bash
# Establecer grupo padre
lp group mod parent add default

# Ahora mod hereda todos los permisos de default
```

## Depuración de Permisos

### Verificar Permisos de Jugador

```bash
# En Minecraft
/connector status

# En consola
lp user <jugador> permission list
```

### Verificar Permisos de Grupo

```bash
lp group <grupo> permission list
```

### Probar Permiso

```bash
# Verificar si el jugador tiene el permiso
lp user <jugador> has connector.console
```

## Problemas Comunes

| Problema | Solución |
|----------|----------|
| "Sin permiso" | Verifica que el jugador tenga el permiso requerido |
| Permiso no funciona | Verifica que el nodo de permiso sea correcto |
| Permisos de grupo no aplican | Verifica la herencia del grupo padre |
| Comandos de consola no funcionan | Asegúrate de que `connector.console` esté otorgado |
