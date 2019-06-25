package com.froobworld.saml.group.defaults;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.group.TypedEntity;
import org.bukkit.entity.LivingEntity;

public class DefaultGroup implements Group {
    private double separationDistanceSquared;
    private double minimumSize;

    public DefaultGroup(double separationDistance, double minimumSize) {
        this.separationDistanceSquared = Math.pow(separationDistance, 2);
        this.minimumSize = minimumSize;
    }


    @Override
    public String getName() {
        return "default_group";
    }

    @Override
    public boolean inProtoGroup(LivingEntity entity, ProtoGroup protoGroup) {
        return entity.getLocation().distanceSquared(protoGroup.getCentre().getLocation()) <= separationDistanceSquared;
    }

    @Override
    public boolean canBeCentre(LivingEntity entity) {
        return true;
    }

    @Override
    public int assignTypeId(LivingEntity entity) {
        return entity.getType().ordinal();
    }

    @Override
    public GroupStatusUpdater groupStatusUpdater() {
        return new GroupStatusUpdater() {
            private int count;
            private boolean group;

            @Override
            public void updateStatus(TypedEntity typedEntity) {
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
