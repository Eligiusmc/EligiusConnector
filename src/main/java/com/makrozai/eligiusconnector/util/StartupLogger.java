package com.makrozai.eligiusconnector.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class StartupLogger {

    private static final String PREFIX = "[EligiusConnector] ";

    public static void printLogo(String version, String platform, String storage) {
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "------------------------------------------------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "  ELIGIUS MC" + ChatColor.RESET + ChatColor.GRAY + "                          " + ChatColor.GREEN + "EligiusConnector v" + version);
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "  /\\_/\\  " + ChatColor.RESET + ChatColor.WHITE + platform + " | Storage: " + storage);
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "  ( o.o ) " + ChatColor.GRAY + "Discord-Minecraft Connector");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "  > ^ <  " + ChatColor.DARK_GRAY + "GitHub: https://github.com/Eligiusmc");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "------------------------------------------------------------");
    }

    public static void printStep(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + PREFIX + ChatColor.WHITE + message);
    }

    public static void printStep(String message, String detail) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + PREFIX + ChatColor.WHITE + message + " " + ChatColor.AQUA + "[" + detail + "]");
    }

    public static void printSuccess(long timeMs) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + PREFIX + "Successfully enabled. " + ChatColor.GRAY + "(took " + timeMs + "ms)");
    }

    public static void printError(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + PREFIX + "[ERROR] " + message);
    }

    public static void printWarning(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + PREFIX + "[WARN] " + message);
    }

    public static void printInfo(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + PREFIX + message);
    }
}
