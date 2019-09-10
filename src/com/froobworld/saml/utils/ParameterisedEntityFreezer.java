package com.froobworld.saml.utils;

import com.froobworld.saml.Saml;
import com.froobworld.saml.data.*;
import com.froobworld.saml.events.SamlMobFreezeEvent;
import com.froobworld.saml.events.SamlMobUnfreezeEvent;
import com.froobworld.saml.group.GroupedObject;
import com.froobworld.saml.group.ObjectGrouper;
import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.group.entity.SnapshotEntity;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class ParameterisedEntityFreezer {

    public static void freezeToParameters(Saml saml, FreezeParameters parameters, FreezeReason freezeReason) {
        long startTime = System.currentTimeMillis();

        Set<EntityGroup> groups = new HashSet<>();
        groups.addAll(parameters.getIncludeFreezeGroups());
        groups.addAll(parameters.getExcludeFreezeGroups());
        groups.addAll(parameters.getIncludeNerfGroups());
        groups.addAll(parameters.getExcludeNerfGroups());
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

    private static void groupThenFreeze(Saml saml, Map<World, List<IdentifiedSnapshotEntity>> worldFreezeCandidates, Set<EntityGroup> groups, FreezeParameters parameters, long startTime, FreezeReason freezeReason) {
        List<AfflictedEntity> afflictedEntities = new ArrayList<>();
        for(World world : worldFreezeCandidates.keySet()) {
            long timeRemaining = parameters.getMaximumOperationTime() == 0 ? 0 : parameters.getMaximumOperationTime() - System.currentTimeMillis() + startTime;
            if(parameters.getMaximumOperationTime() != 0 && timeRemaining <= 0) {
                break;
            }
            for(GroupedObject<IdentifiedSnapshotEntity> groupedSnapshotEntity : ObjectGrouper.groupObjects(worldFreezeCandidates.get(world), groups, timeRemaining)) {
                AfflictedEntity afflictedEntity = new AfflictedEntity(groupedSnapshotEntity.getObject().entity);
                if(SetUtils.disjoint(groupedSnapshotEntity.getGroups(), parameters.getExcludeFreezeGroups())) {
                    if(!SetUtils.disjoint(groupedSnapshotEntity.getGroups(), parameters.getIncludeFreezeGroups())) {
                        FrozenEntityData.Builder frozenEntityDataBuilder = new FrozenEntityData.Builder()
                                .setFreezeReason(freezeReason)
                                .setMinimumFreezeTime(parameters.getMinimumFreezeTime());
                        groupedSnapshotEntity.getGroups().forEach(g -> frozenEntityDataBuilder.addGroup(g.getName()));

                        afflictedEntity.freeze = true;
                        afflictedEntity.frozenEntityData = frozenEntityDataBuilder.build();
                    }
                }
                if(SetUtils.disjoint(groupedSnapshotEntity.getGroups(), parameters.getExcludeNerfGroups())) {
                    if(!SetUtils.disjoint(groupedSnapshotEntity.getGroups(), parameters.getIncludeNerfGroups())) {
                        NerfedEntityData.Builder nerfedEntityDataBuilder = new NerfedEntityData.Builder()
                                .setFreezeReason(freezeReason)
                                .setMinimumNerfTime(parameters.getMinimumFreezeTime());
                        groupedSnapshotEntity.getGroups().forEach( g -> nerfedEntityDataBuilder.addGroup(g.getName()) );

                        afflictedEntity.nerf = true;
                        afflictedEntity.nerfedEntityData = nerfedEntityDataBuilder.build();
                    }
                }
                if(parameters.doCleanup() || afflictedEntity.freeze || afflictedEntity.nerf) {
                    afflictedEntities.add(afflictedEntity);
                }
            }
        }
        synchronized(saml) {
            if(saml.isEnabled()) {
                Bukkit.getScheduler().runTask(saml, () -> freeze(saml, afflictedEntities, parameters, startTime, freezeReason));
            } else {
                Saml.logger().warning("Plugin was disabled while a freeze was in progress.");
            }
        }
    }

    private static void freeze(Saml saml, List<AfflictedEntity> afflictedEntities, FreezeParameters parameters, long startTime, FreezeReason freezeReason) {
        List<LivingEntity> mobsAffected = new ArrayList<>();
        List<LivingEntity> mobsFrozen = new ArrayList<>();
        List<LivingEntity> mobsNerfed = new ArrayList<>();
        for(AfflictedEntity afflictedEntity : afflictedEntities) {
            if(afflictedEntity.entity.isValid()) {
                boolean afflicted = false;
                if(afflictedEntity.freeze && !EntityFreezer.isFrozen(afflictedEntity.entity)) {
                    EntityFreezer.freezeEntity(saml, afflictedEntity.entity, afflictedEntity.frozenEntityData);
                    mobsFrozen.add(afflictedEntity.entity);
                    if(saml.getFrozenChunkCache() != null) {
                        saml.getFrozenChunkCache().addChunk(afflictedEntity.entity.getLocation());
                    }
                    afflicted = true;
                } else if (parameters.doCleanup() && !afflictedEntity.freeze) {
                    Optional<FrozenEntityData> frozenEntityData = FrozenEntityData.getFrozenEntityData(saml, afflictedEntity.entity);
                    if(frozenEntityData.isPresent()) {
                        if(frozenEntityData.get().getFreezeReason() == freezeReason) {
                            EntityFreezer.unfreezeEntity(saml, afflictedEntity.entity);
                        }
                    }
                }
                if(afflictedEntity.nerf && !EntityNerfer.isNerfed(afflictedEntity.entity)) {
                    EntityNerfer.nerf(saml, afflictedEntity.entity, afflictedEntity.nerfedEntityData);
                    mobsNerfed.add(afflictedEntity.entity);
                    afflicted = true;
                } else if (parameters.doCleanup() && !afflictedEntity.nerf) {
                    Optional<NerfedEntityData> nerfedEntityData = NerfedEntityData.getNerfedEntityData(saml, afflictedEntity.entity);
                    if(nerfedEntityData.isPresent()) {
                        if(nerfedEntityData.get().getFreezeReason() == freezeReason) {
                            EntityNerfer.unnerf(saml, afflictedEntity.entity);
                        }
                    }
                }
                if(afflicted) {
                    mobsAffected.add(afflictedEntity.entity);
                }
            }
        }
        if(saml.getFrozenChunkCache() != null && saml.getFrozenChunkCache().hasUnsavedChanges()) {
            saml.getFrozenChunkCache().saveToFile();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        Bukkit.getPluginManager().callEvent(new SamlMobFreezeEvent(mobsAffected, mobsFrozen, mobsNerfed, elapsedTime, parameters, freezeReason));
    }

    public static void unfreezeToParameters(Saml saml, UnfreezeParameters unfreezeParameters, UnfreezeReason reason) {
        int numberUnfrozen = 0;
        List<LivingEntity> unfrozenList = new ArrayList<>();
        for(World world : unfreezeParameters.getWorlds()) {
            for(LivingEntity entity : world.getLivingEntities()) {
                if(unfreezeParameters.getUnfreezeLimit() != -1 && numberUnfrozen >= unfreezeParameters.getUnfreezeLimit()) {
                    break;
                }
                if(EntityFreezer.isFrozen(entity)) {
                    if (!unfreezeParameters.getIgnorePredicate().test(entity)) {
                        Optional<FrozenEntityData> frozenEntityData = FrozenEntityData.getFrozenEntityData(saml, entity);
                        if(frozenEntityData.isPresent()) {
                            if(unfreezeParameters.includeAllGroups() || frozenEntityData.get().getGroups().stream().anyMatch(unfreezeParameters.getIncludeGroups()::contains)) {
                                if(frozenEntityData.get().getGroups().stream().noneMatch(unfreezeParameters.getExcludeGroups()::contains)) {
                                    if(unfreezeParameters.ignoreRemainingTime() || frozenEntityData.get().getMinimumFreezeTime() <= System.currentTimeMillis() - frozenEntityData.get().getTimeAtFreeze()) {
                                        if(unfreezeParameters.getIncludeFreezeReasons().contains(frozenEntityData.get().getFreezeReason()) && !unfreezeParameters.getExcludeFreezeReasons().contains(frozenEntityData.get().getFreezeReason())) {
                                            EntityFreezer.unfreezeEntity(saml, entity);
                                            unfrozenList.add(entity);
                                            numberUnfrozen++;
                                        }
                                    }
                                }
                            }
                        } else {
                            EntityFreezer.unfreezeEntity(saml, entity);
                            unfrozenList.add(entity);
                            numberUnfrozen++;
                        }
                    }
                }
            }
        }
        if(!unfrozenList.isEmpty()) {
            SamlMobUnfreezeEvent samlMobUnfreezeEvent = new SamlMobUnfreezeEvent(unfrozenList, reason);
            Bukkit.getPluginManager().callEvent(samlMobUnfreezeEvent);
        }
    }

    private static class IdentifiedSnapshotEntity extends SnapshotEntity {
        private LivingEntity entity;

        public IdentifiedSnapshotEntity(LivingEntity entity, Collection<EntityGroup> groups) {
            super(entity, groups);
            this.entity = entity;
        }
    }

    private static class AfflictedEntity {
        private LivingEntity entity;
        private boolean freeze;
        private boolean nerf;
        private FrozenEntityData frozenEntityData;
        private NerfedEntityData nerfedEntityData;

        private AfflictedEntity(LivingEntity entity) {
            this.entity = entity;
        }

    }

}
