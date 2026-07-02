---
title: Solução de Problemas e Erros Comuns
description: Soluções para erros e problemas comuns do EligiusConnector.
---

# Solução de Problemas e Erros Comuns

## 🔴 Problemas com Token do Bot

**Erro:** `Bot token not configured!`

Defina seu token do bot Discord em `config.yml`:
```yaml
bot:
  token: "SEU_TOKEN_AQUI"
```

**Erro:** `Failed to initialize Discord`

Verifique se:
- O token está correto (sem espaços extras)
- Os intents necessários estão ativados no Portal do Desenvolvedor Discord
- O bot foi convidado para seu servidor

## 🔴 Erros de Banco de Dados

**Erro:** `no such column: '...'`

Exclua `plugins/EligiusConnector/connector.db` e reinicie. O plugin recriará o banco de dados com o esquema correto. **Isso perderá os dados de contas vinculadas.**

## 🔴 Problemas de Verificação

**Erro:** `Invalid code or expired`

Os códigos expiram após 5 minutos. Gere um novo código clicando no botão **Verify** no Discord.

**Erro:** `Account already linked`

Sua conta Discord já está vinculada a uma conta Minecraft. Use `/unlink` primeiro.

## 🔴 Limites de Taxa

**Sintoma:** Contadores não atualizam, embeds de status não aparecem

O Discord limita a taxa de chamadas API. Se você reiniciar o servidor frequentemente para testes, aguarde 2-3 minutos entre reinicializações para os limites expirarem. Em produção com uptime normal, isso não é um problema.

## 🔴 Módulo Não Funciona

**Sintoma:** Um módulo (chat, verify, etc.) está desativado

Verifique:
1. O arquivo YAML do módulo existe em `plugins/EligiusConnector/`
2. O campo `channel:` tem um ID de canal Discord válido (não o placeholder `CHANNEL_ID`)
3. O módulo mostra `ENABLED` no log de inicialização do console

## 🔴 Botão de Economia Mostra "Não Disponível"

Instale **Vault** e **EssentialsX**. O botão de economia requer um plugin de economia compatível com Vault.

## 🔴 Placeholders Aparecem como Texto Literal

Instale **PlaceholderAPI**. Sem ele, `%player_name%`, `%server_online%` e placeholders similares não serão resolvidos.
