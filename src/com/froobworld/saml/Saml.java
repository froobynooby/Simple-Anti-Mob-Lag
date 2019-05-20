package com.froobworld.saml;

import com.froobworld.saml.commands.SamlCommand;
import com.froobworld.saml.listeners.EventListener;
import com.froobworld.saml.listeners.SamlListener;
import com.froobworld.saml.tasks.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Saml extends JavaPlugin {
    private Config config;
    private Messages messages;
    private MobFreezeTask mobFreezeTask;

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
        new CheckCacheStartupTask(this);
        this.mobFreezeTask = new MobFreezeTask(this);
        new CacheSavingTask(this);
    }

    private void registerCommands() {
        getCommand("saml").setExecutor(new SamlCommand(this));
        getCommand("saml").setPermission("saml.saml");
        getCommand("saml").setPermissionMessage(ChatColor.RED + "You don't have permission to use this command.");
        getCommand("saml").setTabCompleter(SamlCommand.tabCompleter);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SamlListener(this), this);
    }

    public Config getSamlConfig() {
        return config;
    }

    public Messages getSamlMessages() {
        return messages;
    }

    public MobFreezeTask getMobFreezeTask() {
        return mobFreezeTask;
    }

    @Override
    public void onDisable() {
        new UnfreezeOnShutdownTask(this).run();
        new HandleCacheOnShutdownTask(this).run();
        logger().info("Successfully disabled.");
    }

    public static Logger logger() {
        return Saml.getPlugin(Saml.class).getLogger();
    }

}
