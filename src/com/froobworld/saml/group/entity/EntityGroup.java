package com.froobworld.saml.group.entity;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.group.GroupMetadata;
import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

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

    public static EntityGroup conditionalise(EntityGroup entityGroup) {
        return new EntityGroup() {
            private final GroupMetadata groupMetadata = new GroupMetadata.Builder()
                    .setVolatile(entityGroup.getGroupMetadata().isVolatile())
                    .setRestrictsMembers(entityGroup.getGroupMetadata().restrictsMembers())
                    .setRestrictsGroupStatus(entityGroup.getGroupMetadata().restrictsGroupStatus())
                    .build();

            @Override
            public Map<EntityGroupPropertyKey, Object> getSnapshotProperties(LivingEntity entity) {
                return entityGroup.getSnapshotProperties(entity);
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public GroupMetadata getGroupMetadata() {
                return groupMetadata;
            }

            @Override
            public MembershipEligibility getMembershipEligibility(SnapshotEntity candidate) {
                return entityGroup.getMembershipEligibility(candidate);
            }

            @Override
            public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
                return new GroupStatusUpdater<SnapshotEntity>() {
                    private GroupStatusUpdater<SnapshotEntity> groupStatusUpdater = entityGroup.groupStatusUpdater();

                    @Override
                    public ProtoMemberStatus getProtoMemberStatus(SnapshotEntity candidate, ProtoGroup<? extends SnapshotEntity> protoGroup) {
                        ProtoMemberStatus protoMemberStatus = groupStatusUpdater.getProtoMemberStatus(candidate, protoGroup);

                        return protoMemberStatus == ProtoMemberStatus.MEMBER ? ProtoMemberStatus.CONDITIONAL : protoMemberStatus;
                    }

                    @Override
                    public void updateStatus(SnapshotEntity member) {
                        groupStatusUpdater.updateStatus(member);
                    }

                    @Override
                    public ProtoMemberStatus attemptUpdateStatus(SnapshotEntity candidate, ProtoGroup<? extends SnapshotEntity> protoGroup) {
                        ProtoMemberStatus protoMemberStatus = groupStatusUpdater.attemptUpdateStatus(candidate, protoGroup);

                        return protoMemberStatus == ProtoMemberStatus.MEMBER ? ProtoMemberStatus.CONDITIONAL : protoMemberStatus;
                    }

                    @Override
                    public boolean isGroup() {
                        return groupStatusUpdater.isGroup();
                    }
                };
            }

            @Override
            public void scaleToTps(double tps, double expectedTps) {
                entityGroup.scaleToTps(tps, expectedTps);
            }
        };
    }

    public static EntityGroup negate(EntityGroup entityGroup) {
        if(entityGroup.getGroupMetadata().restrictsMembers() && entityGroup.getGroupMetadata().restrictsGroupStatus()) {
            throw new IllegalArgumentException("Cannot negate a group which restricts both members and group status");
        }

        return new EntityGroup() {
            private final GroupMetadata groupMetadata = new GroupMetadata.Builder()
                    .setVolatile(entityGroup.getGroupMetadata().restrictsGroupStatus() || entityGroup.getGroupMetadata().isVolatile())
                    .setRestrictsMembers(entityGroup.getGroupMetadata().restrictsMembers())
                    .setRestrictsGroupStatus(entityGroup.getGroupMetadata().restrictsGroupStatus())
                    .build();

            @Override
            public Map<EntityGroupPropertyKey, Object> getSnapshotProperties(LivingEntity entity) {
                return entityGroup.getSnapshotProperties(entity);
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public GroupMetadata getGroupMetadata() {
                return groupMetadata;
            }

            @Override
            public MembershipEligibility getMembershipEligibility(SnapshotEntity candidate) {
                MembershipEligibility membershipEligibility = entityGroup.getMembershipEligibility(candidate);
                if(groupMetadata.restrictsMembers()) {
                    switch(membershipEligibility) {
                        case CENTRE:
                            return MembershipEligibility.MEMBER;
                        case MEMBER:
                            return MembershipEligibility.CENTRE;
                        case CENTRE_OR_MEMBER:
                            return MembershipEligibility.NONE;
                        case NONE:
                            return MembershipEligibility.CENTRE_OR_MEMBER;
                    }
                }

                return membershipEligibility;
            }

            @Override
            public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
                return new GroupStatusUpdater<SnapshotEntity>() {
                    private GroupStatusUpdater<SnapshotEntity> groupStatusUpdater = entityGroup.groupStatusUpdater();

                    @Override
                    public ProtoMemberStatus getProtoMemberStatus(SnapshotEntity candidate, ProtoGroup<? extends SnapshotEntity> protoGroup) {
                        if(entityGroup.getGroupMetadata().restrictsMembers()) {
                            switch (groupStatusUpdater.getProtoMemberStatus(candidate, protoGroup)) {
                                case MEMBER:
                                    return ProtoMemberStatus.NON_MEMBER;
                                case NON_MEMBER:
                                    return ProtoMemberStatus.MEMBER;
                                case CONDITIONAL:
                                    return ProtoMemberStatus.NON_MEMBER;
                            }
                        } else {
                            return groupStatusUpdater.getProtoMemberStatus(candidate, protoGroup);
                        }
                        return null;
                    }

                    @Override
                    public void updateStatus(SnapshotEntity member) {
                        groupStatusUpdater.updateStatus(member);
                    }

                    @Override
                    public ProtoMemberStatus attemptUpdateStatus(SnapshotEntity candidate, ProtoGroup<? extends SnapshotEntity> protoGroup) {
                        if(entityGroup.getGroupMetadata().restrictsMembers()) {
                            return getProtoMemberStatus(candidate, protoGroup);
                        } else {
                            groupStatusUpdater.attemptUpdateStatus(candidate, protoGroup);
                        }
                        return null;
                    }

                    @Override
                    public boolean isGroup() {
                        if(entityGroup.getGroupMetadata().restrictsGroupStatus()) {
                            return !groupStatusUpdater.isGroup();
                        } else {
                            return true;
                        }
                    }
                };
            }

            @Override
            public void scaleToTps(double tps, double expectedTps) {
                entityGroup.scaleToTps(tps, expectedTps);
            }
        };
    }
}
