package com.froobworld.saml;

import com.froobworld.saml.utils.ConfigUpdater;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;

public class AdvancedConfig {
    private static final int CURRENT_VERSION = 1;

    private Saml saml;
    private YamlConfiguration config;

    private boolean loaded;

    public AdvancedConfig(Saml saml) {
        this.saml = saml;
        loaded = false;
    }


    public synchronized void loadFromFile() {
        loaded = true;
        saml.getLogger().info("Loading advanced config...");
        File configFile = new File(saml.getDataFolder(), "advanced_config.yml");
        if(!configFile.exists()) {
            Saml.logger().info("Advanced config does not exist, copying default from jar...");
            try {
                saml.getDataFolder().mkdirs();
                Files.copy(saml.getResource("resources/advanced_config.yml"), configFile.toPath());
            } catch (IOException e) {
                Saml.logger().warning("There was a problem copying the advanced config:");
                config = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(saml.getResource("resources/advanced_config.yml"))));
                e.printStackTrace();
                Saml.logger().info("We may still be able to run...");
                return;
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        Saml.logger().info("Advanced config successfully loaded.");

        int version = config.getInt("version");
        if(version  > CURRENT_VERSION) {
            Saml.logger().warning("Your're using an advanced config for a higher version. This may lead to some issues.");
            Saml.logger().info("You can regenerate the advanced config by deleting or renaming your current one, and using the command 'saml reload'.");
        }
        if(version < CURRENT_VERSION) {
            Saml.logger().info("Your advanced config is out of date. Will attempt to perform upgrades...");
            for(int i = version; i < CURRENT_VERSION; i++) {
                if(ConfigUpdater.update(configFile, saml.getResource("resources/advanced-config-updates/" + i), i)) {
                    Saml.logger().info("Applied changes for advanced config version " + i + " to " + (i+1) + ".");
                } else {
                    Saml.logger().warning("Failed to apply changes for advanced config version " + i + " to " + (i+1) + ".");
                    return;
                }
            }
            Saml.logger().info("Advanced config successfully updated!");
            config = YamlConfiguration.loadConfiguration(configFile);
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public Boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    public Double getDouble(String key) {
        return config.getDouble(key);
    }

    public Long getLong(String key) {
        return config.getLong(key);
    }

    public List<String> getStringList(String key) {
        return config.getStringList(key);
    }
}
