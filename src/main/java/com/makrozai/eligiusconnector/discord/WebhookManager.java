package com.makrozai.eligiusconnector.discord;

import com.makrozai.eligiusconnector.EligiusConnector;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class WebhookManager {

    private final EligiusConnector plugin;
    private final OkHttpClient httpClient;
    private Webhook cachedWebhook;

    public WebhookManager(EligiusConnector plugin) {
        this.plugin = plugin;
        this.httpClient = new OkHttpClient();
    }

    public void sendWebhookMessage(String channelId, String content, String username, String avatarUrl) {
        CompletableFuture.runAsync(() -> {
            try {
                Webhook webhook = getOrCreateWebhook(channelId);
                if (webhook == null) {
                    fallbackToSendMessage(channelId, content, username);
                    return;
                }

                JSONObject payload = new JSONObject();
                payload.put("content", content);
                payload.put("username", username);
                payload.put("allowed_mentions", new JSONObject().put("parse", new org.json.JSONArray()));
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    payload.put("avatar_url", avatarUrl);
                }

                Request request = new Request.Builder()
                        .url(webhook.getUrl())
                        .post(RequestBody.create(payload.toString(), okhttp3.MediaType.parse("application/json")))
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        plugin.getLogger().warning("Webhook request failed: " + response.code());
                    }
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to send webhook: " + e.getMessage());
                fallbackToSendMessage(channelId, content, username);
            }
        });
    }

    private Webhook getOrCreateWebhook(String channelId) {
        String webhookName = plugin.getConfigAdapter().getWebhookName();
        if (cachedWebhook != null && cachedWebhook.getChannel().getId().equals(channelId)
                && cachedWebhook.getName().equals(webhookName)) {
            return cachedWebhook;
        }
        cachedWebhook = null;

        TextChannel channel = plugin.getDiscordManager().getChannel(channelId);
        if (channel == null) return null;

        // Look for existing webhook
        for (Webhook webhook : channel.getGuild().retrieveWebhooks().complete()) {
            if (webhook.getChannel().getId().equals(channelId) && webhook.getName().equals(webhookName)) {
                cachedWebhook = webhook;
                return webhook;
            }
        }

        // Create new webhook
        try {
            cachedWebhook = channel.createWebhook(webhookName).complete();
            return cachedWebhook;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to create webhook: " + e.getMessage());
            return null;
        }
    }

    private void fallbackToSendMessage(String channelId, String content, String username) {
        plugin.getDiscordManager().sendMessage(channelId, "**[" + username + "]** " + content);
    }

}
