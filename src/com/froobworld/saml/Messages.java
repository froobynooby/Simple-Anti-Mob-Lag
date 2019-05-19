package com.froobworld.saml;

import com.froobworld.saml.utils.ConfigUpdater;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

public class Messages {
    private static final int CURRENT_VERSION = 1;

    private Saml saml;
    private YamlConfiguration config;

    public Messages(Saml saml) {
        this.saml = saml;
    }


    public synchronized void loadFromFile() {
        saml.getLogger().info("Loading messages config...");
        File configFile = new File(saml.getDataFolder(), "messages.yml");
        if(!configFile.exists()) {
            Saml.logger().info("Messages file does not exist, copying default from jar...");
            try {
                saml.getDataFolder().mkdirs();
                Files.copy(saml.getResource("resources/messages.yml"), configFile.getAbsoluteFile().toPath());
            } catch (IOException e) {
                Saml.logger().warning("There was a problem copying the messages file:");
                config = YamlConfiguration.loadConfiguration(new InputStreamReader(saml.getResource("resources/messages.yml")));
                e.printStackTrace();
                Saml.logger().info("We may still be able to run...");
                return;
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        Saml.logger().info("Messages config successfully loaded.");

        int version = config.getInt("version");
        if(version  > CURRENT_VERSION) {
            Saml.logger().warning("Your're using a messages config for a higher version. This may lead to some issues.");
            Saml.logger().info("You can regenerate the messages config by deleting or renaming your current one, and using the command 'saml reload'.");
        }
        if(version < CURRENT_VERSION) {
            Saml.logger().info("Your messages config is out of date. Will attempt to perform upgrades...");
            for(int i = version; i < CURRENT_VERSION; i++) {
                if(ConfigUpdater.update(configFile, saml.getResource("resources/messages-updates/" + i), i)) {
                    Saml.logger().info("Applied changes for messages config version " + i + " to " + (i+1) + ".");
                } else {
                    Saml.logger().warning("Failed to apply changes for messages config version " + i + " to " + (i+1) + ".");
                    return;
                }
            }
            Saml.logger().info("Messages config successfully updated!");
            config = YamlConfiguration.loadConfiguration(configFile);
        }
    }

    public String getMessage(String key) {
        return config.getString(key);
    }
}
