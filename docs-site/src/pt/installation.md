---
title: Instalação e Requisitos
description: Aprenda a instalar o EligiusConnector no seu servidor Minecraft.
---

# Instalação e Requisitos

O EligiusConnector é uma ponte Discord-Minecraft abrangente. Conecta-se ao Discord via JDA e integra-se opcionalmente com Vault, LuckPerms e PlaceholderAPI.

## 📋 Requisitos do Sistema

| Requisito | Versão Mínima | Motivo |
|-----------|---------------|--------|
| **Plataforma** | Bukkit, Spigot, Paper, Purpur ou Folia 1.21 - 26.1.2+ | Usa API Paper moderna e comandos Brigadier |
| **Java** | Java 21 LTS | Código fonte compilado no formato JDK 21 |

## 📦 Dependências

| Plugin | Obrigatório | Finalidade |
|--------|-------------|------------|
| **PlaceholderAPI** | Recomendado | Ativa `%placeholders%` em embeds e mensagens. Sem ele, placeholders aparecem como texto literal |
| **Vault** | Opcional | Saldo econômico no painel de perfil (botão `/money`) |
| **LuckPerms** | Opcional | Sincronização de cargos — grupos Minecraft ↔ cargos Discord |
| **EssentialsX** | Opcional | Fornece backend econômico para Vault |

## 🚀 Passos de Instalação

1. Baixe o `EligiusConnector-x.x.x.jar` mais recente da [página de Releases](https://github.com/Eligiusmc/EligiusConnector/releases)
2. Copie o JAR para a pasta `plugins/` do seu servidor
3. Instale os plugins recomendados: **PlaceholderAPI**, **Vault**, **LuckPerms**, **EssentialsX**
4. Inicie seu servidor — todos os arquivos de configuração serão gerados em `plugins/EligiusConnector/`
5. Edite `config.yml` e defina seu token do bot Discord
6. Reinicie o servidor ou execute `/connector reload`

![Console de Inicialização](/assets/readme.png)

## ⚙️ Configuração Inicial

Após o primeiro início, você verá o logo do gato Eligius MC no console com o status dos módulos. Todas as configurações YAML são geradas com padrões sensatos. A única alteração obrigatória é o token do bot Discord em `config.yml`:

```yaml
bot:
  token: "SEU_TOKEN_BOT_DISCORD"
  guild_id: "SEU_ID_SERVIDOR_DISCORD"
```

## 🔌 Configuração do Bot Discord

1. Acesse o [Portal do Desenvolvedor Discord](https://discord.com/developers/applications)
2. Crie uma nova aplicação → Adicione um bot
3. Ative **Message Content Intent**, **Server Members Intent** e **Guild Messages Intent**
4. Copie o token do bot e cole em `config.yml`
5. Convide o bot para seu servidor com os escopos `bot` e `applications.commands`
