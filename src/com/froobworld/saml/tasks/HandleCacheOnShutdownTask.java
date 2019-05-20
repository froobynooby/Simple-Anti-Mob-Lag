package com.froobworld.saml.tasks;

import com.froobworld.saml.FrozenChunkCache;
import com.froobworld.saml.Saml;

public class HandleCacheOnShutdownTask implements Runnable {
    private Saml saml;

    public HandleCacheOnShutdownTask(Saml saml) {
        this.saml = saml;
    }

    @Override
    public void run() {
        FrozenChunkCache frozenChunkCache = saml.getMobFreezeTask().getFrozenChunkCache();
        if(frozenChunkCache != null) {
            if(frozenChunkCache.shouldSaveOnExit()) {
                frozenChunkCache.saveToFile();
                Saml.logger().info("The frozen chunk cache file has been saved.");
            } else {
                frozenChunkCache.deleteCacheFile();
                Saml.logger().info("The frozen chunk cache file has been deleted. All chunks should have been accounted for.");
            }
        }
    }
}
