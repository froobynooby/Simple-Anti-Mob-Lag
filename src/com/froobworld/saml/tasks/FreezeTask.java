package com.froobworld.saml.tasks;

import com.froobworld.saml.Saml;
import com.froobworld.saml.config.ConfigKeys;
import com.froobworld.saml.data.FreezeParameters;
import com.froobworld.saml.data.FrozenEntityData;
import com.froobworld.saml.events.SamlMobFreezeEvent;
import com.froobworld.saml.group.GroupedObject;
import com.froobworld.saml.group.ObjectGrouper;
import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.group.entity.SnapshotEntity;
import com.froobworld.saml.utils.EntityFreezer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class FreezeTask {

    public static void freezeToParameters(Saml saml, FreezeParameters parameters, SamlMobFreezeEvent.FreezeReason freezeReason) {
        long startTime = System.currentTimeMillis();

        String startFreezeBroadcastMessage = ChatColor.translateAlternateColorCodes('&', saml.getSamlMessages().getString(ConfigKeys.MSG_STARTING_FREEZING_OPERATION)
                .replaceAll("%TPS", parameters.getCurrentTps() + ""));
        if(parameters.broadcastToConsole()) {
            Saml.logger().info(startFreezeBroadcastMessage);
        }
        if(parameters.broadcastToOps()) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission("saml.notify")) {
                    player.sendMessage(startFreezeBroadcastMessage);
                }
            }
        }

        Set<EntityGroup> groups = new HashSet<>();
        groups.addAll(parameters.getIncludeGroups());
        groups.addAll(parameters.getExcludeGroups());
        groups.forEach( g -> g.scaleToTps(parameters.getCurrentTps(), parameters.getExpectedTps()) );

        Map<World, List<IdentifiedSnapshotEntity>> worldFreezeCandidates = new HashMap<>();
        for(World world : parameters.getWorlds()) {
            List<IdentifiedSnapshotEntity> candidates = new ArrayList<>();
            for(LivingEntity entity : world.getLivingEntities()) {
                if(!parameters.getIgnorePredicate().test(entity)) {
                    candidates.add(new IdentifiedSnapshotEntity(entity, groups));
                }
            }
            worldFreezeCandidates.put(world, candidates);
        }
        Runnable groupingTask = () -> groupThenFreeze(saml, worldFreezeCandidates, groups, parameters, startTime, freezeReason);
        if(parameters.doAsync()) {
            Bukkit.getScheduler().runTaskAsynchronously(saml, groupingTask);
        } else {
            Bukkit.getScheduler().runTask(saml, groupingTask);
        }
    }

    private static void groupThenFreeze(Saml saml, Map<World, List<IdentifiedSnapshotEntity>> worldFreezeCandidates, Set<EntityGroup> groups, FreezeParameters parameters, long startTime, SamlMobFreezeEvent.FreezeReason freezeReason) {
        Map<World, List<GroupedObject<IdentifiedSnapshotEntity>>> worldGroupedSnapshotEntities = new HashMap<>();
        for(World world : worldFreezeCandidates.keySet()) {
            long timeRemaining = parameters.getMaximumOperationTime() == 0 ? 0 : parameters.getMaximumOperationTime() - System.currentTimeMillis() + startTime;
            if(parameters.getMaximumOperationTime() != 0 && timeRemaining <= 0) {
                break;
            }
            worldGroupedSnapshotEntities.put(world, ObjectGrouper.groupObjects(worldFreezeCandidates.get(world), groups, timeRemaining));
        }
        Bukkit.getScheduler().runTask(saml, () -> freeze(saml, worldGroupedSnapshotEntities, parameters, startTime, freezeReason));
    }

    private static void freeze(Saml saml, Map<World, List<GroupedObject<IdentifiedSnapshotEntity>>> worldGroupedSnapshotEntities, FreezeParameters parameters, long startTime, SamlMobFreezeEvent.FreezeReason freezeReason) {
        int totalCount = 0;
        int totalFrozen = 0;
        int numberFrozen = 0;
        List<LivingEntity> mobsToFreeze = new ArrayList<>();
        for(World world : worldGroupedSnapshotEntities.keySet()) {
            for(LivingEntity entity : world.getLivingEntities()) {
                totalCount++;
                if(EntityFreezer.isFrozen(entity)) {
                    totalFrozen++;
                }
            }
            for(GroupedObject<IdentifiedSnapshotEntity> groupedSnapshotEntity : worldGroupedSnapshotEntities.get(world)) {
                if(groupedSnapshotEntity.getObject().entity.isValid() && !EntityFreezer.isFrozen(groupedSnapshotEntity.getObject().entity)) {
                    if(groupedSnapshotEntity.getGroups().size() > 0) {
                        if(groupedSnapshotEntity.getGroups().stream().noneMatch(parameters.getExcludeGroups()::contains)) {
                            FrozenEntityData.Builder frozenEntityDataBuilder = new FrozenEntityData.Builder();
                            frozenEntityDataBuilder.setMinimumFreezeTime(parameters.getMinimumFreezeTime());
                            groupedSnapshotEntity.getGroups().forEach( e -> frozenEntityDataBuilder.addGroup(e.getName()) );

                            EntityFreezer.freezeEntity(saml, groupedSnapshotEntity.getObject().entity, frozenEntityDataBuilder.build());
                            if(saml.getFrozenChunkCache() != null) {
                                saml.getFrozenChunkCache().addChunk(groupedSnapshotEntity.getObject().entity.getLocation());
                            }
                            mobsToFreeze.add(groupedSnapshotEntity.getObject().entity);
                            numberFrozen++;
                            totalFrozen++;
                        }
                    }
                }
            }
        }
        Bukkit.getPluginManager().callEvent(new SamlMobFreezeEvent(mobsToFreeze, parameters, freezeReason));
        if(saml.getFrozenChunkCache() != null) {
            saml.getFrozenChunkCache().saveToFile();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        String endFreezeBroacastMessage = ChatColor.translateAlternateColorCodes('&', saml.getSamlMessages().getString(ConfigKeys.MSG_FREEZING_OPERATION_COMPLETE)
                .replaceAll("%NUMBER_FROZEN", numberFrozen + "")
                .replaceAll("%TOTAL_FROZEN", totalFrozen + "")
                .replaceAll("%TOTAL_MOBS", totalCount + "")
                .replaceAll("%TIME", elapsedTime + ""));
        if(parameters.broadcastToConsole()) {
            Saml.logger().info(endFreezeBroacastMessage);
        }
        if(parameters.broadcastToOps()) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission("saml.notify")) {
                    player.sendMessage(endFreezeBroacastMessage);
                }
            }
        }
    }

    private static class IdentifiedSnapshotEntity extends SnapshotEntity {
        private LivingEntity entity;

        public IdentifiedSnapshotEntity(LivingEntity entity, Collection<EntityGroup> groups) {
            super(entity, groups);
            this.entity = entity;
        }
    }

}
