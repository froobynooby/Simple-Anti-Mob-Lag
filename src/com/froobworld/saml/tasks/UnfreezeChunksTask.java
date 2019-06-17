package com.froobworld.saml.tasks;

import com.froobworld.saml.FrozenChunkCache;
import com.froobworld.saml.Saml;
import com.froobworld.saml.utils.ChunkCoordinates;
import com.froobworld.saml.utils.CompatibilityUtils;
import com.froobworld.saml.utils.UnfreezeChunkConsumer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class UnfreezeChunksTask implements Runnable {
    private List<FrozenChunkCache> frozenChunkCaches;
    private FrozenChunkCache currentCache;
    private int place;
    private int lastCompleted;
    private Saml saml;
    private boolean paper;

    public UnfreezeChunksTask(List<FrozenChunkCache> frozenChunkCaches, Saml saml) {
        this.frozenChunkCaches = frozenChunkCaches;
        this.saml = saml;
        start();
    }

    private void start() {
        if(saml.getSamlConfig().getBoolean("use-paper-get-chunk-async")) {
            if(CompatibilityUtils.USE_PAPER_GET_CHUNK_ASYNC) {
                paper = true;
                List<FrozenChunkCache> cacheCopy = new ArrayList<FrozenChunkCache>(frozenChunkCaches);
                for(FrozenChunkCache frozenChunkCache : cacheCopy) {
                    List<ChunkCoordinates> coordsCopy = new ArrayList<ChunkCoordinates>(frozenChunkCache.getFrozenChunkCoordinates());
                    coordsCopy.forEach(c -> c.getWorld().getChunkAtAsync(
                            c.getX(), c.getZ(), false, new UnfreezeChunkConsumer(saml, frozenChunkCache)
                    ));
                }
            } else {
                paper = false;
                Saml.logger().warning("You elected to use Paper's async chunk fetcher, but this is not supported on your version.");
                Saml.logger().info("We will use the regular method instead.");
            }
        } else {
            paper = false;
        }
        if(!paper) {
            if(!frozenChunkCaches.isEmpty()) {
                currentCache = frozenChunkCaches.get(0);
                place = 0;
            }
        }
        lastCompleted = 0;
        run();
    }

    @Override
    public void run() {
        if(frozenChunkCaches.stream().allMatch( c -> c.getFrozenChunkCoordinates().isEmpty() )) {
            Saml.logger().info("We have finished unfreezing the previously frozen mobs.");
            frozenChunkCaches.forEach( f -> f.deleteCacheFile() );
            return;
        }
        if(!paper) {
            if(currentCache.getFrozenChunkCoordinates().isEmpty()) {
                place++;
                currentCache = frozenChunkCaches.get(place);

            }
            new UnfreezeChunkConsumer(saml, currentCache).accept(currentCache.getFrozenChunkCoordinates().iterator().next().toChunk());
        }
        int completed = (int) frozenChunkCaches.stream().filter(f -> f.getFrozenChunkCoordinates().isEmpty() ).count();
        if(completed > lastCompleted) {
            lastCompleted = completed;
            Saml.logger().info("We have unfrozen " + completed + " of " + frozenChunkCaches.size() + " of the old frozen chunk caches.");
        }
        long ticksPerCachedChunkUnfreeze = saml.getSamlConfig().getLong("ticks-per-cached-chunk-unfreeze");
        Bukkit.getScheduler().scheduleSyncDelayedTask(saml, this, ticksPerCachedChunkUnfreeze <= 0 ? 40:ticksPerCachedChunkUnfreeze);
    }
}
