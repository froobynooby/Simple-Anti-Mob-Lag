package com.froobworld.saml;

import com.froobworld.saml.commands.SamlCommand;
import com.froobworld.saml.listeners.EventListener;
import com.froobworld.saml.listeners.SamlListener;
import com.froobworld.saml.metrics.Metrics;
import com.froobworld.saml.tasks.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Saml extends JavaPlugin {
    private Config config;
    private AdvancedConfig advancedConfig;
    private Messages messages;
    private MobFreezeTask mobFreezeTask;

    private UnfreezeOnShutdownTask unfreezeOnShutdownTask;
    private HandleCacheOnShutdownTask handleCacheOnShutdownTask;

    @Override
    public void onEnable() {
        config = new Config(this);
        config.loadFromFile();
        advancedConfig = new AdvancedConfig(this);
        if(config.getBoolean("use-advanced-config")) {
            advancedConfig.loadFromFile();
        }
        messages = new Messages(this);
        messages.loadFromFile();

        registerCommands();
        registerListeners();
        addTasks();

        new Metrics(this);

        logger().info("Successfully enabled.");
    }

    private void addTasks() {
        new CheckCacheStartupTask(this);
        this.mobFreezeTask = new MobFreezeTask(this);
        new CacheSavingTask(this);

        this.unfreezeOnShutdownTask = new UnfreezeOnShutdownTask(this);
        this.handleCacheOnShutdownTask = new HandleCacheOnShutdownTask(this);
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

    public AdvancedConfig getAdvancedConfig() {
        return advancedConfig;
    }

    public Messages getSamlMessages() {
        return messages;
    }

    public MobFreezeTask getMobFreezeTask() {
        return mobFreezeTask;
    }

    @Override
    public void onDisable() {
        unfreezeOnShutdownTask.run();
        handleCacheOnShutdownTask.run();
        logger().info("Successfully disabled.");
    }

    public static Logger logger() {
        return Saml.getPlugin(Saml.class).getLogger();
    }

}
