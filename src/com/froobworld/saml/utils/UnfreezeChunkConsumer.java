package com.froobworld.saml.utils;

import com.froobworld.saml.FrozenChunkCache;
import com.froobworld.saml.tasks.UnfreezeChunksTask;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.function.Consumer;

public class UnfreezeChunkConsumer implements Consumer<Chunk> {
    private FrozenChunkCache frozenChunkCache;

    public UnfreezeChunkConsumer(FrozenChunkCache frozenChunkCache) {
        this.frozenChunkCache = frozenChunkCache;
    }


    @Override
    public void accept(Chunk chunk) {
        if(!chunk.isLoaded()) {
            chunk.load(false);
        }
        for(Entity entity : chunk.getEntities()) {
            if(entity instanceof LivingEntity) {
                if(!((LivingEntity) entity).hasAI()) {
                    ((LivingEntity) entity).setAI(true);
                }
            }
        }
        frozenChunkCache.removeChunk(chunk);
    }
}
