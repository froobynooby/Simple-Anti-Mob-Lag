package com.froobworld.saml.listeners;

import com.froobworld.saml.Saml;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.HashMap;

public class EventListener implements Listener {
    private Saml saml;

    public EventListener(Saml saml) {
        this.saml = saml;
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        boolean unfreezeOnInteract = saml.getSamlConfig().getBoolean("unfreeze-on-interact");
        HashMap<EntityType, Boolean> typedUnfreezeOnInteract = new HashMap<EntityType, Boolean>();
        if(saml.getSamlConfig().getBoolean("use-advanced-config")) {
            for(EntityType entityType : EntityType.values()) {
                if(saml.getAdvancedConfig().keyExists("unfreeze-on-interact." + entityType.name())) {
                    typedUnfreezeOnInteract.put(entityType, saml.getAdvancedConfig().getBoolean("unfreeze-on-interact." + entityType.name()));
                }
            }
        }

        if(typedUnfreezeOnInteract.getOrDefault(event.getRightClicked().getType(), unfreezeOnInteract)) {
            if (event.getRightClicked() instanceof LivingEntity) {
                if (!((LivingEntity) event.getRightClicked()).hasAI()) {
                    ((LivingEntity) event.getRightClicked()).setAI(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        boolean unfreezeOnDamage = saml.getSamlConfig().getBoolean("unfreeze-on-damage");
        HashMap<EntityType, Boolean> typedUnfreezeOnDamage = new HashMap<EntityType, Boolean>();
        if(saml.getSamlConfig().getBoolean("use-advanced-config")) {
            for(EntityType entityType : EntityType.values()) {
                if(saml.getAdvancedConfig().keyExists("unfreeze-on-damage." + entityType.name())) {
                    typedUnfreezeOnDamage.put(entityType, saml.getAdvancedConfig().getBoolean("unfreeze-on-damage." + entityType.name()));
                }
            }
        }
        if(typedUnfreezeOnDamage.getOrDefault(event.getEntity().getType(), unfreezeOnDamage)) {
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
