# EligiusConnector — Reglas del Proyecto

## Ecosistema Eligius

Este proyecto pertenece al ecosistema **Eligiusmc**. Seguir la misma arquitectura y convenciones que:
- [EligiusNametag](https://github.com/Eligiusmc/EligiusNametag)
- [EligiusHiddenArmor](https://github.com/Eligiusmc/EligiusHiddenArmor)

## Stack Obligatorio

- **Java 21 LTS** (sin excepciones)
- **Gradle Kotlin DSL** (`build.gradle.kts`, `settings.gradle.kts`)
- **Paper/Spigot API** 1.21 - 26.1.2+
- **Folia compatible** (async scheduler via `Scheduler` utility — ver seccion abajo)
- **Hexagonal Architecture** (puertos y adaptadores)

## Arquitectura del Proyecto

```
src/main/java/com/makrozai/eligiusconnector/
├── EligiusConnector.java        # Plugin principal, onEnable/onDisable
├── config/ConfigAdapter.java    # Carga de todos los YAMLs (status, chat, verify, etc)
├── config/LanguageManager.java  # i18n multi-idioma (lang/*.yml)
├── database/DatabaseManager.java # HikariCP (SQLite/MySQL), operaciones CRUD
├── discord/DiscordManager.java  # JDA: conexion, embeds, channels, webhooks
├── discord/DiscordListener.java # Eventos de Discord (slash commands, mensajes)
├── discord/ButtonListener.java  # Botones interactivos (verify, panels)
├── discord/PanelManager.java    # Paneles de perfil/birthday/verify
├── discord/WebhookManager.java  # Webhooks para chat bridge (avatar+nombre)
├── discord/ConsoleLogReader.java # Lee logs del server → canal Discord
├── counters/                    # Sistema de contadores dinamicos (counters.yml)
│   ├── CounterManager.java      # Carga y gestiona periodic + state counters
│   ├── CounterConfig.java       # Record de config para cada counter
│   ├── StateManager.java        # Estados observables (server_online, whitelist, etc)
│   ├── StateCounter.java        # Renombre de canal reactivo a cambios de estado
│   └── PeriodicCounter.java     # Renombre de canal por intervalo (placeholders)
├── placeholders/PlaceholderResolver.java  # %placeholder% → valor (PAPI + built-ins)
├── listeners/PlayerListener.java  # Chat, join/leave, death, advancements → Discord
├── listeners/StatsListener.java   # Stats (kills, blocks, etc)
├── stats/PlayerStatsManager.java  # CRUD de estadisticas en DB
├── events/                       # Sistema de eventos custom (bosses, torneos)
├── commands/                     # /verify, /unlink, /connector, /events, /birthday, /chat
├── tasks/NicknameSyncTask.java   # Sincroniza nicknames Minecraft ↔ Discord
└── util/
    ├── StartupLogger.java        # Logs colorizados de arranque
    └── Scheduler.java           # Adaptador Folia (detecta plataforma, usa API correcta)

src/main/resources/
├── config.yml          # Token, server_id, modulos, database, language
├── status.yml          # Embeds online/offline (canal compartido con counters.yml)
├── counters.yml        # Contadores: online, all_members, server_status (state)
├── chat.yml            # Config del bridge de chat
├── join_leave.yml      # Embeds de join/leave/death/advancement
├── verify.yml          # Verificacion + sonidos + comandos post-verify
├── birthday.yml        # Sistema de cumpleanos
├── console.yml         # Log reader
├── synchronization.yml # Roles y nickname sync
├── profile.yml         # Embeds de perfil/inventario
├── events/*.yml        # Config de eventos (bosses, torneos)
└── lang/*.yml          # en, es, de, fr, pt, ru
```

### ⚠️ Contrato del canal de Status

El canal `1521276573033173204` (configurado en `status.yml`) es **compartido por 2 subsistemas**:

| Subsistema | Archivo | Que hace |
|---|---|---|
| Status embed | `status.yml` → `onEnable`/`onDisable` | Publica embed online/offline, borra el anterior |
| State counter `server_status` | `counters.yml` → `StateCounter` | Renombre el canal a "Online"/"Offline" |

Ambos apuntan al mismo `channel:` por coincidencia de config. NO modificar uno sin revisar el otro.
La interaccion es intencional: el counter renombre el canal, el embed publica el mensaje visual.

### ⚠️ Bug conocido (fixeado Jul 2026)

**Sintoma:** Al cerrar el servidor, el nombre del canal cambia pero el embed offline no se publica
ni se borra el online anterior.

**Causa raiz:** El metodo `sendStatusOffEmbed` (async) encadenaba `clearChannel` → `sendEmbed` via
`.queue()`. Esto requiere 3+ round-trips REST (fetch history → delete each → send embed). Durante
`onDisable`, `discordManager.shutdown()` se ejecuta inmediatamente despues, cancelando la cadena
async antes del ultimo paso. El rename del canal (1 sola llamada REST) si alcanza a completarse.

**Fix aplicado:** `sendStatusOffEmbedSync` (via `.complete()`) en DiscordManager.java:318:
1. Captura los mensajes bot viejos ANTES de enviar
2. Envia el embed offline (bloqueante, garantizado)
3. Borra los mensajes viejos via `purgeMessages` (bulk, best-effort, no aborta si falla)
El guard en `onDisable` ahora incluye `isConnected()` como en `onEnable`.

## Deudas tecnicas conocidas

| # | Archivo | Problema | Prioridad |
|---|---------|----------|-----------|
| 1 | `DiscordManager.clearChannel:155` | `purgeMessages` (bulk) reemplaza delete individual, pero `purgeMessages` solo funciona con mensajes < 2 semanas | Baja |
| 2 | `ConsoleLogReader:55` | `readLine()` usa ISO-8859-1 (ASCII en la practica). Si hay texto no-ASCII en logs se corrompe | Baja |
| 3 | `ConfigAdapter.reloadAll:50` | Solo recarga FileConfiguration, no inyecta en `CounterManager`/`DiscordManager` | Media |
| 4 | `CounterManager.stopAll:47` | `LinkedHashMap` sin sync + clear mientras tarea async correa → race teorica | Baja |
| 5 | `PlayerListener.getAvatarUrl:45` | `getMemberById` sin guard `isConnected()` (menor: guild != null lo protege) | Baja |
| 6 | `PlayerListener.onPlayerJoin` | `hasPlayedBefore` se ejecuta async → race con `StatsListener` (primera vez). Actualmente la consulta usa `connector_player_stats` lo cual depende del orden de insercion | Baja |
| 7 | Sin multi-guild | `getServerId` asume un solo guild. No hay soporte para multiples servidores Discord | Baja |
| 8 | Sin cache de DB | Cada mensaje de chat consulta `getDiscordId` → DB query. OK hasta que sea lento | Baja |

## Folia — Scheduler Adaptado

La utilidad `Scheduler.java` detecta Folia via `Class.forName("...AsyncScheduler")` y delega
a `Bukkit.getAsyncScheduler()` en Folia (API Paper 1.21+). En Paper/Spigot usa `Bukkit.getScheduler()`.

Metodos estaticos disponibles:
- `Scheduler.runAsync(plugin, task)` → reemplaza `runTaskAsynchronously`
- `Scheduler.runLaterAsync(plugin, task, delayTicks)` → reemplaza `runTaskLaterAsynchronously`
- `Scheduler.runTimerAsync(plugin, task, delay, period)` → reemplaza `runTaskTimerAsynchronously`

El `BukkitTask` retornado soporta `.cancel()`. Los Folia `ScheduledTask` se wrappean via reflection
para evitar dependencia compile-time.

## Convenciones de Codigo

### Commits
- **Conventional Commits** estricto: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`
- Nunca hacer push directo a `master`
- Branches: `develop` → `feature/<nombre>`

### Build
- `./gradlew build` debe pasar sin errores
- `./gradlew shadowJar` para el JAR final
- Dependencies: HikariCP, PacketEvents, Redis (si aplica)

## Skills Obligatorios

### Siempre activos (nunca desactivar)
- **caveman**: Modo ultra-compreso. Ahorrar tokens siempre.
- **ponytail**: Laziest senior dev. Solo escribir lo necesario.

### Para Minecraft
- `minecraft-plugin-development`: Guia de desarrollo de plugins
- `minecraft-bukkit-pro`: Patrones Bukkit/Paper profesional

### Para Java
- `130-java-testing-strategies`: Estrategias de testing RIGHT-BICEP, A-TRIP, CORRECT

### Para Documentacion
- `documentation-writer`: Documentacion clara y concisa

### Para Code Review
- `code-reviewer`: Review sistematico antes de merge

### Para Seguridad
- `security-review`: Auditoria de vulnerabilidades

### Para TDD
- `tdd`: Test-driven development cuando aplique

## Plugins Activos

- **ponytail**: Siempre activo. Modo `full` por defecto.
  - `/ponytail ultra` solo cuando el codebase lo amerite
  - Nunca sobre-ingenierar
  - Reutilizar APIs existentes del server (TextDisplay, packets, etc.)

## Versions Target

- Minecraft: **1.21 - 26.1.2+**
- Java: **21 LTS**
- Paper API: **latest stable**
- Folia: **compatible** (via Scheduler utility)

## Reglas de Desarrollo

1. **YAGNI primero**: No crear algo que no se necesita ahora
2. **Reutilizar antes de crear**: Verificar si Paper/Spigot ya provee la funcionalidad
3. **Zero-lag**: Todo debe ser async cuando sea posible (usar `Scheduler.runAsync`)
4. **Zero-dependencies**: Evitar ProtocolLib si Paper API lo soporta
5. **Folia-ready**: Usar `Scheduler.runAsync/runTimerAsync` en vez de `Bukkit.getScheduler()` directo
6. **i18n**: Soporte multi-idioma desde el principio
7. **bStats**: Metricas anonimas integradas
8. **Release Please**: Automatizacion de releases con Conventional Commits
9. **No commit/push sin autorizacion**: Nunca hacer git commit ni git push a menos que el usuario lo pida explicitamente

## Testing

- Unit tests para logica de dominio
- Integration tests para interaccion con Paper API
- Mockear server events cuando sea necesario
- Cobertura minima: 80% en logica de negocio

## Documentacion

- README.md actualizado con cada feature
- Wiki en `docs-site/` (VitePress)
- Javadocs en servicios publicos
- Changelog automatico via Release Please

## Comandos Utiles

```bash
./gradlew build          # Build completo
./gradlew shadowJar      # Generar JAR
./gradlew test           # Ejecutar tests
./gradlew runServer      # Testing local
```
