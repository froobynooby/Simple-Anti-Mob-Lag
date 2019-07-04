package com.froobworld.saml.group.defaults;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import org.bukkit.entity.LivingEntity;

public class DefaultGroup implements Group<LivingEntity> {
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
    public boolean inProtoGroup(LivingEntity entity, ProtoGroup<LivingEntity> protoGroup) {
        return entity.getLocation().distanceSquared(protoGroup.getCentre().getLocation()) <= separationDistanceSquared;
    }

    @Override
    public boolean canBeCentre(LivingEntity entity) {
        return true;
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
