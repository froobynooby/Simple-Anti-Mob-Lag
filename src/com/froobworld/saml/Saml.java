package com.froobworld.saml;

import com.froobworld.saml.commands.SamlCommand;
import com.froobworld.saml.config.ConfigKeys;
import com.froobworld.saml.config.SamlConfiguration;
import com.froobworld.saml.events.SamlConfigReloadEvent;
import com.froobworld.saml.group.entity.EntityGroupStore;
import com.froobworld.saml.listeners.EventListener;
import com.froobworld.saml.listeners.SamlListener;
import com.froobworld.saml.metrics.Metrics;
import com.froobworld.saml.tasks.*;
import com.froobworld.saml.utils.TpsSupplier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class Saml extends JavaPlugin {
    private SamlConfiguration config;
    private SamlConfiguration advancedConfig;
    private SamlConfiguration customGroups;
    private SamlConfiguration messages;
    private TpsSupplier tpsSupplier;
    private EntityGroupStore groupStore;
    private FrozenChunkCache frozenChunkCache;

    private UnfreezeOnShutdownTask unfreezeOnShutdownTask;
    private HandleCacheOnShutdownTask handleCacheOnShutdownTask;

    @Override
    public void onEnable() {
        config = new SamlConfiguration(this, SamlConfiguration.CONFIG_CURRENT_VERSION, "config.yml");
        config.loadFromFile();
        advancedConfig = new SamlConfiguration(this, SamlConfiguration.ADVANCED_CONFIG_CURRENT_VERSION, "advanced_config.yml");
        if(config.getBoolean(ConfigKeys.CNF_USE_ADVANCED_CONFIG)) {
            advancedConfig.loadFromFile();
        }
        if(config.getBoolean(ConfigKeys.CNF_KEEP_FROZEN_CHUNK_CACHE)) {
            createFrozenChunkCacheIfNotExist();
        }
        customGroups = new SamlConfiguration(this, SamlConfiguration.CUSTOM_GROUPS_CURRENT_VERSION, "custom_groups.yml");
        customGroups.loadFromFile();
        messages = new SamlConfiguration(this, SamlConfiguration.MESSAGES_CURRENT_VERSION, "messages.yml");
        messages.loadFromFile();
        tpsSupplier = new TpsSupplier(this);
        groupStore = new EntityGroupStore(this);

        registerCommands();
        registerListeners();
        addTasks();

        new Metrics(this);

        logger().info("Successfully enabled.");
    }

    private void addTasks() {
        new CheckCacheStartupTask(this);
        new MainLoopTask(this);
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

    public SamlConfiguration getCustomGroups() {
        return customGroups;
    }

    public SamlConfiguration getSamlMessages() {
        return messages;
    }

    public EntityGroupStore getGroupStore() {
        return groupStore;
    }

    public void reloadSamlConfiguration() {
        config.loadFromFile();
        if(advancedConfig.isLoaded()) {
            advancedConfig.loadFromFile();
        }
        messages.loadFromFile();
        Bukkit.getPluginManager().callEvent(new SamlConfigReloadEvent(config, advancedConfig, messages));
    }

    public void reloadTpsSupplier() {
        tpsSupplier.cancelTasks();
        tpsSupplier = new TpsSupplier(this);
    }

    public TpsSupplier getTpsSupplier() {
        return tpsSupplier;
    }

    public FrozenChunkCache getFrozenChunkCache() {
        return frozenChunkCache;
    }

    public void createFrozenChunkCacheIfNotExist() {
        if(frozenChunkCache == null) {
            frozenChunkCache = new FrozenChunkCache(new File(getDataFolder(), ".chunk-cache"), this, false);
        }
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
