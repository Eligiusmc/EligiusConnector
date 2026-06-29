# Spec: Documentation

## Overview

Documentación completa del plugin.

## Requirements

### Functional
1. Wiki VitePress multi-idioma (EN, ES)
2. README con badges
3. Javadocs
4. Changelog automático
5. Placeholder list

### Non-Functional
- Documentación actualizada con cada release
- SEO optimizado
- Multi-idioma

## Technical Design

### Directory Structure
```
docs-site/
├── .vitepress/
│   └── config.ts
├── en/
│   ├── index.md
│   ├── getting-started.md
│   ├── configuration.md
│   ├── commands.md
│   ├── permissions.md
│   └── placeholders.md
└── es/
    ├── index.md
    ├── getting-started.md
    ├── configuration.md
    ├── commands.md
    ├── permissions.md
    └── placeholders.md
```

### README Badges
```markdown
# EligiusConnector

[![License](https://img.shields.io/github/license/Eligiusmc/EligiusConnector)](https://github.com/Eligiusmc/EligiusConnector/blob/main/LICENSE)
[![Build](https://img.shields.io/github/actions/workflow/status/Eligiusmc/EligiusConnector/ci.yml?branch=main)](https://github.com/Eligiusmc/EligiusConnector/actions)
[![Release](https://img.shields.io/github/v/release/Eligiusmc/EligiusConnector)](https://github.com/Eligiusmc/EligiusConnector/releases)
[![Discord](https://img.shields.io/discord/123456789012345678?label=discord)](https://discord.gg/eligius)
[![Downloads](https://img.shields.io/modrinth/dt/eligiusconnector?label=modrinth)](https://modrinth.com/plugin/eligiusconnector)
[![Hangar](https://img.shields.io/hangar/dt/eligiusconnector?label=hangar)](https://hangar.papermc.io/eligius/EligiusConnector)
```

### Commands
- `./gradlew javadoc` — Generar Javadocs
- `npm run docs:dev` — Iniciar docs local
- `npm run docs:build` — Construir docs

## Testing
- Unit: Documentation validation
- Integration: Full docs build

## Acceptance Criteria
- [ ] Wiki completa
- [ ] README actualizado
- [ ] Javadocs generados
- [ ] Changelog automático
