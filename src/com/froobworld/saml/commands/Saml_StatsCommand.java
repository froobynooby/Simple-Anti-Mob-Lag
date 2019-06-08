package com.froobworld.saml.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Saml_StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cl, String[] args) {
        int totalMobCount = 0;
        int totalFrozenMobCount = 0;

        Map<World, Integer> worldMobCounts = new HashMap<World, Integer>();
        Map<World, Integer> worldFrozenMobCounts = new HashMap<World, Integer>();
        for(World world : Bukkit.getWorlds()) {
            int worldMobCount = 0;
            int worldFrozenMobCount = 0;
            for(LivingEntity livingEntity : world.getLivingEntities()) {
                if(livingEntity instanceof Player || livingEntity instanceof ArmorStand) {
                    continue;
                }
                worldMobCount++;
                if(!livingEntity.hasAI()) {
                    worldFrozenMobCount++;
                }
            }

            totalMobCount += worldMobCount;
            totalFrozenMobCount += worldFrozenMobCount;
            worldMobCounts.put(world, worldMobCount);
            worldFrozenMobCounts.put(world, worldFrozenMobCount);
        }

        sender.sendMessage(ChatColor.YELLOW + "---- " + ChatColor.WHITE + "SAML stats" + ChatColor.YELLOW + " ----");
        sender.sendMessage(ChatColor.YELLOW + "Server-wide: " + ChatColor.WHITE + totalFrozenMobCount + "/" + totalMobCount + " frozen");
        sender.sendMessage("");
        for(World world : Bukkit.getWorlds()) {
            sender.sendMessage(ChatColor.YELLOW + world.getName() + ": " + ChatColor.WHITE + worldFrozenMobCounts.get(world) + "/" + worldMobCounts.get(world) + " frozen");
        }
        return true;
    }
}
