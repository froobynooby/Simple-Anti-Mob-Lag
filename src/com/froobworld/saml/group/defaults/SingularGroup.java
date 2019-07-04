package com.froobworld.saml.group.defaults;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import org.bukkit.entity.LivingEntity;

public class SingularGroup implements Group<LivingEntity> {

    @Override
    public String getName() {
        return "default_singular";
    }

    @Override
    public boolean inProtoGroup(LivingEntity entity, ProtoGroup<LivingEntity> protoGroup) {
        return false;
    }

    @Override
    public boolean canBeCentre(LivingEntity entity) {
        return true;
    }

    @Override
    public GroupStatusUpdater<LivingEntity> groupStatusUpdater() {
        return new GroupStatusUpdater<LivingEntity>() {
            private boolean group;

            @Override
            public void updateStatus(LivingEntity entity) {
                group = true;
            }

            @Override
            public boolean isGroup() {
                return group;
            }
        };
    }
}
