package com.froobworld.saml.listeners;

import com.froobworld.saml.Config;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class EventListener implements Listener {
    private Config config;

    public EventListener(Config config) {
        this.config = config;
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if(config.getBoolean("unfreeze-on-interact")) {
            if (event.getRightClicked() instanceof LivingEntity) {
                if (!((LivingEntity) event.getRightClicked()).hasAI()) {
                    ((LivingEntity) event.getRightClicked()).setAI(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(config.getBoolean("unfreeze-on-damage")) {
            if (event.getEntity() instanceof LivingEntity) {
                if (!((LivingEntity) event.getEntity()).hasAI()) {
                    ((LivingEntity) event.getEntity()).setAI(true);
                }
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if(config.getBoolean("unfreeze-on-unload")) {
            for(Entity entity : event.getChunk().getEntities()) {
                if(entity instanceof LivingEntity) {
                    if(!((LivingEntity) entity).hasAI()) {
                        ((LivingEntity) entity).setAI(true);
                    }
                }
            }
        }
    }

}
