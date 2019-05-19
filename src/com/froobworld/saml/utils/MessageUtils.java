package com.froobworld.saml.utils;

import com.froobworld.saml.Config;
import com.froobworld.saml.Saml;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtils {

    public static void broadcastToOps(String message, Config config) {
        if(!config.getBoolean("broadcast-to-ops")) {
            return;
        }
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission("saml.notify")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

    public static void broadcastToConsole(String message, Config config) {
        if(!config.getBoolean("broadcast-to-console")) {
            return;
        }
        Saml.logger().info(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void broadcastToOpsAndConsole(String message, Config config) {
        broadcastToOps(message, config);
        broadcastToConsole(message, config);
    }
}
