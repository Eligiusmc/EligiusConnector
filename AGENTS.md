# EligiusDiscord — Reglas del Proyecto

## Ecosistema Eligius

Este proyecto pertenece al ecosistema **Eligiusmc**. Seguir la misma arquitectura y convenciones que:
- [EligiusNametag](https://github.com/Eligiusmc/EligiusNametag)
- [EligiusHiddenArmor](https://github.com/Eligiusmc/EligiusHiddenArmor)

## Stack Obligatorio

- **Java 21 LTS** (sin excepciones)
- **Gradle Kotlin DSL** (`build.gradle.kts`, `settings.gradle.kts`)
- **Paper/Spigot API** 1.21 - 26.1.2+
- **Folia compatible** (thread-safe, async architecture)
- **Hexagonal Architecture** (puertos y adaptadores)

## Convenciones de Código

### Commits
- **Conventional Commits** estricto: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`
- Nunca hacer push directo a `master`
- Branches: `develop` → `feature/<nombre>`

### Arquitectura
- Separar dominio de infraestructura
- Puertos (interfaces) en el dominio
- Adaptadores en la capa de infraestructura
- Inyección de dependencias manual (sin frameworks pesados)

### Build
- `./gradlew build` debe pasar sin errores
- `./gradlew shadowJar` para el JAR final
- Dependencies: HikariCP, PacketEvents, Redis (si aplica)

## Skills Obligatorios

### Siempre activos (nunca desactivar)
- **caveman**: Modo ultra-compreso. Ahorrar tokens siempre.
- **ponytail**: Laziest senior dev. Solo escribir lo necesario.

### Para Minecraft
- `minecraft-plugin-development`: Guía de desarrollo de plugins
- `minecraft-bukkit-pro`: Patrones Bukkit/Paper profesional

### Para Java
- `130-java-testing-strategies`: Estrategias de testing RIGHT-BICEP, A-TRIP, CORRECT

### Para Documentación
- `documentation-writer`: Documentación clara y concisa

### Para Code Review
- `code-reviewer`: Review sistemático antes de merge

### Para Seguridad
- `security-review`: Auditoría de vulnerabilidades

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
- Folia: **compatible**

## Reglas de Desarrollo

1. **YAGNI primero**: No crear algo que no se necesita ahora
2. **Reutilizar antes de crear**: Verificar si Paper/Spigot ya provee la funcionalidad
3. **Zero-lag**: Todo debe ser async cuando sea posible
4. **Zero-dependencies**: Evitar ProtocolLib si Paper API lo soporta
5. **Folia-ready**: Thread-safe desde el inicio
6. **i18n**: Soporte multi-idioma desde el principio
7. **bStats**: Métricas anónimas integradas
8. **Release Please**: Automatización de releases con Conventional Commits
9. **No commit/push sin autorización**: Nunca hacer git commit ni git push a menos que el usuario lo pida explícitamente

## Testing

- Unit tests para lógica de dominio
- Integration tests para interacción con Paper API
- Mockear server events cuando sea necesario
- Cobertura mínima: 80% en lógica de negocio

## Documentación

- README.md actualizado con cada feature
- Wiki en `docs-site/` (VitePress)
- Javadocs en servicios públicos
- Changelog automático via Release Please

## Comandos Útiles

```bash
./gradlew build          # Build completo
./gradlew shadowJar      # Generar JAR
./gradlew test           # Ejecutar tests
./gradlew runServer      # Testing local
```
