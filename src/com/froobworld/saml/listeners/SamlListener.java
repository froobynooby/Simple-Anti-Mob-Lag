package com.froobworld.saml.listeners;

import com.froobworld.saml.Saml;
import com.froobworld.saml.events.*;
import com.froobworld.saml.group.entity.EntityGroupOperations;
import com.froobworld.saml.group.entity.groups.DefaultGroup;
import com.froobworld.saml.group.entity.groups.SingularGroup;
import com.froobworld.saml.group.entity.groups.helpers.SpecificCentreTypeGroup;
import com.froobworld.saml.utils.CompatibilityUtils;
import com.froobworld.saml.utils.EntityFreezer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.*;

public class SamlListener implements Listener {
    private Saml saml;

    public SamlListener(Saml saml) {
        this.saml = saml;
    }


    @EventHandler(ignoreCancelled = true)
    public void onSamlConfigReload(SamlConfigReloadEvent event) {
        if(event.getConfig().getBoolean("use-advanced-config")) {
            if(!saml.getAdvancedConfig().isLoaded()) {
                saml.getAdvancedConfig().loadFromFile();
            }
        }
        if(event.getConfig().getBoolean("keep-frozen-chunk-cache")) {
            saml.createFrozenChunkCacheIfNotExist();
        }
        if(!event.getConfig().getBoolean("unfreeze-on-shutdown") || !event.getConfig().getBoolean("unfreeze-on-unload")) {
            if(saml.getFrozenChunkCache() != null) {
                saml.getFrozenChunkCache().setShouldSaveOnExit();
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSamlPreMobFreeze(SamlPreMobFreezeEvent event) {
        if(event.getReason() == SamlMobFreezeEvent.FreezeReason.MAIN_TASK) {
            for(World world : Bukkit.getWorlds()) {
                if(!saml.getSamlConfig().getStringList("ignore-worlds").contains(world.getName())) {
                    event.getFreezeParametersBuilder().addWorld(world);
                }
            }
            event.getFreezeParametersBuilder().broadcastToConsole(saml.getSamlConfig().getBoolean("broadcast-to-console"));
            event.getFreezeParametersBuilder().broadcastToOps(saml.getSamlConfig().getBoolean("broadcast-to-ops"));
            event.getFreezeParametersBuilder().setDoAsync(saml.getSamlConfig().getBoolean("use-async-grouping"));
            event.getFreezeParametersBuilder().setMinimumFreezeTime(saml.getConfig().getLong("minimum-freeze-time"));
            event.getFreezeParametersBuilder().setMaximumOperationTime(saml.getConfig().getLong("maximum-operation-time"));
            boolean customFreezeGroups = false;
            if(saml.getSamlConfig().getBoolean("use-advanced.config")) {
                customFreezeGroups = saml.getAdvancedConfig().getBoolean("use-custom-groups");
            }
            if (saml.getSamlConfig().getBoolean("group-bias") && event.getCurrentFreezeParameters().getCurrentTps() > saml.getSamlConfig().getDouble("group-bias-tps-threshold")) {
                if(customFreezeGroups) {
                    for(String group : saml.getAdvancedConfig().getStringList("freeze-groups")) {
                        event.getFreezeParametersBuilder().includeGroup(saml.getGroupStore().getGroup(group, false));
                    }
                    for(String group : saml.getAdvancedConfig().getStringList("exclude-groups")) {
                        event.getFreezeParametersBuilder().excludeGroup(saml.getGroupStore().getGroup(group, false));
                    }
                } else {
                    event.getFreezeParametersBuilder().includeGroup(new DefaultGroup(saml));
                }
                List<String> alwaysFreezeList = saml.getSamlConfig().getStringList("always-freeze");
                for(EntityType entityType : EntityType.values()) {
                    if(alwaysFreezeList.contains(entityType.name())) {
                        SpecificCentreTypeGroup centreTypeGroup = new SpecificCentreTypeGroup(Collections.singleton(entityType));
                        SingularGroup singularGroup = new SingularGroup();
                        event.getFreezeParametersBuilder().includeGroup(EntityGroupOperations.conjunction("default_always_freeze", centreTypeGroup, singularGroup));
                    }
                }
            } else {
                event.getFreezeParametersBuilder().includeGroup(new SingularGroup());
            }
        }

        boolean ignoreTamed = saml.getSamlConfig().getBoolean("ignore-tamed");
        boolean ignoreNamed = saml.getSamlConfig().getBoolean("ignore-named");
        boolean ignoreLeashed = saml.getSamlConfig().getBoolean("ignore-leashed");
        boolean ignoreLoveMode = saml.getSamlConfig().getBoolean("ignore-love-mode");
        Set<String> neverFreeze = new HashSet<String>(saml.getSamlConfig().getStringList("never-freeze"));
        double ignorePlayerProximityDistanceSquared = Math.pow(saml.getSamlConfig().getDouble("ignore-player-proximity"), 2);
        double ignoreYoungerThanTicks = saml.getSamlConfig().getDouble("ignore-younger-than-ticks");
        boolean ignoreTargetPlayer = saml.getConfig().getBoolean("ignore-target-player");

        double ignoreTamedTpsThreshold = saml.getSamlConfig().getDouble("ignore-tamed-tps-threshold");
        double ignoreNamedTpsThreshold = saml.getSamlConfig().getDouble("ignore-named-tps-threshold");
        double ignoreLeashedTpsThreshold = saml.getSamlConfig().getDouble("ignore-leashed-tps-threshold");
        double ignoreLoveModeTpsThreshold = saml.getSamlConfig().getDouble("ignore-love-mode-tps-threshold");
        double neverFreezeTpsThreshold = saml.getSamlConfig().getDouble("never-freeze-tps-threshold");
        double ignorePlayerProximityTpsThreshold = saml.getSamlConfig().getDouble("ignore-player-proximity-tps-threshold");
        double ignoreYoungerThanTicksTpsThreshold = saml.getSamlConfig().getDouble("ignore-younger-than-ticks-tps-threshold");
        double ignoreTargetPlayerTpsThreshold = saml.getSamlConfig().getDouble("ignore-target-player-tps-threshold");

        HashMap<EntityType, Boolean> typedIgnoreTamed = new HashMap<EntityType, Boolean>();
        HashMap<EntityType, Boolean> typedIgnoreNamed = new HashMap<EntityType, Boolean>();
        HashMap<EntityType, Boolean> typedIgnoreLeashed = new HashMap<EntityType, Boolean>();
        HashMap<EntityType, Boolean> typedIgnoreLoveMode = new HashMap<EntityType, Boolean>();
        HashMap<EntityType, Double> typedIgnorePlayerProximityDistanceSquared = new HashMap<EntityType, Double>();
        HashMap<EntityType, Double> typedIgnoreYoungerThanTicks = new HashMap<EntityType, Double>();
        HashMap<EntityType, Boolean> typedIgnoreTargetPlayer = new HashMap<EntityType, Boolean>();
        if(saml.getSamlConfig().getBoolean("use-advanced-config")) {
            for(EntityType entityType : EntityType.values()) {
                if(saml.getAdvancedConfig().keyExists("ignore-tamed." + entityType.name())) {
                    typedIgnoreTamed.put(entityType, saml.getAdvancedConfig().getBoolean("ignore-tamed." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists("ignore-named." + entityType.name())) {
                    typedIgnoreNamed.put(entityType, saml.getAdvancedConfig().getBoolean("ignore-named." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists("ignore-leashed." + entityType.name())) {
                    typedIgnoreLeashed.put(entityType, saml.getAdvancedConfig().getBoolean("ignore-leashed." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists("ignore-love-mode." + entityType.name())) {
                    typedIgnoreLoveMode.put(entityType, saml.getAdvancedConfig().getBoolean("ignore-love-mode." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists("ignore-player-proximity." + entityType.name())) {
                    typedIgnorePlayerProximityDistanceSquared.put(entityType, Math.pow(saml.getAdvancedConfig().getDouble("ignore-player-proximity." + entityType.name()), 2));
                }
                if(saml.getAdvancedConfig().keyExists("ignore-younger-than-ticks." + entityType.name())) {
                    typedIgnoreYoungerThanTicks.put(entityType, saml.getAdvancedConfig().getDouble("ignore-younger-than-ticks." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists("ignore-target-player." + entityType.name())) {
                    typedIgnoreTargetPlayer.put(entityType, saml.getAdvancedConfig().getBoolean("ignore-target-player." + entityType.name()));
                }
            }
        }

        HashMap<EntityType, Double> typedIgnoreTamedTpsThreshold = new HashMap<EntityType, Double>();
        HashMap<EntityType, Double> typedIgnoreNamedTpsThreshold = new HashMap<EntityType, Double>();
        HashMap<EntityType, Double> typedIgnoreLeashedTpsThreshold = new HashMap<EntityType, Double>();
        HashMap<EntityType, Double> typedIgnoreLoveModeTpsThreshold = new HashMap<EntityType, Double>();
        HashMap<EntityType, Double> typedNeverFreezeTpsThreshold = new HashMap<EntityType, Double>();
        HashMap<EntityType, Double> typedIgnorePlayerProximityTpsThreshold = new HashMap<EntityType, Double>();
        HashMap<EntityType, Double> typedIgnoreYoungerThanTicksTpsThreshold = new HashMap<EntityType, Double>();
        HashMap<EntityType, Double> typedIgnoreTargetPlayerTpsThreshold = new HashMap<EntityType, Double>();
        if(saml.getSamlConfig().getBoolean("use-advanced-config")) {
            for(EntityType entityType : EntityType.values()) {
                if(saml.getAdvancedConfig().keyExists("ignore-tamed-tps-threshold." + entityType.name())) {
                    typedIgnoreTamedTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble("ignore-tamed-tps-threshold." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists("ignore-named-tps-threshold." + entityType.name())) {
                    typedIgnoreNamedTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble("ignore-named-tps-threshold." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists("ignore-leashed-tps-threshold." + entityType.name())) {
                    typedIgnoreLeashedTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble("ignore-leashed-tps-threshold." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists("ignore-love-mode-tps-threshold." + entityType.name())) {
                    typedIgnoreLoveModeTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble("ignore-love-mode-tps-threshold." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists("never-freeze-tps-threshold." + entityType.name())) {
                    typedNeverFreezeTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble("never-freeze-tps-threshold." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists("ignore-player-proximity-tps-threshold." + entityType.name())) {
                    typedIgnorePlayerProximityTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble("ignore-player-proximity-tps-threshold." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists("ignore-younger-than-ticks-tps-threshold." + entityType.name())) {
                    typedIgnoreYoungerThanTicksTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble("ignore-younger-than-ticks-tps-threshold." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists("ignore-target-player-tps-threshold." + entityType.name())) {
                    typedIgnoreTargetPlayerTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble("ignore-target-player-tps-threshold." + entityType.name()));
                }
            }
        }

        double currentTps = event.getCurrentFreezeParameters().getCurrentTps();
        event.getFreezeParametersBuilder().addIgnorePredicate( e -> (currentTps >= typedNeverFreezeTpsThreshold.getOrDefault(e.getType(), neverFreezeTpsThreshold) && neverFreeze.contains(e.getType().name())) );
        event.getFreezeParametersBuilder().addIgnorePredicate( e -> (currentTps >= typedIgnoreTamedTpsThreshold.getOrDefault(e.getType(), ignoreTamedTpsThreshold) && typedIgnoreTamed.getOrDefault(e.getType(), ignoreTamed) && e instanceof Tameable && ((Tameable) e).getOwner() != null) );
        event.getFreezeParametersBuilder().addIgnorePredicate( e -> (currentTps >= typedIgnoreNamedTpsThreshold.getOrDefault(e.getType(), ignoreNamedTpsThreshold) && typedIgnoreNamed.getOrDefault(e.getType(), ignoreNamed) && e.getCustomName() != null) );
        event.getFreezeParametersBuilder().addIgnorePredicate( e -> (currentTps >= typedIgnoreLeashedTpsThreshold.getOrDefault(e.getType(), ignoreLeashedTpsThreshold) && typedIgnoreLeashed.getOrDefault(e.getType(), ignoreLeashed) && e.isLeashed()) );
        event.getFreezeParametersBuilder().addIgnorePredicate( e -> (currentTps >= typedIgnoreLoveModeTpsThreshold.getOrDefault(e.getType(), ignoreLoveModeTpsThreshold) && CompatibilityUtils.ANIMAL_LOVE_MODE && typedIgnoreLoveMode.getOrDefault(e.getType(), ignoreLoveMode) && e instanceof Animals && ((Animals) e).isLoveMode()) );
        event.getFreezeParametersBuilder().addIgnorePredicate( e -> (currentTps >= typedIgnorePlayerProximityTpsThreshold.getOrDefault(e.getType(), ignorePlayerProximityTpsThreshold) && typedIgnorePlayerProximityDistanceSquared.getOrDefault(e.getType(), ignorePlayerProximityDistanceSquared) > 0 && Bukkit.getOnlinePlayers().stream().anyMatch( p -> (p.getWorld().equals(e.getWorld()) && p.getLocation().distanceSquared(e.getLocation()) < typedIgnorePlayerProximityDistanceSquared.getOrDefault(e.getType(), ignorePlayerProximityDistanceSquared)) )) );
        event.getFreezeParametersBuilder().addIgnorePredicate( e -> (saml.getSamlConfig().getStringList("ignore-metadata").stream().anyMatch(e::hasMetadata)) );
        event.getFreezeParametersBuilder().addIgnorePredicate( e -> (currentTps >= typedIgnoreYoungerThanTicksTpsThreshold.getOrDefault(e.getType(), ignoreYoungerThanTicksTpsThreshold) && e.getTicksLived() < typedIgnoreYoungerThanTicks.getOrDefault(e.getType(), ignoreYoungerThanTicks)) );
        event.getFreezeParametersBuilder().addIgnorePredicate( e -> (currentTps >= typedIgnoreTargetPlayerTpsThreshold.getOrDefault(e.getType(), ignoreTargetPlayerTpsThreshold) && typedIgnoreTargetPlayer.getOrDefault(e.getType(), ignoreTargetPlayer) &&  CompatibilityUtils.MOB_TARGET && e instanceof Mob && ((Mob) e).getTarget() instanceof Player) );
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSamlMobFreeze(SamlMobFreezeEvent event) {
        if(CompatibilityUtils.MOB_TARGET) {
            boolean preventTargetingFrozen = saml.getSamlConfig().getBoolean("prevent-targeting-frozen");
            HashMap<EntityType, Boolean> typedPreventTargetingFrozen = new HashMap<EntityType, Boolean>();
            if(saml.getSamlConfig().getBoolean("use-advanced-config")) {
                for (EntityType entityType : EntityType.values()) {
                    if (saml.getAdvancedConfig().keyExists("prevent-target-frozen." + entityType.name())) {
                        typedPreventTargetingFrozen.put(entityType, saml.getAdvancedConfig().getBoolean("prevent-target-frozen." + entityType.name()));
                    }
                }
            }
            for(World world : Bukkit.getWorlds()) {
                for(LivingEntity entity : world.getLivingEntities()) {
                    if(typedPreventTargetingFrozen.getOrDefault(entity.getType(), preventTargetingFrozen) && entity instanceof Mob) {
                        if(((Mob) entity).getTarget() != null && EntityFreezer.isSamlFrozen(saml, ((Mob) entity).getTarget())) {
                            ((Mob) entity).setTarget(null);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSamlPreMobUnfreeze(SamlPreMobUnfreezeEvent event) {
        if(event.getReason() == SamlMobUnfreezeEvent.UnfreezeReason.MAIN_TASK) {
            for(World world : Bukkit.getWorlds()) {
                if(!saml.getSamlConfig().getStringList("ignore-worlds").contains(world.getName())) {
                    event.getUnfreezeParametersBuilder().addWorld(world);
                }
            }
            event.getUnfreezeParametersBuilder().setUnfreezeLimit(saml.getSamlConfig().getLong("unfreeze-limit"));
            if(saml.getSamlConfig().getDouble("minimum-freeze-time") <= 0) {
                event.getUnfreezeParametersBuilder().ignoreRemainingTime(true);
            }
        }

        event.getUnfreezeParametersBuilder().addIgnorePredicate( e -> (saml.getSamlConfig().getStringList("ignore-metadata").stream().anyMatch(e::hasMetadata)) );
        event.getUnfreezeParametersBuilder().addIgnorePredicate( e ->(saml.getSamlConfig().getBoolean("only-unfreeze-tagged") && !EntityFreezer.isSamlFrozen(saml, e)) );
    }

}