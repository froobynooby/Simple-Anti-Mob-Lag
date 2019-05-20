package com.froobworld.saml.listeners;

import com.froobworld.saml.Saml;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class EventListener implements Listener {
    private Saml saml;

    public EventListener(Saml saml) {
        this.saml = saml;
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if(saml.getSamlConfig().getBoolean("unfreeze-on-interact")) {
            if (event.getRightClicked() instanceof LivingEntity) {
                if (!((LivingEntity) event.getRightClicked()).hasAI()) {
                    ((LivingEntity) event.getRightClicked()).setAI(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(saml.getSamlConfig().getBoolean("unfreeze-on-damage")) {
            if (event.getEntity() instanceof LivingEntity) {
                if (!((LivingEntity) event.getEntity()).hasAI()) {
                    ((LivingEntity) event.getEntity()).setAI(true);
                }
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if(saml.getSamlConfig().getBoolean("unfreeze-on-unload")) {
            for(Entity entity : event.getChunk().getEntities()) {
                if(entity instanceof LivingEntity) {
                    if(!((LivingEntity) entity).hasAI()) {
                        ((LivingEntity) entity).setAI(true);
                    }
                }
            }
            if(saml.getMobFreezeTask().getFrozenChunkCache() != null) {
                saml.getMobFreezeTask().getFrozenChunkCache().removeChunk(event.getChunk());
            }
        }
    }

}
