# EligiusConnector — Proposal

## Summary

EligiusConnector es un plugin de Minecraft que conecta servidores de Discord con servidores de Minecraft. Permite sincronizar cuentas, controlar la consola, compartir chat, sincronizar roles, y gestionar eventos personalizados.

## Motivation

- **Problema:** Los admins necesitan administrar sus servidores de MC desde Discord de forma centralizada
- **Solución:** Un plugin profesional, modular, y extensible que unifica todas estas funcionalidades
- **Referencia:** DiscordSRV (mejorado, sin dependencias innecesarias, arquitectura hexagonal)

## Scope

### Incluido (MVP)
1. Sincronización de cuentas (SQLite/MySQL/Redis)
2. Chat compartido MC ↔ Discord
3. Filtros de chat con escalada
4. Control de consola remota
5. Sincronización de roles (bidireccional con LuckPerms)
6. Notificaciones básicas (join/leave/death)
7. Player queries (profile, inventory, location)
8. Auditoría
9. PlaceholderAPI integration

### Post-MVP
- Sistema de eventos personalizados
- Integración PlotProtectionStones
- Integración AdvancedJobs
- Multi-server Redis sync
- Web dashboard

## Architecture

- **Hexagonal Architecture** (Ports & Adapters)
- **Java 21 LTS**
- **Gradle Kotlin DSL**
- **Paper/Spigot/Bukkit/Folia/Purpur 1.21 - 26.1.2+**
- **PacketEvents** for packet interception
- **JDA** for Discord API
- **LuckPerms** for permissions
- **PlaceholderAPI** for placeholders

## Status

- [ ] Account Linking
- [ ] Chat Bridge
- [ ] Console Remote
- [ ] Role Sync
- [ ] Notifications
- [ ] Player Queries
- [ ] Custom Events
- [ ] Chat Filters
- [ ] Audit Log
- [ ] Security
- [ ] PlaceholderAPI
