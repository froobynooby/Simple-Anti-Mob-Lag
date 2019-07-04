package com.froobworld.saml.group.defaults;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class TypedGroup implements Group<LivingEntity> {
    private EntityType type;
    private double separationDistanceSquared;
    private double minimumSize;

    public TypedGroup(EntityType type, double separationDistance, double minimumSize) {
        this.type = type;
        this.separationDistanceSquared = Math.pow(separationDistance, 2);
        this.minimumSize = minimumSize;
    }


    @Override
    public String getName() {
        return "default_typed_" + type.name().toLowerCase();
    }

    @Override
    public boolean inProtoGroup(LivingEntity entity, ProtoGroup<LivingEntity> protoGroup) {
        return entity.getType() == type && entity.getLocation().distanceSquared(protoGroup.getCentre().getLocation()) <= separationDistanceSquared;
    }

    @Override
    public boolean canBeMember(LivingEntity entity) {
        return entity.getType() == type;
    }

    @Override
    public GroupStatusUpdater<LivingEntity> groupStatusUpdater() {
        return new GroupStatusUpdater<LivingEntity>() {
            private int count;
            private boolean group;

            @Override
            public void updateStatus(LivingEntity entity) {
                count++;
                group = count >= minimumSize;
            }

            @Override
            public boolean isGroup() {
                return group;
            }
        };
    }
}
