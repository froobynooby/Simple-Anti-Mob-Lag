package com.froobworld.saml.listeners;

import com.froobworld.saml.Saml;
import com.froobworld.saml.config.ConfigKeys;
import com.froobworld.saml.data.FreezeReason;
import com.froobworld.saml.data.UnfreezeReason;
import com.froobworld.saml.events.*;
import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.utils.CompatibilityUtils;
import com.froobworld.saml.utils.EntityFreezer;
import com.froobworld.saml.utils.EntityNerfer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import sun.security.krb5.Config;

import java.text.ParseException;
import java.util.*;

public class SamlListener implements Listener {
    private Saml saml;

    public SamlListener(Saml saml) {
        this.saml = saml;
    }


    @EventHandler(ignoreCancelled = true)
    public void onSamlConfigReload(SamlConfigReloadEvent event) {
        if(event.getConfig().getBoolean(ConfigKeys.CNF_USE_ADVANCED_CONFIG)) {
            if(!saml.getAdvancedConfig().isLoaded()) {
                saml.getAdvancedConfig().loadFromFile();
            }
        }
        if(event.getConfig().getBoolean(ConfigKeys.CNF_KEEP_FROZEN_CHUNK_CACHE)) {
            saml.createFrozenChunkCacheIfNotExist();
        }
        if(!event.getConfig().getBoolean(ConfigKeys.CNF_UNFREEZE_ON_SHUTDOWN) || !event.getConfig().getBoolean(ConfigKeys.CNF_UNFREEZE_ON_UNLOAD)) {
            if(saml.getFrozenChunkCache() != null) {
                saml.getFrozenChunkCache().setShouldSaveOnExit();
            }
        }
        saml.reloadTpsSupplier();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSamlPreMobFreeze(SamlPreMobFreezeEvent event) {
        event.getFreezeParametersBuilder().setDoAsync(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_USE_ASYNC_GROUPING));
        event.getFreezeParametersBuilder().setMaximumOperationTime(saml.getSamlConfig().getLong(ConfigKeys.CNF_MAXIMUM_OPERATION_TIME));

        if(event.getReason() == FreezeReason.TPS) {
            Map<String, EntityGroup> groupMap = new HashMap<>();
            for (String group : saml.getSamlConfig().getStringList(ConfigKeys.CNF_TPS_FREEZE_GROUPS)) {
                EntityGroup entityGroup = groupMap.computeIfAbsent(group, this::getGroupOrWarn);
                if(entityGroup != null) {
                    event.getFreezeParametersBuilder().includeFreezeGroup(entityGroup);
                }
            }
            for (String group : saml.getSamlConfig().getStringList(ConfigKeys.CNF_TPS_FREEZE_EXCLUDE_GROUPS)) {
                EntityGroup entityGroup = groupMap.computeIfAbsent(group, this::getGroupOrWarn);
                if(entityGroup != null) {
                    event.getFreezeParametersBuilder().excludeFreezeGroup(entityGroup);
                }
            }
            for (String group : saml.getSamlConfig().getStringList(ConfigKeys.CNF_TPS_NERF_GROUPS)) {
                EntityGroup entityGroup = groupMap.computeIfAbsent(group, this::getGroupOrWarn);
                if(entityGroup != null) {
                    event.getFreezeParametersBuilder().includeNerfGroup(entityGroup);
                }
            }
            for (String group : saml.getSamlConfig().getStringList(ConfigKeys.CNF_TPS_NERF_EXCLUDE_GROUPS)) {
                EntityGroup entityGroup = groupMap.computeIfAbsent(group, this::getGroupOrWarn);
                if(entityGroup != null) {
                    event.getFreezeParametersBuilder().excludeNerfGroup(entityGroup);
                }
            }
            event.getFreezeParametersBuilder().setDoCleanup(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_TPS_DO_CLEANUP));
            event.getFreezeParametersBuilder().setMinimumFreezeTime(saml.getSamlConfig().getLong(ConfigKeys.CNF_TPS_MINIMUM_FREEZE_TIME));
        }
        if(event.getReason() == FreezeReason.PASSIVE) {
            Map<String, EntityGroup> groupMap = new HashMap<>();
            for (String group : saml.getSamlConfig().getStringList(ConfigKeys.CNF_PASSIVE_FREEZE_GROUPS)) {
                EntityGroup entityGroup = groupMap.computeIfAbsent(group, this::getGroupOrWarn);
                if(entityGroup != null) {
                    event.getFreezeParametersBuilder().includeFreezeGroup(entityGroup);
                }
            }
            for (String group : saml.getSamlConfig().getStringList(ConfigKeys.CNF_PASSIVE_FREEZE_EXCLUDE_GROUPS)) {
                EntityGroup entityGroup = groupMap.computeIfAbsent(group, this::getGroupOrWarn);
                if(entityGroup != null) {
                    event.getFreezeParametersBuilder().excludeFreezeGroup(entityGroup);
                }
            }
            for (String group : saml.getSamlConfig().getStringList(ConfigKeys.CNF_PASSIVE_NERF_GROUPS)) {
                EntityGroup entityGroup = groupMap.computeIfAbsent(group, this::getGroupOrWarn);
                if(entityGroup != null) {
                    event.getFreezeParametersBuilder().includeNerfGroup(entityGroup);
                }
            }
            for (String group : saml.getSamlConfig().getStringList(ConfigKeys.CNF_PASSIVE_NERF_EXCLUDE_GROUPS)) {
                EntityGroup entityGroup = groupMap.computeIfAbsent(group, this::getGroupOrWarn);
                if(entityGroup != null) {
                    event.getFreezeParametersBuilder().excludeNerfGroup(entityGroup);
                }
            }
            event.getFreezeParametersBuilder().setDoCleanup(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_PASSIVE_DO_CLEANUP));
        }
        for(World world : Bukkit.getWorlds()) {
            if(!saml.getSamlConfig().getStringList(ConfigKeys.CNF_IGNORE_WORLD).contains(world.getName())) {
                event.getFreezeParametersBuilder().addWorld(world);
            }
        }

        boolean ignoreTamed = saml.getSamlConfig().getBoolean(ConfigKeys.CNF_IGNORE_TAMED);
        boolean ignoreNamed = saml.getSamlConfig().getBoolean(ConfigKeys.CNF_IGNORE_NAMED);
        boolean ignoreLeashed = saml.getSamlConfig().getBoolean(ConfigKeys.CNF_IGNORE_LEASHED);
        boolean ignoreLoveMode = saml.getSamlConfig().getBoolean(ConfigKeys.CNF_IGNORE_LOVE_MODE);
        Set<String> neverFreeze = new HashSet<>(saml.getSamlConfig().getStringList(ConfigKeys.CNF_NEVER_FREEZE));
        double ignorePlayerProximityDistanceSquared = Math.pow(saml.getSamlConfig().getDouble(ConfigKeys.CNF_IGNORE_PLAYER_PROXIMITY), 2);
        double ignoreYoungerThanTicks = saml.getSamlConfig().getDouble(ConfigKeys.CNF_IGNORE_YOUNGER_THAN_TICKS);
        boolean ignoreTargetPlayer = saml.getConfig().getBoolean(ConfigKeys.CNF_IGNORE_TARGET_PLAYER);

        double ignoreTamedTpsThreshold = saml.getSamlConfig().getDouble(ConfigKeys.CNF_IGNORE_TAMED_TPS_THRESHOLD);
        double ignoreNamedTpsThreshold = saml.getSamlConfig().getDouble(ConfigKeys.CNF_IGNORE_NAMED_TPS_THRESHOLD);
        double ignoreLeashedTpsThreshold = saml.getSamlConfig().getDouble(ConfigKeys.CNF_IGNORE_LEASHED_TPS_THRESHOLD);
        double ignoreLoveModeTpsThreshold = saml.getSamlConfig().getDouble(ConfigKeys.CNF_IGNORE_LOVE_MODE_TPS_THRESHOLD);
        double neverFreezeTpsThreshold = saml.getSamlConfig().getDouble(ConfigKeys.CNF_NEVER_FREEZE_TPS_THRESHOLD);
        double ignorePlayerProximityTpsThreshold = saml.getSamlConfig().getDouble(ConfigKeys.CNF_IGNORE_PLAYER_PROXIMITY_TPS_THRESHOLD);
        double ignoreYoungerThanTicksTpsThreshold = saml.getSamlConfig().getDouble(ConfigKeys.CNF_IGNORE_YOUNGER_THAN_TICKS_TPS_THRESHOLD);
        double ignoreTargetPlayerTpsThreshold = saml.getSamlConfig().getDouble(ConfigKeys.CNF_IGNORE_TARGET_PLAYER_TPS_THRESHOLD);

        HashMap<EntityType, Boolean> typedIgnoreTamed = new HashMap<>();
        HashMap<EntityType, Boolean> typedIgnoreNamed = new HashMap<>();
        HashMap<EntityType, Boolean> typedIgnoreLeashed = new HashMap<>();
        HashMap<EntityType, Boolean> typedIgnoreLoveMode = new HashMap<>();
        HashMap<EntityType, Double> typedIgnorePlayerProximityDistanceSquared = new HashMap<>();
        HashMap<EntityType, Double> typedIgnoreYoungerThanTicks = new HashMap<>();
        HashMap<EntityType, Boolean> typedIgnoreTargetPlayer = new HashMap<>();
        if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_USE_ADVANCED_CONFIG)) {
            for(EntityType entityType : EntityType.values()) {
                if(saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_IGNORE_TAMED + "." + entityType.name())) {
                    typedIgnoreTamed.put(entityType, saml.getAdvancedConfig().getBoolean(ConfigKeys.ADV_IGNORE_TAMED + "." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_IGNORE_NAMED + "." + entityType.name())) {
                    typedIgnoreNamed.put(entityType, saml.getAdvancedConfig().getBoolean(ConfigKeys.ADV_IGNORE_NAMED + "." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_IGNORE_LEASHED + "." + entityType.name())) {
                    typedIgnoreLeashed.put(entityType, saml.getAdvancedConfig().getBoolean(ConfigKeys.ADV_IGNORE_LEASHED + "." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_IGNORE_LOVE_MODE + "." + entityType.name())) {
                    typedIgnoreLoveMode.put(entityType, saml.getAdvancedConfig().getBoolean(ConfigKeys.ADV_IGNORE_LOVE_MODE + "." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_IGNORE_PLAYER_PROXIMITY + "." + entityType.name())) {
                    typedIgnorePlayerProximityDistanceSquared.put(entityType, Math.pow(saml.getAdvancedConfig().getDouble(ConfigKeys.ADV_IGNORE_PLAYER_PROXIMITY + "." + entityType.name()), 2));
                }
                if(saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_IGNORE_YOUNGER_THAN_TICKS + "." + entityType.name())) {
                    typedIgnoreYoungerThanTicks.put(entityType, saml.getAdvancedConfig().getDouble(ConfigKeys.ADV_IGNORE_YOUNGER_THAN_TICKS + "." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_IGNORE_TARGET_PLAYER + "." + entityType.name())) {
                    typedIgnoreTargetPlayer.put(entityType, saml.getAdvancedConfig().getBoolean(ConfigKeys.ADV_IGNORE_TARGET_PLAYER + "." + entityType.name()));
                }
            }
        }

        HashMap<EntityType, Double> typedIgnoreTamedTpsThreshold = new HashMap<>();
        HashMap<EntityType, Double> typedIgnoreNamedTpsThreshold = new HashMap<>();
        HashMap<EntityType, Double> typedIgnoreLeashedTpsThreshold = new HashMap<>();
        HashMap<EntityType, Double> typedIgnoreLoveModeTpsThreshold = new HashMap<>();
        HashMap<EntityType, Double> typedNeverFreezeTpsThreshold = new HashMap<>();
        HashMap<EntityType, Double> typedIgnorePlayerProximityTpsThreshold = new HashMap<>();
        HashMap<EntityType, Double> typedIgnoreYoungerThanTicksTpsThreshold = new HashMap<>();
        HashMap<EntityType, Double> typedIgnoreTargetPlayerTpsThreshold = new HashMap<>();
        if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_USE_ADVANCED_CONFIG)) {
            for(EntityType entityType : EntityType.values()) {
                if(saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_IGNORE_TAMED_TPS_THRESHOLD + "." + entityType.name())) {
                    typedIgnoreTamedTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble(ConfigKeys.ADV_IGNORE_TAMED_TPS_THRESHOLD + "." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_IGNORE_NAMED_TPS_THRESHOLD + "." + entityType.name())) {
                    typedIgnoreNamedTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble(ConfigKeys.ADV_IGNORE_NAMED_TPS_THRESHOLD + "." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_IGNORE_LEASHED_TPS_THRESHOLD + "." + entityType.name())) {
                    typedIgnoreLeashedTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble(ConfigKeys.ADV_IGNORE_LEASHED_TPS_THRESHOLD + "." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_IGNORE_LOVE_MODE_TPS_THRESHOLD + "." + entityType.name())) {
                    typedIgnoreLoveModeTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble(ConfigKeys.ADV_IGNORE_LOVE_MODE_TPS_THRESHOLD + "." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_NEVER_FREEZE_TPS_THRESHOLD + "." + entityType.name())) {
                    typedNeverFreezeTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble(ConfigKeys.ADV_NEVER_FREEZE_TPS_THRESHOLD + "." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_IGNORE_PLAYER_PROXIMITY_TPS_THRESHOLD + "." + entityType.name())) {
                    typedIgnorePlayerProximityTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble(ConfigKeys.ADV_IGNORE_PLAYER_PROXIMITY_TPS_THRESHOLD + "." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_IGNORE_YOUNGER_THAN_TICKS_TPS_THRESHOLD + "." + entityType.name())) {
                    typedIgnoreYoungerThanTicksTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble(ConfigKeys.ADV_IGNORE_YOUNGER_THAN_TICKS_TPS_THRESHOLD + "." + entityType.name()));
                }
                if(saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_IGNORE_TARGET_PLAYER_TPS_THRESHOLD + "." + entityType.name())) {
                    typedIgnoreTargetPlayerTpsThreshold.put(entityType, saml.getAdvancedConfig().getDouble(ConfigKeys.ADV_IGNORE_TARGET_PLAYER_TPS_THRESHOLD + "." + entityType.name()));
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
        event.getFreezeParametersBuilder().addIgnorePredicate( e -> (saml.getSamlConfig().getStringList(ConfigKeys.CNF_IGNORE_METADATA).stream().anyMatch(e::hasMetadata)) );
        event.getFreezeParametersBuilder().addIgnorePredicate( e -> (currentTps >= typedIgnoreYoungerThanTicksTpsThreshold.getOrDefault(e.getType(), ignoreYoungerThanTicksTpsThreshold) && e.getTicksLived() < typedIgnoreYoungerThanTicks.getOrDefault(e.getType(), ignoreYoungerThanTicks)) );
        event.getFreezeParametersBuilder().addIgnorePredicate( e -> (currentTps >= typedIgnoreTargetPlayerTpsThreshold.getOrDefault(e.getType(), ignoreTargetPlayerTpsThreshold) && typedIgnoreTargetPlayer.getOrDefault(e.getType(), ignoreTargetPlayer) &&  CompatibilityUtils.MOB_TARGET && e instanceof Mob && ((Mob) e).getTarget() instanceof Player) );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSamlPreMobFreezeMonitor(SamlPreMobFreezeEvent event) {
        if(event.getReason() == FreezeReason.TPS) {
            if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_TPS_FREEZE_BROADCAST_TO_OPS)) {
                String message = saml.getSamlMessages().getString(ConfigKeys.MSG_TPS_FREEZE_START_OPS)
                        .replace("%tps%", event.getCurrentFreezeParameters().getCurrentTps() + "");
                message = ChatColor.translateAlternateColorCodes('&', message);
                Bukkit.broadcast(message, "saml.notify");
            }
            if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_TPS_FREEZE_BROADCAST_TO_CONSOLE)) {
                String message = saml.getSamlMessages().getString(ConfigKeys.MSG_TPS_FREEZE_START_CONSOLE)
                        .replace("%tps%", event.getCurrentFreezeParameters().getCurrentTps() + "");
                message = ChatColor.translateAlternateColorCodes('&', message);
                Saml.logger().info(message);
            }
        }
        if(event.getReason() == FreezeReason.PASSIVE) {
            if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_PASSIVE_FREEZE_BROADCAST_TO_OPS)) {
                String message = saml.getSamlMessages().getString(ConfigKeys.MSG_PASSIVE_FREEZE_START_OPS);
                message = ChatColor.translateAlternateColorCodes('&', message);
                Bukkit.broadcast(message, "saml.notify");
            }
            if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_PASSIVE_FREEZE_BROADCAST_TO_CONSOLE)) {
                String message = saml.getSamlMessages().getString(ConfigKeys.MSG_PASSIVE_FREEZE_START_CONSOLE);
                message = ChatColor.translateAlternateColorCodes('&', message);
                Saml.logger().info(message);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSamlMobFreeze(SamlMobFreezeEvent event) {
        int total = 0;
        int totalAffected = 0;
        int totalFrozen = 0;
        int totalNerfed = 0;

        if(event.getReason() == FreezeReason.TPS || event.getReason() == FreezeReason.PASSIVE) {
            for (World world : Bukkit.getWorlds()) {
                for (LivingEntity entity : world.getLivingEntities()) {
                    total++;
                    boolean affected = false;
                    if (EntityFreezer.isFrozen(entity)) {
                        totalFrozen++;
                        affected = true;
                    }
                    if (EntityNerfer.isNerfed(entity)) {
                        totalNerfed++;
                        affected = true;
                    }
                    if (affected) {
                        totalAffected++;
                    }
                }
            }
        }

        if(event.getReason() == FreezeReason.TPS) {
            if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_TPS_FREEZE_BROADCAST_TO_OPS)) {
                String message = saml.getSamlMessages().getString(ConfigKeys.MSG_TPS_FREEZE_COMPLETE_OPS)
                        .replace("%time%", event.getTimeTaken() + "")
                        .replace("%number_affected%", event.getMobsAffected().size() + "")
                        .replace("%number_frozen%", event.getMobsFrozen().size() + "")
                        .replace("%number_nerfed%", event.getMobsNerfed().size() + "")
                        .replace("%total_number_mobs%", total + "")
                        .replace("%total_number_affected%", totalAffected + "")
                        .replace("%total_number_frozen%", totalFrozen + "")
                        .replace("%total_number_nerfed%", totalNerfed + "");
                message = ChatColor.translateAlternateColorCodes('&', message);
                Bukkit.broadcast(message, "saml.notify");
            }
            if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_TPS_FREEZE_BROADCAST_TO_CONSOLE)) {
                String message = saml.getSamlMessages().getString(ConfigKeys.MSG_TPS_FREEZE_COMPLETE_CONSOLE)
                        .replace("%time%", event.getTimeTaken() + "")
                        .replace("%number_affected%", event.getMobsAffected().size() + "")
                        .replace("%number_frozen%", event.getMobsFrozen().size() + "")
                        .replace("%number_nerfed%", event.getMobsNerfed().size() + "")
                        .replace("%total_number_mobs%", total + "")
                        .replace("%total_number_affected%", totalAffected + "")
                        .replace("%total_number_frozen%", totalFrozen + "")
                        .replace("%total_number_nerfed%", totalNerfed + "");
                message = ChatColor.translateAlternateColorCodes('&', message);
                Saml.logger().info(message);
            }
        }
        if(event.getReason() == FreezeReason.PASSIVE) {
            if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_PASSIVE_FREEZE_BROADCAST_TO_OPS)) {
                String message = saml.getSamlMessages().getString(ConfigKeys.MSG_PASSIVE_FREEZE_COMPLETE_OPS)
                        .replace("%time%", event.getTimeTaken() + "")
                        .replace("%number_affected%", event.getMobsAffected().size() + "")
                        .replace("%number_frozen%", event.getMobsFrozen().size() + "")
                        .replace("%number_nerfed%", event.getMobsNerfed().size() + "")
                        .replace("%total_number_mobs%", total + "")
                        .replace("%total_number_affected%", totalAffected + "")
                        .replace("%total_number_frozen%", totalFrozen + "")
                        .replace("%total_number_nerfed%", totalNerfed + "");
                message = ChatColor.translateAlternateColorCodes('&', message);
                Bukkit.broadcast(message, "saml.notify");
            }
            if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_PASSIVE_FREEZE_BROADCAST_TO_CONSOLE)) {
                String message = saml.getSamlMessages().getString(ConfigKeys.MSG_PASSIVE_FREEZE_COMPLETE_CONSOLE)
                        .replace("%time%", event.getTimeTaken() + "")
                        .replace("%number_affected%", event.getMobsAffected().size() + "")
                        .replace("%number_frozen%", event.getMobsFrozen().size() + "")
                        .replace("%number_nerfed%", event.getMobsNerfed().size() + "")
                        .replace("%total_number_mobs%", total + "")
                        .replace("%total_number_affected%", totalAffected + "")
                        .replace("%total_number_frozen%", totalFrozen + "")
                        .replace("%total_number_nerfed%", totalNerfed + "");
                message = ChatColor.translateAlternateColorCodes('&', message);
                Saml.logger().info(message);
            }
        }


        if(CompatibilityUtils.MOB_TARGET) {
            boolean preventTargetingFrozen = saml.getSamlConfig().getBoolean(ConfigKeys.CNF_PREVENT_TARGETING_FROZEN);
            HashMap<EntityType, Boolean> typedPreventTargetingFrozen = new HashMap<>();
            if(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_USE_ADVANCED_CONFIG)) {
                for (EntityType entityType : EntityType.values()) {
                    if (saml.getAdvancedConfig().keyExists(ConfigKeys.ADV_PREVENT_TARGETING_FROZEN + "." + entityType.name())) {
                        typedPreventTargetingFrozen.put(entityType, saml.getAdvancedConfig().getBoolean(ConfigKeys.ADV_PREVENT_TARGETING_FROZEN + "." + entityType.name()));
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
        if(event.getReason() == UnfreezeReason.TPS) {
            for(World world : Bukkit.getWorlds()) {
                if(!saml.getSamlConfig().getStringList(ConfigKeys.CNF_IGNORE_WORLD).contains(world.getName())) {
                    event.getUnfreezeParametersBuilder().addWorld(world);
                }
            }
            event.getUnfreezeParametersBuilder().setUnfreezeLimit(saml.getSamlConfig().getLong(ConfigKeys.CNF_TPS_UNFREEZE_LIMIT));
            if(saml.getSamlConfig().getDouble(ConfigKeys.CNF_TPS_MINIMUM_FREEZE_TIME) <= 0) {
                event.getUnfreezeParametersBuilder().ignoreRemainingTime(true);
            }
            event.getUnfreezeParametersBuilder().includeFreezeReason(FreezeReason.TPS);
            event.getUnfreezeParametersBuilder().includeFreezeReason(FreezeReason.DEFAULT);
            for(Map.Entry<String, Object> entry : saml.getSamlConfig().getSection(ConfigKeys.CNF_TPS_UNFREEZE_WEIGHTS).entrySet()) {
                event.getUnfreezeParametersBuilder().setUnfreezeWeight(entry.getKey(), (Integer) entry.getValue());
            }
        }

        event.getUnfreezeParametersBuilder().addIgnorePredicate( e -> (saml.getSamlConfig().getStringList(ConfigKeys.CNF_IGNORE_METADATA).stream().anyMatch(e::hasMetadata)) );
        event.getUnfreezeParametersBuilder().addIgnorePredicate( e ->(saml.getSamlConfig().getBoolean(ConfigKeys.CNF_ONLY_UNFREEZE_TAGGED) && !EntityFreezer.isSamlFrozen(saml, e)) );
    }

    private EntityGroup getGroupOrWarn(String groupName) {
        EntityGroup entityGroup = null;
        try {
            entityGroup = saml.getGroupStore().getGroup(groupName, false);
        } catch (ParseException e) {
            Saml.logger().warning("There was an issue retrieving the group '" + groupName + "': parse exception. Run the command '/saml test " + groupName + "' for more info");
            return null;
        }
        if(entityGroup == null) {
            Saml.logger().warning("There was an issue retrieving the group '" + groupName + "': it doesn't exist");
        }
        return entityGroup;
    }

}