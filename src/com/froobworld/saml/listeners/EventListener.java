package com.froobworld.saml.listeners;

import com.froobworld.saml.Saml;
import com.froobworld.saml.config.ConfigKeys;
import com.froobworld.saml.events.SamlMobUnfreezeEvent;
import com.froobworld.saml.utils.EntityFreezer;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.List;

public class EventListener implements Listener {
    private Saml saml;

    public EventListener(Saml saml) {
        this.saml = saml;
    }


    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if(event.getRightClicked() instanceof LivingEntity) {
            if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_ONLY_UNFREEZE_TAGGED) ? EntityFreezer.isSamlFrozen(saml, (LivingEntity) event.getRightClicked()) : EntityFreezer.isFrozen((LivingEntity) event.getRightClicked())) {
                if(saml.getSamlConfig().getStringList(ConfigKeys.CNF_IGNORE_METADATA).stream().anyMatch(event.getRightClicked()::hasMetadata)) {
                    return;
                }
                boolean unfreezeOnInteract;
                if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_USE_ADVANCED_CONFIG) && saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_UNFREEZE_ON_INTERACT + "." + event.getRightClicked().getType().name())) {
                    unfreezeOnInteract = saml.getAdvancedConfig().getBoolean(ConfigKeys.ADV_UNFREEZE_ON_INTERACT + "." + event.getRightClicked().getType().name());
                }else {
                    unfreezeOnInteract = saml.getSamlConfig().getBoolean(ConfigKeys.CNF_UNFREEZE_ON_INTERACT);
                }

                double unfreezeOnInteractTpsThreshold;
                if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_USE_ADVANCED_CONFIG) && saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_UNFREEZE_ON_INTERACT_TPS_THRESHOLD + "." + event.getRightClicked().getType().name())) {
                    unfreezeOnInteractTpsThreshold = saml.getAdvancedConfig().getDouble(ConfigKeys.ADV_UNFREEZE_ON_INTERACT_TPS_THRESHOLD + "." + event.getRightClicked().getType().name());
                } else {
                    unfreezeOnInteractTpsThreshold = saml.getSamlConfig().getDouble(ConfigKeys.CNF_UNFREEZE_ON_INTERACT_TPS_THRESHOLD);
                }

                if(unfreezeOnInteract && saml.getTpsSupplier().getTps() > unfreezeOnInteractTpsThreshold) {
                    EntityFreezer.unfreezeEntity(saml, (LivingEntity) event.getRightClicked());

                    SamlMobUnfreezeEvent mobUnfreezeEvent = new SamlMobUnfreezeEvent((LivingEntity) event.getRightClicked(), SamlMobUnfreezeEvent.UnfreezeReason.INTERACTION);
                    Bukkit.getPluginManager().callEvent(mobUnfreezeEvent);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_ONLY_UNFREEZE_TAGGED) ? EntityFreezer.isSamlFrozen(saml, (LivingEntity) event.getEntity()) : EntityFreezer.isFrozen((LivingEntity) event.getEntity())) {
                if(saml.getSamlConfig().getStringList(ConfigKeys.CNF_IGNORE_METADATA).stream().anyMatch(event.getEntity()::hasMetadata)) {
                    return;
                }
                boolean unfreezeOnDamage;
                if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_USE_ADVANCED_CONFIG) && saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_UNFREEZE_ON_DAMAGE + "." + event.getEntity().getType().name())) {
                    unfreezeOnDamage = saml.getAdvancedConfig().getBoolean(ConfigKeys.ADV_UNFREEZE_ON_DAMAGE + "." + event.getEntity().getType().name());
                }else {
                    unfreezeOnDamage = saml.getSamlConfig().getBoolean(ConfigKeys.CNF_UNFREEZE_ON_DAMAGE);
                }

                double unfreezeOnDamageTpsThreshold;
                if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_USE_ADVANCED_CONFIG) && saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_UNFREEZE_ON_DAMAGE_TPS_THRESHOLD + "." + event.getEntity().getType().name())) {
                    unfreezeOnDamageTpsThreshold = saml.getAdvancedConfig().getDouble(ConfigKeys.ADV_UNFREEZE_ON_DAMAGE_TPS_THRESHOLD + "." + event.getEntity().getType().name());
                } else {
                    unfreezeOnDamageTpsThreshold = saml.getSamlConfig().getDouble(ConfigKeys.CNF_UNFREEZE_ON_DAMAGE_TPS_THRESHOLD);
                }

                if(unfreezeOnDamage && saml.getTpsSupplier().getTps() > unfreezeOnDamageTpsThreshold) {
                    EntityFreezer.unfreezeEntity(saml, (LivingEntity) event.getEntity());

                    SamlMobUnfreezeEvent mobUnfreezeEvent = new SamlMobUnfreezeEvent((LivingEntity) event.getEntity(), SamlMobUnfreezeEvent.UnfreezeReason.DAMAGE);
                    Bukkit.getPluginManager().callEvent(mobUnfreezeEvent);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTargetLivingEntityEvent(EntityTargetLivingEntityEvent event) {
        if(event.getTarget() == null || event.getTarget() instanceof Player) {
            return;
        }
        if(saml.getSamlConfig().getStringList(ConfigKeys.CNF_IGNORE_METADATA).stream().anyMatch(event.getTarget()::hasMetadata)) {
            return;
        }
        boolean preventTargetingFrozen;
        if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_USE_ADVANCED_CONFIG) && saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_PREVENT_TARGETING_FROZEN + "." + event.getTarget().getType().name())) {
            preventTargetingFrozen = saml.getAdvancedConfig().getBoolean(ConfigKeys.ADV_PREVENT_TARGETING_FROZEN + "." + event.getTarget().getType().name());
        } else {
            preventTargetingFrozen = saml.getSamlConfig().getBoolean(ConfigKeys.CNF_PREVENT_TARGETING_FROZEN);
        }

        if(preventTargetingFrozen && EntityFreezer.isFrozen(event.getTarget())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntityEntity(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof LivingEntity) || event.getEntity() instanceof Player || event.getEntity() instanceof ArmorStand) {
            return;
        }
        if(saml.getSamlConfig().getStringList(ConfigKeys.CNF_IGNORE_METADATA).stream().anyMatch(event.getEntity()::hasMetadata)) {
            return;
        }
        Entity damager = event.getDamager();
        if(event.getDamager() instanceof Projectile) {
            if(((Projectile) event.getDamager()).getShooter() instanceof Entity) {
                damager = (Entity) ((Projectile) event.getDamager()).getShooter();
            }
        }

        if(damager instanceof Player) {
            boolean preventPlayerDamagingFrozen;
            if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_USE_ADVANCED_CONFIG) && saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_PREVENT_PLAYER_DAMAGING_FROZEN + "." + event.getEntity().getType().name())) {
                preventPlayerDamagingFrozen = saml.getAdvancedConfig().getBoolean(ConfigKeys.ADV_PREVENT_PLAYER_DAMAGING_FROZEN + "." + event.getEntity().getType().name());
            } else {
                preventPlayerDamagingFrozen = saml.getSamlConfig().getBoolean(ConfigKeys.CNF_PREVENT_PLAYER_DAMAGING_FROZEN);
            }
            if(preventPlayerDamagingFrozen && EntityFreezer.isFrozen((LivingEntity) event.getEntity())) {
                event.setCancelled(true);
            }
        } else {
            boolean preventDamagingFrozen;
            if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_USE_ADVANCED_CONFIG) && saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_PREVENT_DAMAGING_FROZEN + "." + event.getEntity().getType().name())) {
                preventDamagingFrozen = saml.getAdvancedConfig().getBoolean(ConfigKeys.ADV_PREVENT_DAMAGING_FROZEN + "." + event.getEntity().getType().name());
            } else {
                preventDamagingFrozen = saml.getSamlConfig().getBoolean(ConfigKeys.CNF_PREVENT_DAMAGING_FROZEN);
            }
            if(preventDamagingFrozen && EntityFreezer.isFrozen((LivingEntity) event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_UNFREEZE_ON_UNLOAD)) {
            List<LivingEntity> mobsToUnfreeze = new ArrayList<LivingEntity>();
            for(Entity entity : event.getChunk().getEntities()) {
                if(entity instanceof LivingEntity) {
                    if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_ONLY_UNFREEZE_TAGGED) ? EntityFreezer.isSamlFrozen(saml, (LivingEntity) entity) : EntityFreezer.isFrozen((LivingEntity) entity)) {
                        if(saml.getSamlConfig().getStringList(ConfigKeys.CNF_IGNORE_METADATA).stream().anyMatch(entity::hasMetadata)) {
                            continue;
                        }
                        EntityFreezer.unfreezeEntity(saml, (LivingEntity) entity);
                        mobsToUnfreeze.add((LivingEntity) entity);
                    }
                }
            }
            SamlMobUnfreezeEvent mobUnfreezeEvent = new SamlMobUnfreezeEvent(mobsToUnfreeze, SamlMobUnfreezeEvent.UnfreezeReason.CHUNK_UNLOAD);
            Bukkit.getPluginManager().callEvent(mobUnfreezeEvent);

            if (saml.getFrozenChunkCache() != null) {
                saml.getFrozenChunkCache().removeChunk(event.getChunk());
            }
        }
    }

}
