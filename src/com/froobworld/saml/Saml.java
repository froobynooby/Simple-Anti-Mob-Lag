package com.froobworld.saml;

import com.froobworld.saml.commands.SamlCommand;
import com.froobworld.saml.listeners.EventListener;
import com.froobworld.saml.tasks.MobFreezeTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Saml extends JavaPlugin {
    private Config config;
    private Messages messages;

    @Override
    public void onEnable() {
        config = new Config(this);
        config.loadFromFile();
        messages = new Messages(this);
        messages.loadFromFile();

        registerCommands();
        registerListeners();
        addTasks();

        logger().info("Successfully enabled.");
    }

    private void addTasks() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new MobFreezeTask(this, config, messages));
    }

    private void registerCommands() {
        getCommand("saml").setExecutor(new SamlCommand(this));
        getCommand("saml").setPermission("saml.saml");
        getCommand("saml").setPermissionMessage(ChatColor.RED + "You don't have permission to use this command.");
        getCommand("saml").setTabCompleter(SamlCommand.tabCompleter);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new EventListener(config), this);
    }

    public Config getLwalConfig() {
        return config;
    }

    public Messages getLwalMessages() {
        return messages;
    }

    @Override
    public void onDisable() {
        if(config.getBoolean("unfreeze-on-shutdown")) {
            for(World world : Bukkit.getWorlds()) {
                for(LivingEntity entity : world.getLivingEntities()) {
                    if(!entity.hasAI()) {
                        entity.setAI(true);
                    }
                }
            }
        }

        logger().info("Successfully disabled.");
    }

    public static Logger logger() {
        return Saml.getPlugin(Saml.class).getLogger();
    }

}
