---
title: Troubleshooting & Common Errors
description: Solutions for common EligiusConnector errors and issues.
---

# Troubleshooting & Common Errors

## 🔴 Bot Token Issues

**Error:** `Bot token not configured!`

Set your Discord bot token in `config.yml`:
```yaml
bot:
  token: "YOUR_TOKEN_HERE"
```

**Error:** `Failed to initialize Discord`

Check that:
- The token is correct (no extra spaces)
- The bot has the required intents enabled in Discord Developer Portal
- The bot has been invited to your server

## 🔴 Database Errors

**Error:** `no such column: '...'`

Delete `plugins/EligiusConnector/connector.db` and restart. The plugin will recreate the database with the correct schema. **This will lose linked accounts data.**

## 🔴 Verification Issues

**Error:** `Invalid code or expired`

Codes expire after 5 minutes. Generate a new code by clicking the **Verify** button in Discord.

**Error:** `Account already linked`

Your Discord account is already linked to a Minecraft account. Use `/unlink` first.

## 🔴 Rate Limits

**Symptom:** Counters don't update, status embeds don't appear

Discord rate-limits API calls. If you're restarting the server frequently for testing, wait 2-3 minutes between restarts for rate limits to expire. In production with normal uptime, this is not an issue.

## 🔴 Module Not Working

**Symptom:** A module (chat, verify, etc.) is disabled

Check:
1. The module's YAML file exists in `plugins/EligiusConnector/`
2. The `channel:` field has a valid Discord channel ID (not `CHANNEL_ID` placeholder)
3. The module shows `ENABLED` in the console startup log

## 🔴 Economy Button Shows "Not Available"

Install **Vault** and **EssentialsX**. The economy button requires a Vault-compatible economy plugin.

## 🔴 Placeholders Show as Literal Text

Install **PlaceholderAPI**. Without it, `%player_name%`, `%server_online%`, and similar placeholders will not be resolved.
