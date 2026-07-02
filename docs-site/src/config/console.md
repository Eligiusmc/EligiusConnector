---
title: Console Relay Configuration (console.yml)
description: Stream server logs to Discord and execute commands from a console channel.
---

# console.yml

Two-way console bridge: server logs are streamed to a Discord channel, and messages typed in that channel are executed as server commands (if not blacklisted).

## channel

```yaml
channel: "1521272226442383440"
```

Discord channel ID. All server logs are streamed here. Messages sent by users in this channel are executed as Minecraft server commands.

## poll_interval

```yaml
poll_interval: 2
```

How often (in seconds) the plugin reads new lines from `logs/latest.log`. Lower values give more real-time output at the cost of slightly higher CPU usage.

## command_blacklist

```yaml
command_blacklist:
  - "stop"
  - "restart"
  - "reload"
  - "ban"
  - "ban-ip"
  - "pardon"
  - "pardon-ip"
  - "op"
  - "deop"
  - "say"
  - "gamemode"
  - "whitelist"
  - "kick"
  - "plugins"
```

Commands in this list cannot be executed from Discord. The check is prefix-based — e.g. blocking `ban` also blocks `ban-ip` and `banlist`, but the explicit `ban-ip` entry ensures clarity. Dangerous administrative commands (stop, restart, op, deop) are blacklisted by default.

## Formatting

Log output in Discord uses ` ```ansi ` code blocks to preserve ANSI color codes from the server console, giving a native console-like appearance.
