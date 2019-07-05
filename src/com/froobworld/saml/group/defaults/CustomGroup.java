package com.froobworld.saml.group.defaults;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.utils.SnapshotEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class CustomGroup implements EntityGroup {
    private String name;
    private double separationDistanceSquared;
    private Map<EntityType, Integer> typedMinimumSize;


    public CustomGroup(String name, double separationDistance, Map<EntityType, Integer> typedMinimumSize) {
        this.name = name;
        this.separationDistanceSquared = Math.pow(separationDistance, 2);
        this.typedMinimumSize = typedMinimumSize;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean inProtoGroup(SnapshotEntity entity, ProtoGroup<SnapshotEntity> protoGroup) {
        return typedMinimumSize.containsKey(entity.getType()) && entity.getLocation().distanceSquared(protoGroup.getCentre().getLocation()) <= separationDistanceSquared;
    }

    @Override
    public boolean canBeMember(SnapshotEntity entity) {
        return typedMinimumSize.containsKey(entity.getType());
    }

    @Override
    public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
        return new GroupStatusUpdater<SnapshotEntity>() {
            private Map<EntityType, Integer> typedCounts = new HashMap<EntityType, Integer>();
            private boolean group = false;

            @Override
            public void updateStatus(SnapshotEntity entity) {
                typedCounts.put(entity.getType(), typedCounts.getOrDefault(entity.getType(), 0) + 1);
                group = typedMinimumSize.entrySet().stream().allMatch( e -> typedCounts.getOrDefault(e.getKey(), 0) >= e.getValue() );
            }

            @Override
            public boolean isGroup() {
                return group;
            }
        };
    }

    @Override
    public Map<String, Object> getSnapshotProperties(LivingEntity entity) {
        return null;
    }
}
