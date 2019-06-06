package com.froobworld.saml;

import com.froobworld.saml.commands.SamlCommand;
import com.froobworld.saml.events.SamlConfigReloadEvent;
import com.froobworld.saml.listeners.EventListener;
import com.froobworld.saml.listeners.SamlListener;
import com.froobworld.saml.metrics.Metrics;
import com.froobworld.saml.tasks.*;
import com.froobworld.saml.utils.TpsSupplier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Saml extends JavaPlugin {
    private SamlConfiguration config;
    private SamlConfiguration advancedConfig;
    private SamlConfiguration messages;
    private MobFreezeTask mobFreezeTask;
    private TpsSupplier tpsSupplier;

    private UnfreezeOnShutdownTask unfreezeOnShutdownTask;
    private HandleCacheOnShutdownTask handleCacheOnShutdownTask;

    @Override
    public void onEnable() {
        config = new SamlConfiguration(this, SamlConfiguration.CONFIG_CURRENT_VERSION, "config.yml");
        config.loadFromFile();
        advancedConfig = new SamlConfiguration(this, SamlConfiguration.ADVANCED_CONFIG_CURRENT_VERSION, "advanced_config.yml");
        if(config.getBoolean("use-advanced-config")) {
            advancedConfig.loadFromFile();
        }
        messages = new SamlConfiguration(this, SamlConfiguration.MESSAGES_CURRENT_VERSION, "messages.yml");
        messages.loadFromFile();
        tpsSupplier = new TpsSupplier(this);

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

    public SamlConfiguration getSamlConfig() {
        return config;
    }

    public SamlConfiguration getAdvancedConfig() {
        return advancedConfig;
    }

    public SamlConfiguration getSamlMessages() {
        return messages;
    }

    public void reloadSamlConfiguration() {
        config.loadFromFile();
        if(advancedConfig.isLoaded()) {
            advancedConfig.loadFromFile();
        }
        messages.loadFromFile();
        Bukkit.getPluginManager().callEvent(new SamlConfigReloadEvent(config, advancedConfig, messages));
    }

    public MobFreezeTask getMobFreezeTask() {
        return mobFreezeTask;
    }

    public TpsSupplier getTpsSupplier() {
        return tpsSupplier;
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
