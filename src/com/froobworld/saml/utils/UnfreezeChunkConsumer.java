package com.froobworld.saml.utils;

import com.froobworld.saml.tasks.UnfreezeChunksTask;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.function.Consumer;

public class UnfreezeChunkConsumer implements Consumer<Chunk> {
    private UnfreezeChunksTask unfreezeChunksTask;

    public UnfreezeChunkConsumer(UnfreezeChunksTask unfreezeChunksTask) {
        this.unfreezeChunksTask = unfreezeChunksTask;
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
        unfreezeChunksTask.chunkUnfrozen(chunk);
    }
}
