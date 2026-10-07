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
├── discord/PanelManager.java    # Paneles de perfil/verify
├── discord/WebhookManager.java  # Webhooks para chat bridge (avatar+nombre)
├── discord/ConsoleLogReader.java # Lee logs del server → canal Discord
├── counters/                    # Sistema de contadores dinamicos (counters.yml)
│   ├── CounterManager.java      # Carga y gestiona periodic + state counters
│   ├── CounterConfig.java       # Record de config para cada counter
│   ├── StateManager.java        # Estado observable server_online
│   ├── StateCounter.java        # Renombre de canal reactivo a cambios de estado
│   └── PeriodicCounter.java     # Renombre de canal por intervalo (placeholders)
├── placeholders/PlaceholderResolver.java  # %placeholder% → valor (PAPI + built-ins)
├── listeners/PlayerListener.java  # Chat, join/leave, death, advancements → Discord
├── listeners/StatsListener.java   # Stats (kills, blocks, etc)
├── stats/PlayerStatsManager.java  # CRUD de estadisticas en DB
├── events/                       # Sistema de eventos custom (bosses, torneos)
├── commands/                     # /verify, /unlink, /connector, /events, /chat
├── tasks/NicknameSyncTask.java   # Sincroniza nicknames Minecraft ↔ Discord
└── util/
    ├── StartupLogger.java        # Logs colorizados de arranque
    └── Scheduler.java           # Adaptador Folia (detecta plataforma, usa API correcta)

src/main/resources/
├── config.yml          # Token, server_id, modulos, database, language
├── status.yml          # Embeds online/offline (canal compartido con counters.yml)
├── counters.yml        # Contadores: online, all_members, server_status (state)
├── chat.yml            # Config del bridge de chat
├── player_events.yml   # Embeds de join/leave/death/advancement
├── verify.yml          # Verificacion + sonidos + comandos post-verify
├── console.yml         # Log reader
├── synchronization.yml # Roles y nickname sync
├── profile.yml         # Embeds de perfil/inventario
├── events/*.yml        # Secuencias de comandos iniciadas manualmente
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

### Estado al apagar el servidor

El rename del contador de estado y el embed offline se completan sincrónicamente antes de cerrar JDA,
con timeout para el rename. El borrado bulk de mensajes antiguos es best-effort.

## Deudas tecnicas conocidas

| # | Archivo | Problema | Prioridad |
|---|---------|----------|-----------|
| 1 | `DiscordManager.clearChannel` | `purgeMessages` solo borra mensajes de menos de 2 semanas; status/paneles suelen ser recientes | Baja |
| 2 | `PlayerListener` | Sin cache de DB para vínculos de chat; cada mensaje consulta la base de datos | Baja |
| 3 | Configuración Discord | Un solo guild (`bot.guild_id`); multi-guild no se necesita hoy | Baja |

## Folia — Scheduler Adaptado

La utilidad `Scheduler.java` detecta Folia por el nombre del servidor y usa los schedulers global,
de entidad y async; en Paper/Spigot usa `Bukkit.getScheduler()`.

Metodos estaticos disponibles:
- `Scheduler.runSync(plugin, task)` → hilo global de Folia / main thread en Paper
- `Scheduler.runForEntity(plugin, entity, task)` → scheduler de entidad en Folia / main thread en Paper
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
- Dependencies: HikariCP, JDA, Adventure (MiniMessage), MySQL Connector/J

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

1. **🚫 NUNCA commit/push sin autorizacion explicita**: No ejecutar `git commit` ni `git push` a menos que el usuario lo solicite. Si el usuario dice "commit" o "haz commit", hacerlo. Si no lo pide, NUNCA.
2. **YAGNI primero**: No crear algo que no se necesita ahora
3. **Reutilizar antes de crear**: Verificar si Paper/Spigot ya provee la funcionalidad
4. **Zero-lag**: Todo debe ser async cuando sea posible (usar `Scheduler.runAsync`)
5. **Zero-dependencies**: Evitar ProtocolLib si Paper API lo soporta
6. **Folia-ready**: Usar `Scheduler.runAsync/runTimerAsync` en vez de `Bukkit.getScheduler()` directo
7. **i18n**: Soporte multi-idioma desde el principio
8. **Release Please**: Automatizacion de releases con Conventional Commits

## Deploy al Servidor de Pruebas

Al terminar un update y generar nuevo JAR, SIEMPRE hacer deploy limpio:

```bash
rm -f server/plugins/EligiusConnector-1.0.0.jar
rm -rf server/plugins/EligiusConnector
cp build/libs/EligiusConnector-1.0.0.jar server/plugins/
```

Esto elimina el JAR anterior y TODA la carpeta de configuracion generada, forzando
la regeneracion desde los resources del JAR. La DB se pierde tambien (server de pruebas).

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
