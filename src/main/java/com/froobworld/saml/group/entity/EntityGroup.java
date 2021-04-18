package com.froobworld.saml.group.entity;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.group.GroupMetadata;
import com.froobworld.saml.group.GroupStatusUpdater;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface EntityGroup extends Group<SnapshotEntity> {
    public Map<EntityGroupPropertyKey, Object> getSnapshotProperties(LivingEntity entity);
    public default void scaleToTps(double tps, double expectedTps) {}

    public static BiFunction<EntityGroup, EntityGroup, EntityGroup> transformGroupOperation(BiFunction<EntityGroup, EntityGroup, Group<SnapshotEntity>> operation) {
        return new BiFunction<EntityGroup, EntityGroup, EntityGroup>() {
            @Override
            public EntityGroup apply(EntityGroup entityGroup1, EntityGroup entityGroup2) {
                Group<SnapshotEntity> snapshotEntityGroup = operation.apply(entityGroup1, entityGroup2);

                return new EntityGroup() {
                    @Override
                    public Map<EntityGroupPropertyKey, Object> getSnapshotProperties(LivingEntity entity) {
                        Map<EntityGroupPropertyKey, Object> allProperties = new HashMap<>();
                        Map<EntityGroupPropertyKey, Object> snapshotProperties1 = entityGroup1.getSnapshotProperties(entity);
                        Map<EntityGroupPropertyKey, Object> snapshotProperties2 = entityGroup2.getSnapshotProperties(entity);
                        if(snapshotProperties1 != null) {
                            allProperties.putAll(snapshotProperties1);
                        }
                        if(snapshotProperties2 != null) {
                            allProperties.putAll(snapshotProperties2);
                        }

                        return allProperties;
                    }

                    @Override
                    public String getName() {
                        return snapshotEntityGroup.getName();
                    }

                    @Override
                    public GroupMetadata getGroupMetadata() {
                        return snapshotEntityGroup.getGroupMetadata();
                    }

                    @Override
                    public MembershipEligibility getMembershipEligibility(SnapshotEntity candidate) {
                        return snapshotEntityGroup.getMembershipEligibility(candidate);
                    }

                    @Override
                    public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
                        return snapshotEntityGroup.groupStatusUpdater();
                    }

                    @Override
                    public void scaleToTps(double tps, double expectedTps) {
                        entityGroup1.scaleToTps(tps, expectedTps);
                        entityGroup2.scaleToTps(tps, expectedTps);
                    }
                };
            }
        };
    }

    public static Function<EntityGroup, EntityGroup> transformGroupModifier(Function<EntityGroup, Group<SnapshotEntity>> modifier) {
        return new Function<EntityGroup, EntityGroup>() {
            @Override
            public EntityGroup apply(EntityGroup entityGroup) {
                Group<SnapshotEntity> snapshotEntityGroup = modifier.apply(entityGroup);

                return new EntityGroup() {
                    @Override
                    public Map<EntityGroupPropertyKey, Object> getSnapshotProperties(LivingEntity entity) {
                        return entityGroup.getSnapshotProperties(entity);
                    }

                    @Override
                    public String getName() {
                        return snapshotEntityGroup.getName();
                    }

                    @Override
                    public GroupMetadata getGroupMetadata() {
                        return snapshotEntityGroup.getGroupMetadata();
                    }

                    @Override
                    public MembershipEligibility getMembershipEligibility(SnapshotEntity candidate) {
                        return snapshotEntityGroup.getMembershipEligibility(candidate);
                    }

                    @Override
                    public void scaleToTps(double tps, double expectedTps) {
                        entityGroup.scaleToTps(tps, expectedTps);
                    }

                    @Override
                    public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
                        return snapshotEntityGroup.groupStatusUpdater();
                    }
                };
            }
        };
    }
}
