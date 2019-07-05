package com.froobworld.saml.group.defaults;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.utils.SnapshotEntity;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class SingularGroup implements EntityGroup {

    @Override
    public String getName() {
        return "default_singular";
    }

    @Override
    public boolean inProtoGroup(SnapshotEntity entity, ProtoGroup<SnapshotEntity> protoGroup) {
        return false;
    }

    @Override
    public boolean canBeMember(SnapshotEntity entity) {
        return true;
    }

    @Override
    public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
        return new GroupStatusUpdater<SnapshotEntity>() {
            private boolean group;

            @Override
            public void updateStatus(SnapshotEntity entity) {
                group = true;
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
