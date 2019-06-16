package com.froobworld.saml.utils;

import com.froobworld.saml.FrozenChunkCache;
import com.froobworld.saml.SamlConfiguration;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.function.Consumer;

public class UnfreezeChunkConsumer implements Consumer<Chunk> {
    private FrozenChunkCache frozenChunkCache;
    private SamlConfiguration config;

    public UnfreezeChunkConsumer(FrozenChunkCache frozenChunkCache, SamlConfiguration config) {
        this.frozenChunkCache = frozenChunkCache;
        this.config = config;
    }


    @Override
    public void accept(Chunk chunk) {
        if(!chunk.isLoaded()) {
            chunk.load(false);
        }
        for(Entity entity : chunk.getEntities()) {
            if(entity instanceof LivingEntity) {
                if(EntityFreezer.isFrozen((LivingEntity) entity)) {
                    if(config.getStringList("ignore-metadata").stream().anyMatch(entity::hasMetadata)) {
                        continue;
                    }
                    EntityFreezer.unfreezeEntity((LivingEntity) entity);
                }
            }
        }
        frozenChunkCache.removeChunk(chunk);
    }
}
