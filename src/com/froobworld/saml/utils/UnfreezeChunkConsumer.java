package com.froobworld.saml.utils;

import com.froobworld.saml.FrozenChunkCache;
import com.froobworld.saml.Saml;
import com.froobworld.saml.config.ConfigKeys;
import com.froobworld.saml.events.SamlMobUnfreezeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
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
        List<LivingEntity> unfrozenMobs = new ArrayList<LivingEntity>();
        for(Entity entity : chunk.getEntities()) {
            if(entity instanceof LivingEntity) {
                if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_ONLY_UNFREEZE_TAGGED) ? EntityFreezer.isSamlFrozen(saml, (LivingEntity) entity) : EntityFreezer.isFrozen((LivingEntity) entity)) {
                    if(saml.getSamlConfig().getStringList(ConfigKeys.CNF_IGNORE_METADATA).stream().anyMatch(entity::hasMetadata)) {
                        continue;
                    }
                    EntityFreezer.unfreezeEntity(saml, (LivingEntity) entity);
                    unfrozenMobs.add((LivingEntity) entity);
                }
            }
        }
        if(!unfrozenMobs.isEmpty()) {
            SamlMobUnfreezeEvent mobUnfreezeEvent = new SamlMobUnfreezeEvent(unfrozenMobs, SamlMobUnfreezeEvent.UnfreezeReason.UNFREEZE_CACHED_CHUNK);
            Bukkit.getPluginManager().callEvent(mobUnfreezeEvent);
        }

        frozenChunkCache.removeChunk(chunk);
    }
}
