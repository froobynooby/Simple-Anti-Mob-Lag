package com.froobworld.saml.group.entity.helpergroups;

import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.group.entity.EntityGroupParser;
import com.froobworld.saml.group.entity.SnapshotEntity;
import com.google.gson.JsonObject;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class SameTypeGroup implements EntityGroup {

    @Override
    public String getName() {
        return "default_same_type";
    }

    @Override
    public boolean inProtoGroup(SnapshotEntity entity, ProtoGroup<? extends SnapshotEntity> protoGroup) {
        return entity.getType() == protoGroup.getCentre().getType();
    }

    @Override
    public boolean canBeMember(SnapshotEntity candidate) {
        return true;
    }

    @Override
    public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
        return new GroupStatusUpdater<SnapshotEntity>() {
            @Override
            public void updateStatus(SnapshotEntity member) {}

            @Override
            public boolean isGroup() {
                return true;
            }
        };
    }

    @Override
    public Map<String, Object> getSnapshotProperties(LivingEntity entity) {
        return null;
    }

    public static EntityGroupParser<SameTypeGroup> parser() {
        return new EntityGroupParser<SameTypeGroup>() {
            @Override
            public SameTypeGroup fromJson(JsonObject jsonObject) {
                return new SameTypeGroup();
            }
        };
    }
}
