package com.froobworld.saml.config;

import com.froobworld.saml.Saml;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SamlConfiguration {
    public static final int CONFIG_CURRENT_VERSION = 8;
    public static final int ADVANCED_CONFIG_CURRENT_VERSION = 2;
    public static final int CUSTOM_GROUPS_CURRENT_VERSION = 1;
    public static final int NERFER_GOALS_CURRENT_VERSION = 1;
    public static final int MESSAGES_CURRENT_VERSION = 2;

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
            Saml.logger().info("Couldn't find existing " + fileName + ", copying default from jar...");
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
        Saml.logger().info("Successfully loaded " + fileName + ".");

        if(config.contains(ConfigKeys.VERSION)) {
            int version = config.getInt(ConfigKeys.VERSION);
            if (version > currentVersion) {
                Saml.logger().warning("You're using a " + fileName + " for a higher version. This may lead to some issues.");
                Saml.logger().info("You may wish to regenerate this file by deleting it and reloading.");
            }
            if (version < currentVersion) {
                Saml.logger().info("Your " + fileName + " is out of date. Will attempt to perform upgrades...");
                for (int i = version; i < currentVersion; i++) {
                    if(!backup()) {
                        Saml.logger().warning("Failed to create a backup for " + fileName + ", will proceed anyway.");
                    }
                    InputStream patchInputStream = saml.getResource("resources/" + fileNameDashed + "-updates/" + i);
                    if(patchInputStream != null) {
                        if (ConfigUpdater.update(configFile, patchInputStream, i)) {
                            Saml.logger().info("Applied changes for " + fileName + " version " + i + " to " + (i + 1) + ".");
                        } else {
                            Saml.logger().warning("Failed to apply changes for " + fileName + " version " + i + " to " + (i + 1) + ".");
                            return;
                        }
                    } else {
                        Saml.logger().info("Regenerating " + fileName + " to the latest version...");
                        saml.getDataFolder().mkdirs();
                        try {
                            Files.copy(saml.getResource("resources/" + fileName), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            Saml.logger().info("Successfully regenerated " + fileName + ".");
                        } catch (IOException e) {
                            Saml.logger().warning("There was a problem regenerating " + fileName + ":");
                            e.printStackTrace();
                            return;
                        }
                    }
                }
                Saml.logger().info("Successfully updated " + fileName + "!");
                config = YamlConfiguration.loadConfiguration(configFile);
            }
        } else {
            Saml.logger().warning("Your " + fileName + " either hasn't loaded properly or is not versioned. This may lead to problems.");
        }
    }

    private boolean backup() {
        File configFile = new File(saml.getDataFolder(), fileName);
        int version = config.getInt(ConfigKeys.VERSION);
        String backupDirPath = configFile.getParent() + File.separator + fileNameDashed + "-backups";
        try {
            new File(backupDirPath).mkdirs();
            Files.copy(configFile.toPath(), new File(backupDirPath, configFile.getName() + "." + version + ".bak").toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean keyExists(String key) {
        return config.contains(key);
    }

    public String getString(String key) {
        return config.getString(key);
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

    public Map<String, Object> getSection(String key) {
        ConfigurationSection section = config.getConfigurationSection(key);
        return section != null ? section.getValues(true) : Collections.emptyMap();
    }

}
