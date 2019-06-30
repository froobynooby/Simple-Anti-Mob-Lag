package com.froobworld.saml.group.defaults;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import org.bukkit.entity.LivingEntity;

public class ChunkGroup implements Group {
    private double minimumSize;

    public ChunkGroup(double minimumSize) {
        this.minimumSize = minimumSize;
    }

    @Override
    public String getName() {
        return "default_chunk";
    }

    @Override
    public boolean inProtoGroup(LivingEntity entity, ProtoGroup protoGroup) {
        return entity.getLocation().getBlockX() >> 4 == protoGroup.getCentre().getLocation().getBlockX() >> 4 && entity.getLocation().getBlockZ() >> 4 == protoGroup.getCentre().getLocation().getBlockZ() >> 4;
    }

    @Override
    public boolean canBeCentre(LivingEntity entity) {
        return true;
    }

    @Override
    public GroupStatusUpdater groupStatusUpdater() {
        return new GroupStatusUpdater() {
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
