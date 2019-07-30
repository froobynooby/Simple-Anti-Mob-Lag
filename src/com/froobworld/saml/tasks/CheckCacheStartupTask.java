package com.froobworld.saml.tasks;

import com.froobworld.saml.FrozenChunkCache;
import com.froobworld.saml.Saml;
import com.froobworld.saml.config.ConfigKeys;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CheckCacheStartupTask implements Runnable {
    private Saml saml;

    public CheckCacheStartupTask(Saml saml) {
        this.saml = saml;
        run();
    }

    @Override
    public void run() {
        File chunkCacheDirectory = new File(saml.getDataFolder(), "frozen-chunks");

        File chunkCacheFile = new File(saml.getDataFolder(), ".chunk-cache");
        if(chunkCacheFile.exists()) {
            if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_UNFREEZE_ON_SHUTDOWN) && saml.getSamlConfig().getBoolean(ConfigKeys.CNF_UNFREEZE_ON_UNLOAD)) {
                Saml.logger().info("There is an old chunk cache file. Perhaps the server didn't shut down correctly?");
            }
            if(!chunkCacheDirectory.exists()) {
                chunkCacheDirectory.mkdirs();
            }
            FileUtil.copy(chunkCacheFile, new File(chunkCacheDirectory, System.currentTimeMillis() + ""));
            chunkCacheFile.delete();
        }
        if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_UNFREEZE_CACHED_CHUNKS_ON_STARTUP)) {
            if (chunkCacheDirectory.exists()) {
                if(chunkCacheDirectory.listFiles().length > 0) {
                    Saml.logger().info("We will now start unfreezing chunks from the old cache files.");
                    List<FrozenChunkCache> frozenChunkCaches = new ArrayList<FrozenChunkCache>();
                    for (File file : chunkCacheDirectory.listFiles()) {
                        frozenChunkCaches.add(new FrozenChunkCache(file, saml, true));
                    }
                    new UnfreezeChunksTask(frozenChunkCaches, saml);
                }
            }
        }
    }
}
