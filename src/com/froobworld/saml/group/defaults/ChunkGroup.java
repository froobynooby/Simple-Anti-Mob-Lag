package com.froobworld.saml.group.defaults;

import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.utils.SnapshotEntity;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class ChunkGroup implements EntityGroup {
    private double minimumSize;

    public ChunkGroup(double minimumSize) {
        this.minimumSize = minimumSize;
    }

    @Override
    public String getName() {
        return "default_chunk";
    }

    @Override
    public boolean inProtoGroup(SnapshotEntity entity, ProtoGroup<SnapshotEntity> protoGroup) {
        return entity.getLocation().getBlockX() >> 4 == protoGroup.getCentre().getLocation().getBlockX() >> 4 && entity.getLocation().getBlockZ() >> 4 == protoGroup.getCentre().getLocation().getBlockZ() >> 4;
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
