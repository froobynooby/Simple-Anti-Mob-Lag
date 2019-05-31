package com.froobworld.saml;

import com.froobworld.saml.utils.ConfigUpdater;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;

public class SamlConfiguration {
    public static final int CONFIG_CURRENT_VERSION = 5;
    public static final int ADVANCED_CONFIG_CURRENT_VERSION = 1;
    public static final int MESSAGES_CURRENT_VERSION = 1;

    private Saml saml;
    private int currentVersion;
    private String fileName;
    private String fileNameDashed;
    private YamlConfiguration config;
    private boolean loaded;

    public SamlConfiguration(Saml saml, int currentVersion, String fileName) {
        this.saml = saml;
        this.currentVersion = currentVersion;
        this.fileName = fileName;
        this.fileNameDashed = fileName.replaceAll("\\.", "-");
        this.loaded = false;
    }


    public synchronized void loadFromFile() {
        loaded = true;
        saml.getLogger().info("Loading " + fileName + "...");
        File configFile = new File(saml.getDataFolder(), fileName);
        if(!configFile.exists()) {
            Saml.logger().info(fileName + " does not exist, copying default from jar...");
            try {
                saml.getDataFolder().mkdirs();
                Files.copy(saml.getResource("resources/" + fileName), configFile.toPath());
            } catch (IOException e) {
                Saml.logger().warning("There was a problem copying the default " + fileName + ":");
                config = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(saml.getResource("resources/" + fileName))));
                e.printStackTrace();
                Saml.logger().info("We may still be able to run...");
                return;
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        Saml.logger().info(fileName + " successfully loaded.");

        int version = config.getInt("version");
        if(version  > currentVersion) {
            Saml.logger().warning("Your're using a " + fileName + " for a higher version. This may lead to some issues.");
            Saml.logger().info("You may wish to regenerate this file by deleting it and reloading.");
        }
        if(version < currentVersion) {
            Saml.logger().info("Your " + fileName + " is out of date. Will attempt to perform upgrades...");
            for(int i = version; i < currentVersion; i++) {
                if(ConfigUpdater.update(configFile, saml.getResource("resources/" + fileNameDashed +"-updates/" + i), i)) {
                    Saml.logger().info("Applied changes for " + fileName + " version " + i + " to " + (i+1) + ".");
                } else {
                    Saml.logger().warning("Failed to apply changes for " + fileName + " version " + i + " to " + (i+1) + ".");
                    return;
                }
            }
            Saml.logger().info(fileName + " successfully updated!");
            config = YamlConfiguration.loadConfiguration(configFile);
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean keyExists(String key) {
        return config.contains(key);
    }

    public String getString(String key) {
        return config.getString("key");
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
