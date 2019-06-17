package com.froobworld.saml.utils;

import com.froobworld.saml.FrozenChunkCache;
import com.froobworld.saml.Saml;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.function.Consumer;

public class UnfreezeChunkConsumer implements Consumer<Chunk> {
    private Saml saml;
    private FrozenChunkCache frozenChunkCache;

    public UnfreezeChunkConsumer(Saml saml, FrozenChunkCache frozenChunkCache) {
        this.saml = saml;
        this.frozenChunkCache = frozenChunkCache;
    }


    @Override
    public void accept(Chunk chunk) {
        if(!chunk.isLoaded()) {
            chunk.load(false);
        }
        for(Entity entity : chunk.getEntities()) {
            if(entity instanceof LivingEntity) {
                if(saml.getSamlConfig().getBoolean("only-unfreeze-tagged") ? EntityFreezer.isSamlFrozen(saml, (LivingEntity) entity) : EntityFreezer.isFrozen((LivingEntity) entity)) {
                    if(saml.getSamlConfig().getStringList("ignore-metadata").stream().anyMatch(entity::hasMetadata)) {
                        continue;
                    }
                    EntityFreezer.unfreezeEntity(saml, (LivingEntity) entity);
                }
            }
        }
        frozenChunkCache.removeChunk(chunk);
    }
}
