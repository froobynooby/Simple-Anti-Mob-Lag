package com.froobworld.saml;

import com.froobworld.saml.utils.ConfigUpdater;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;

public class Config {
    private static final int CURRENT_VERSION = 2;

    private Saml saml;
    private YamlConfiguration config;

    public Config(Saml saml) {
        this.saml = saml;
    }


    public synchronized void loadFromFile() {
        saml.getLogger().info("Loading config...");
        File configFile = new File(saml.getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            Saml.logger().info("Config does not exist, copying default from jar...");
            try {
                saml.getDataFolder().mkdirs();
                Files.copy(saml.getResource("resources/config.yml"), configFile.toPath());
            } catch (IOException e) {
                Saml.logger().warning("There was a problem copying the config:");
                config = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(saml.getResource("resources/config.yml"))));
                e.printStackTrace();
                Saml.logger().warning("We may still be able to run...");
                return;
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        Saml.logger().warning("Config successfully loaded.");

        int version = config.getInt("version");
        if(version  > CURRENT_VERSION) {
            Saml.logger().warning("Your're using a config for a higher version. This may lead to some issues.");
            Saml.logger().info("You can regenerate the config by deleting or renaming your current one, and using the command 'saml reload'.");
        }
        if(version < CURRENT_VERSION) {
            Saml.logger().info("Your config is out of date. Will attempt to perform upgrades...");
            for(int i = version; i < CURRENT_VERSION; i++) {
                if(ConfigUpdater.update(configFile, saml.getResource("resources/config-updates/" + i), i)) {
                    Saml.logger().info("Applied changes for config version " + i + " to " + (i+1) + ".");
                } else {
                    Saml.logger().warning("Failed to apply changes for config version " + i + " to " + (i+1) + ".");
                    return;
                }
            }
            Saml.logger().info("Config successfully updated!");
            config = YamlConfiguration.loadConfiguration(configFile);
        }
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
