package com.froobworld.saml;

import com.froobworld.saml.config.ConfigKeys;
import com.froobworld.saml.utils.ChunkCoordinates;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FrozenChunkCache {
    private Set<ChunkCoordinates> frozenChunkCoordinates;
    private File cacheFile;
    private boolean unsavedChanges;
    private boolean shouldSaveOnExit;

    public FrozenChunkCache(File cacheFile, Saml saml, boolean loadFromFile) {
        this.cacheFile = cacheFile;
        frozenChunkCoordinates = new HashSet<>();
        if(loadFromFile) {
            loadFromFile();
        }
        unsavedChanges = false;
        shouldSaveOnExit = !saml.getSamlConfig().getBoolean(ConfigKeys.CNF_UNFREEZE_ON_SHUTDOWN) || !saml.getSamlConfig().getBoolean(ConfigKeys.CNF_UNFREEZE_ON_UNLOAD);
    }


    public boolean shouldSaveOnExit() {
        return shouldSaveOnExit;
    }

    public void setShouldSaveOnExit() {
        shouldSaveOnExit = true;
    }

    private void loadFromFile() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(cacheFile);
        for(String string : config.getStringList("frozen-chunks")) {
            frozenChunkCoordinates.add(ChunkCoordinates.fromString(string));
        }
    }

    public void addChunk(Location location) {
        frozenChunkCoordinates.add(new ChunkCoordinates(location.getWorld().getUID(),location.getBlockX() >> 4, location.getBlockZ() >> 4));
        unsavedChanges = true;
    }

    public void removeChunk(Chunk chunk) {
        frozenChunkCoordinates.remove(new ChunkCoordinates(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ()));
        unsavedChanges = true;
    }

    public void deleteCacheFile() {
        cacheFile.delete();
    }

    public Set<ChunkCoordinates> getFrozenChunkCoordinates() {
        return frozenChunkCoordinates;
    }

    public boolean hasUnsavedChanges() {
        return unsavedChanges;
    }

    public void saveToFile() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for(ChunkCoordinates coords : frozenChunkCoordinates) {
            jsonArray.add(coords.toString());
        }
        jsonObject.add("frozen-chunks", jsonArray);
        try {
            if(!cacheFile.exists()) {
                cacheFile.createNewFile();
            }
            try (FileWriter writer = new FileWriter(cacheFile)) {
                writer.append(jsonObject.toString());
            } catch(IOException e) {
                e.printStackTrace();
                Saml.logger().warning("There was a problem writing to the frozen chunk cache file.");
            }
            unsavedChanges = false;
        } catch (IOException e) {
            e.printStackTrace();
            Saml.logger().warning("There was a problem creating the frozen chunk cache file.");
        }
    }
}
