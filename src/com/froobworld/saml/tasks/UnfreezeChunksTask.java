package com.froobworld.saml.tasks;

import com.froobworld.saml.FrozenChunkCache;
import com.froobworld.saml.Saml;
import com.froobworld.saml.utils.ChunkCoordinates;
import com.froobworld.saml.utils.CompatibilityUtils;
import com.froobworld.saml.utils.UnfreezeChunkConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UnfreezeChunksTask implements Runnable {
    private FrozenChunkCache frozenChunkCache;
    private Saml saml;
    private boolean paper;

    public UnfreezeChunksTask(FrozenChunkCache frozenChunkCache, Saml saml) {
        this.frozenChunkCache = frozenChunkCache;
        this.saml = saml;
        start();
    }

    private void start() {
        if(saml.getSamlConfig().getBoolean("use-paper-get-chunk-async")) {
            if(Bukkit.getServer().getVersion().contains("Paper")) {
                if(CompatibilityUtils.getCanUsePaperAsyncChunkGet()) {
                    paper = true;
                    List<ChunkCoordinates> copy = new ArrayList<ChunkCoordinates>();
                    copy.addAll(frozenChunkCache.getFrozenChunkCoordinates());
                    copy.forEach(c -> c.getWorld().getChunkAtAsync(
                            c.getX(), c.getZ(), false, new UnfreezeChunkConsumer(this)
                    ));
                } else {
                    paper = false;
                    Saml.logger().warning("You elected to use Paper's async chunk fetcher, but this is not supported on your version.");
                    Saml.logger().info("We will use the regular method instead.");
                }
            } else {
                Saml.logger().warning("You elected to use Paper's async chunk fetcher, but you don't seem to be using Paper!");
                Saml.logger().info("We will use the regular method instead.");
                paper = false;
            }
        } else {
            paper = false;
        }
        run();
    }

    @Override
    public void run() {
        if(frozenChunkCache.getFrozenChunkCoordinates().size() == 0) {
            Saml.logger().info("We have finished unfreezing the previously unfrozen mobs.");
            frozenChunkCache.deleteCacheFile();
            return;
        }
        if(!paper) {
            ChunkCoordinates nextChunkCoordinates = frozenChunkCache.getFrozenChunkCoordinates().iterator().next();
            new UnfreezeChunkConsumer(this).accept(nextChunkCoordinates.toChunk());
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(saml, this, saml.getSamlConfig().getLong("ticks-per-cached-chunk-unfreeze"));
    }

    public void chunkUnfrozen(Chunk chunk) {
        frozenChunkCache.removeChunk(chunk);
    }
}
