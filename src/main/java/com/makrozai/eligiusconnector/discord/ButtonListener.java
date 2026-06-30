package com.makrozai.eligiusconnector.discord;

import com.makrozai.eligiusconnector.EligiusConnector;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ButtonListener extends ListenerAdapter {

    private final EligiusConnector plugin;

    public ButtonListener(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String[] parts = event.getComponentId().split(":");
        String action = parts[0];

        switch (action) {
            case "verify" -> handleVerify(event);
            case "whereami" -> handleWhereAmI(event);
            case "inventory" -> handleInventory(event);
            case "money" -> handleMoney(event);
            case "profile" -> handleProfile(event);
            case "set_birthday" -> handleSetBirthday(event);
            case "birthday_input" -> handleBirthdayInput(event);
            default -> event.reply("Unknown action").setEphemeral(true).queue();
        }
    }

    private void handleVerify(ButtonInteractionEvent event) {
        CompletableFuture.runAsync(() -> {
            long discordId = Long.parseLong(event.getUser().getId());
            UUID uuid = plugin.getDatabaseManager().getMinecraftUuid(discordId);

            if (uuid != null) {
                event.reply(plugin.getConfigAdapter().getVerifyAlreadyLinked()).setEphemeral(true).queue();
                return;
            }

            String code = plugin.generateVerifyCode(discordId);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("🔐 Account Verification");
            embed.setDescription("To verify your account:");
            embed.setColor(Color.CYAN);
            embed.addField("Step 1", "Join the Minecraft server", false);
            embed.addField("Step 2", "Type `/verify` in chat", false);
            embed.addField("Step 3", "Enter this code: **" + code + "**", false);
            embed.setFooter("Code expires in " + plugin.getConfigAdapter().getCodeExpiryMinutes() + " minutes");
            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        });
    }

    private void handleWhereAmI(ButtonInteractionEvent event) {
        long discordId = Long.parseLong(event.getUser().getId());
        UUID uuid = plugin.getDatabaseManager().getMinecraftUuid(discordId);

        if (uuid == null) {
            event.reply("❌ Your account is not linked").setEphemeral(true).queue();
            return;
        }

        // Must access player on main thread
        Bukkit.getScheduler().runTask(plugin, () -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                event.reply("❌ You are not online").setEphemeral(true).queue();
                return;
            }

            Location loc = player.getLocation();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("📍 Your Location");
            embed.setColor(Color.GREEN);
            embed.addField("World", loc.getWorld().getName(), true);
            embed.addField("X", String.valueOf(loc.getBlockX()), true);
            embed.addField("Y", String.valueOf(loc.getBlockY()), true);
            embed.addField("Z", String.valueOf(loc.getBlockZ()), true);
            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        });
    }

    private void handleInventory(ButtonInteractionEvent event) {
        long discordId = Long.parseLong(event.getUser().getId());
        UUID uuid = plugin.getDatabaseManager().getMinecraftUuid(discordId);

        if (uuid == null) {
            event.reply("❌ Your account is not linked").setEphemeral(true).queue();
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                event.reply("❌ You are not online").setEphemeral(true).queue();
                return;
            }

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("🎒 Your Inventory");
            embed.setColor(Color.ORANGE);
            int count = 0;
            for (var item : player.getInventory().getContents()) {
                if (item != null && item.getType() != org.bukkit.Material.AIR) count++;
            }
            embed.setDescription("Items: **" + count + "**/" + player.getInventory().getContents().length);
            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        });
    }

    private void handleMoney(ButtonInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("💰 Balance");
        embed.setColor(Color.YELLOW);
        embed.setDescription("Balance: **$0.00**\nEconomy plugin not configured");
        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }

    private void handleProfile(ButtonInteractionEvent event) {
        long discordId = Long.parseLong(event.getUser().getId());
        UUID uuid = plugin.getDatabaseManager().getMinecraftUuid(discordId);

        Bukkit.getScheduler().runTask(plugin, () -> {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("👤 Profile");
            embed.setColor(Color.BLUE);

            if (uuid != null) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    embed.setThumbnail("https://minotar.net/avatar/" + player.getName() + "/128");
                    embed.addField("Name", player.getName(), true);
                    embed.addField("Health", String.valueOf((int) player.getHealth()), true);
                    embed.addField("Food", String.valueOf(player.getFoodLevel()), true);
                    embed.addField("Level", String.valueOf(player.getLevel()), true);
                    embed.addField("World", player.getWorld().getName(), true);
                } else {
                    embed.setDescription("Player is offline or account not linked");
                }
            } else {
                embed.setDescription("Player is offline or account not linked");
            }
            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        });
    }

    private void handleSetBirthday(ButtonInteractionEvent event) {
        CompletableFuture.runAsync(() -> {
            long discordId = Long.parseLong(event.getUser().getId());
            String existing = plugin.getDatabaseManager().getBirthday(discordId);

            if (existing != null && !existing.isEmpty()) {
                event.reply(plugin.getConfigAdapter().getBirthdayAlreadySet()).setEphemeral(true).queue();
                return;
            }

            event.reply("📅 Please enter your birthday (dd/MM/yyyy) using: `/birthday set dd/MM/yyyy`")
                    .setEphemeral(true)
                    .queue();
        });
    }

    private void handleBirthdayInput(ButtonInteractionEvent event) {
        event.reply("Please use `/birthday set dd/MM/yyyy` to set your birthday")
                .setEphemeral(true)
                .queue();
    }
}
