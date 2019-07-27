package com.froobworld.saml.group.entity.defaultparsers;

import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.group.entity.EntityGroupParser;
import com.froobworld.saml.group.entity.SnapshotEntity;
import com.google.gson.JsonObject;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class SingularGroupParser implements EntityGroupParser {
    @Override
    public EntityGroup fromJson(JsonObject jsonObject) {
        return new EntityGroup() {
            @Override
            public Map<String, Object> getSnapshotProperties(LivingEntity entity) {
                return null;
            }

            @Override
            public String getName() {
                return "default_singular";
            }

            @Override
            public ProtoMemberStatus inProtoGroup(SnapshotEntity entity, ProtoGroup<? extends SnapshotEntity> protoGroup) {
                return ProtoMemberStatus.NON_MEMBER;
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
        };
    }
}
