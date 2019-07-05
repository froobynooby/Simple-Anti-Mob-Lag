package com.froobworld.saml.group.defaults;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.utils.SnapshotEntity;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class DefaultGroup implements EntityGroup {
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
    public boolean inProtoGroup(SnapshotEntity entity, ProtoGroup<SnapshotEntity> protoGroup) {
        return entity.getLocation().distanceSquared(protoGroup.getCentre().getLocation()) <= separationDistanceSquared;
    }

    @Override
    public boolean canBeMember(SnapshotEntity entity) {
        return true;
    }

    @Override
    public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
        return new GroupStatusUpdater<SnapshotEntity>() {
            private int count;
            private boolean group;

            @Override
            public void updateStatus(SnapshotEntity entity) {
                count++;
                group = count >= minimumSize;
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
