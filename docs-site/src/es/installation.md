---
title: Instalación y Requisitos
description: Aprende a instalar EligiusConnector en tu servidor Minecraft.
---

# Instalación y Requisitos

EligiusConnector es un bridge completo Discord-Minecraft. Se conecta a Discord vía JDA y opcionalmente se integra con Vault, LuckPerms y PlaceholderAPI.

## 📋 Requisitos del Sistema

| Requisito | Versión Mínima | Razón |
|-----------|----------------|-------|
| **Plataforma** | Bukkit, Spigot, Paper, Purpur o Folia 1.21 - 26.1.2+ | Usa la API moderna de Paper y comandos Brigadier |
| **Java** | Java 21 LTS | Código fuente compilado en formato JDK 21 |

## 📦 Dependencias

| Plugin | Requerido | Propósito |
|--------|-----------|-----------|
| **PlaceholderAPI** | Recomendado | Habilita `%placeholders%` en embeds y mensajes. Sin él, los placeholders se muestran como texto literal |
| **Vault** | Opcional | Saldo de economía en el panel de perfil (botón `/money`) |
| **LuckPerms** | Opcional | Sincronización de roles — grupos de Minecraft ↔ roles de Discord |
| **EssentialsX** | Opcional | Provee el backend de economía para Vault |

## 🚀 Pasos de Instalación

1. Descarga el último `EligiusConnector-x.x.x.jar` de la [página de Releases](https://github.com/Eligiusmc/EligiusConnector/releases)
2. Copia el JAR en la carpeta `plugins/` de tu servidor
3. Instala los plugins recomendados: **PlaceholderAPI**, **Vault**, **LuckPerms**, **EssentialsX**
4. Inicia tu servidor — todos los archivos de configuración se generarán en `plugins/EligiusConnector/`
5. Edita `config.yml` y configura el token de tu bot de Discord
6. Reinicia el servidor o ejecuta `/connector reload`

![Consola de Inicio](/assets/readme.png)

## ⚙️ Configuración Inicial

Después del primer inicio, verás el logo del gato Eligius MC en la consola con el estado de los módulos. Todos los YAML se generan con valores predeterminados sensatos. El único cambio obligatorio es el token del bot de Discord en `config.yml`:

```yaml
bot:
  token: "TU_TOKEN_DEL_BOT_DE_DISCORD"
  guild_id: "ID_DE_TU_SERVIDOR_DE_DISCORD"
```

## 🔌 Configuración del Bot de Discord

1. Ve al [Portal de Desarrolladores de Discord](https://discord.com/developers/applications)
2. Crea una nueva aplicación → Agrega un bot
3. Activa **Message Content Intent**, **Server Members Intent** y **Guild Messages Intent**
4. Copia el token del bot y pégalo en `config.yml`
5. Invita al bot a tu servidor con los scopes `bot` y `applications.commands`
