package com.froobworld.saml.listeners;

import com.froobworld.saml.Saml;
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
            if(saml.getSamlConfig().getBoolean("only-unfreeze-tagged") ? EntityFreezer.isSamlFrozen(saml, (LivingEntity) event.getRightClicked()) : EntityFreezer.isFrozen((LivingEntity) event.getRightClicked())) {
                if(saml.getSamlConfig().getStringList("ignore-metadata").stream().anyMatch(event.getRightClicked()::hasMetadata)) {
                    return;
                }
                boolean unfreezeOnInteract;
                if(saml.getSamlConfig().getBoolean("use-advanced-config") && saml.getAdvancedConfig().keyExists("unfreeze-on-interact." + event.getRightClicked().getType().name())) {
                    unfreezeOnInteract = saml.getAdvancedConfig().getBoolean("unfreeze-on-interact." + event.getRightClicked().getType().name());
                }else {
                    unfreezeOnInteract = saml.getSamlConfig().getBoolean("unfreeze-on-interact");
                }

                double unfreezeOnInteractTpsThreshold;
                if(saml.getSamlConfig().getBoolean("use-advanced-config") && saml.getAdvancedConfig().keyExists("unfreeze-on-interact-tps-threshold." + event.getRightClicked().getType().name())) {
                    unfreezeOnInteractTpsThreshold = saml.getAdvancedConfig().getDouble("unfreeze-on-interact-tps-threshold." + event.getRightClicked().getType().name());
                } else {
                    unfreezeOnInteractTpsThreshold = saml.getSamlConfig().getDouble("unfreeze-on-interact-tps-threshold");
                }

                if(unfreezeOnInteract && saml.getTpsSupplier().get() > unfreezeOnInteractTpsThreshold) {
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
            if(saml.getSamlConfig().getBoolean("only-unfreeze-tagged") ? EntityFreezer.isSamlFrozen(saml, (LivingEntity) event.getEntity()) : EntityFreezer.isFrozen((LivingEntity) event.getEntity())) {
                if(saml.getSamlConfig().getStringList("ignore-metadata").stream().anyMatch(event.getEntity()::hasMetadata)) {
                    return;
                }
                boolean unfreezeOnDamage;
                if(saml.getSamlConfig().getBoolean("use-advanced-config") && saml.getAdvancedConfig().keyExists("unfreeze-on-damage." + event.getEntity().getType().name())) {
                    unfreezeOnDamage = saml.getAdvancedConfig().getBoolean("unfreeze-on-damage." + event.getEntity().getType().name());
                }else {
                    unfreezeOnDamage = saml.getSamlConfig().getBoolean("unfreeze-on-damage");
                }

                double unfreezeOnDamageTpsThreshold;
                if(saml.getSamlConfig().getBoolean("use-advanced-config") && saml.getAdvancedConfig().keyExists("unfreeze-on-damage-tps-threshold." + event.getEntity().getType().name())) {
                    unfreezeOnDamageTpsThreshold = saml.getAdvancedConfig().getDouble("unfreeze-on-damage-tps-threshold." + event.getEntity().getType().name());
                } else {
                    unfreezeOnDamageTpsThreshold = saml.getSamlConfig().getDouble("unfreeze-on-damage-tps-threshold");
                }

                if(unfreezeOnDamage && saml.getTpsSupplier().get() > unfreezeOnDamageTpsThreshold) {
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
        if(saml.getSamlConfig().getStringList("ignore-metadata").stream().anyMatch(event.getTarget()::hasMetadata)) {
            return;
        }
        boolean preventTargetingFrozen;
        if(saml.getSamlConfig().getBoolean("use-advanced-config") && saml.getAdvancedConfig().keyExists("prevent-targeting-frozen." + event.getTarget().getType().name())) {
            preventTargetingFrozen = saml.getAdvancedConfig().getBoolean("prevent-targeting-frozen." + event.getTarget().getType().name());
        } else {
            preventTargetingFrozen = saml.getSamlConfig().getBoolean("prevent-targeting-frozen");
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
        if(saml.getSamlConfig().getStringList("ignore-metadata").stream().anyMatch(event.getEntity()::hasMetadata)) {
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
            if(saml.getSamlConfig().getBoolean("use-advanced-config") && saml.getAdvancedConfig().keyExists("prevent-player-damaging-frozen." + event.getEntity().getType().name())) {
                preventPlayerDamagingFrozen = saml.getAdvancedConfig().getBoolean("prevent-player-damaging-frozen." + event.getEntity().getType().name());
            } else {
                preventPlayerDamagingFrozen = saml.getSamlConfig().getBoolean("prevent-player-damaging-frozen");
            }
            if(preventPlayerDamagingFrozen && EntityFreezer.isFrozen((LivingEntity) event.getEntity())) {
                event.setCancelled(true);
            }
        } else {
            boolean preventDamagingFrozen;
            if(saml.getSamlConfig().getBoolean("use-advanced-config") && saml.getAdvancedConfig().keyExists("prevent-damaging-frozen." + event.getEntity().getType().name())) {
                preventDamagingFrozen = saml.getAdvancedConfig().getBoolean("prevent-damaging-frozen." + event.getEntity().getType().name());
            } else {
                preventDamagingFrozen = saml.getSamlConfig().getBoolean("prevent-damaging-frozen");
            }
            if(preventDamagingFrozen && EntityFreezer.isFrozen((LivingEntity) event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if(saml.getSamlConfig().getBoolean("unfreeze-on-unload")) {
            List<LivingEntity> mobsToUnfreeze = new ArrayList<LivingEntity>();
            for(Entity entity : event.getChunk().getEntities()) {
                if(entity instanceof LivingEntity) {
                    if(saml.getSamlConfig().getBoolean("only-unfreeze-tagged") ? EntityFreezer.isSamlFrozen(saml, (LivingEntity) entity) : EntityFreezer.isFrozen((LivingEntity) entity)) {
                        if(saml.getSamlConfig().getStringList("ignore-metadata").stream().anyMatch(entity::hasMetadata)) {
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
