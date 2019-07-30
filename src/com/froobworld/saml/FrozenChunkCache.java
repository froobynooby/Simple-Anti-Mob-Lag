package com.froobworld.saml;

import com.froobworld.saml.config.ConfigKeys;
import com.froobworld.saml.utils.ChunkCoordinates;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FrozenChunkCache {
    private Set<ChunkCoordinates> frozenChunkCoordinates;
    private File cacheFile;
    private boolean unsavedChanges;
    private boolean shouldSaveOnExit;

    public FrozenChunkCache(File cacheFile, Saml saml, boolean loadFromFile) {
        this.cacheFile = cacheFile;
        frozenChunkCoordinates = new HashSet<ChunkCoordinates>();
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
        YamlConfiguration config = new YamlConfiguration();
        List<String> serialisedChunkCoordinateList = new ArrayList<String>();

        for(ChunkCoordinates coords : frozenChunkCoordinates) {
            serialisedChunkCoordinateList.add(coords.toString());
        }

        config.set("frozen-chunks", serialisedChunkCoordinateList);
        try {
            if(!cacheFile.exists()) {
                cacheFile.createNewFile();
            }
            config.save(cacheFile);
            unsavedChanges = false;
        } catch (IOException e) {
            e.printStackTrace();
            Saml.logger().warning("There was a problem saving the frozen chunk cache.");
        }
    }
}
