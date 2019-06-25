package com.froobworld.saml.group.defaults;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.group.TypedEntity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class CustomGroup implements Group {
    private String name;
    private double separationDistanceSquared;
    private Map<Integer, Integer> typedMinimumSize;


    public CustomGroup(String name, double separationDistance, Map<Integer, Integer> typedMinimumSize) {
        this.name = name;
        this.separationDistanceSquared = Math.pow(separationDistance, 2);
        this.typedMinimumSize = typedMinimumSize;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean inProtoGroup(LivingEntity entity, ProtoGroup protoGroup) {
        return typedMinimumSize.containsKey(entity.getType().ordinal()) && entity.getLocation().distanceSquared(protoGroup.getCentre().getLocation()) <= separationDistanceSquared;
    }

    @Override
    public boolean canBeCentre(LivingEntity entity) {
        return typedMinimumSize.containsKey(assignTypeId(entity));
    }

    @Override
    public int assignTypeId(LivingEntity entity) {
        return entity.getType().ordinal();
    }

    @Override
    public GroupStatusUpdater groupStatusUpdater() {
        return new GroupStatusUpdater() {
            private Map<Integer, Integer> typedCounts = new HashMap<Integer, Integer>();
            private boolean group = false;

            @Override
            public void updateStatus(TypedEntity typedEntity) {
                typedCounts.put(typedEntity.getTypeId(), typedCounts.getOrDefault(typedEntity.getTypeId(), 0) + 1);
                group = typedMinimumSize.entrySet().stream().allMatch( e -> typedCounts.getOrDefault(e.getKey(), 0) >= e.getValue() );
            }

            @Override
            public boolean isGroup() {
                return group;
            }
        };
    }
}
