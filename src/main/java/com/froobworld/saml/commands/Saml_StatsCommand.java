package com.froobworld.saml.commands;

import com.froobworld.saml.utils.EntityFreezer;
import com.froobworld.saml.utils.EntityNerfer;
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
        int totalAffectedMobCount = 0;
        int totalFrozenMobCount = 0;
        int totalNerfedMobCount = 0;

        Map<World, Integer> worldMobCounts = new HashMap<>();
        Map<World, Integer> worldAffectedMobCounts = new HashMap<>();
        Map<World, Integer> worldFrozenMobCounts = new HashMap<>();
        Map<World, Integer> worldNerfedMobCounts = new HashMap<>();
        for(World world : Bukkit.getWorlds()) {
            int worldMobCount = 0;
            int worldAffectedMobCount = 0;
            int worldFrozenMobCount = 0;
            int worldNerfedMobCount = 0;
            for(LivingEntity livingEntity : world.getLivingEntities()) {
                if(livingEntity instanceof Player || livingEntity instanceof ArmorStand) {
                    continue;
                }
                worldMobCount++;
                boolean affected = false;
                if(EntityFreezer.isFrozen(livingEntity)) {
                    worldFrozenMobCount++;
                    affected = true;
                }
                if(EntityNerfer.isNerfed(livingEntity)) {
                    worldNerfedMobCount++;
                    affected = true;
                }
                if(affected) {
                    worldAffectedMobCount++;
                }
            }

            totalMobCount += worldMobCount;
            totalAffectedMobCount += worldAffectedMobCount;
            totalFrozenMobCount += worldFrozenMobCount;
            totalNerfedMobCount += worldNerfedMobCount;
            worldMobCounts.put(world, worldMobCount);
            worldAffectedMobCounts.put(world, worldAffectedMobCount);
            worldFrozenMobCounts.put(world, worldFrozenMobCount);
            worldNerfedMobCounts.put(world, worldNerfedMobCount);
        }

        sender.sendMessage(ChatColor.YELLOW + "---- " + ChatColor.WHITE + "SAML stats" + ChatColor.YELLOW + " ----");
        sender.sendMessage(ChatColor.YELLOW + "Server-wide: " + ChatColor.WHITE + totalAffectedMobCount + "/" + totalMobCount + " affected (" + totalFrozenMobCount + " frozen, " + totalNerfedMobCount + " nerfed)");
        sender.sendMessage("");
        for(World world : Bukkit.getWorlds()) {
            sender.sendMessage(ChatColor.YELLOW + world.getName() + ": " + ChatColor.WHITE + worldAffectedMobCounts.get(world) + "/" + worldMobCounts.get(world) + " affected (" + worldFrozenMobCounts.get(world) + " frozen, " + worldNerfedMobCounts.get(world) + " nerfed)");
        }
        return true;
    }
}
