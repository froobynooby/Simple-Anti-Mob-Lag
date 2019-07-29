package com.froobworld.saml.group.entity.groups.helpers;

import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.group.entity.EntityGroupParser;
import com.froobworld.saml.group.entity.SnapshotEntity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SpecificTypeGroup implements EntityGroup {
    private Set<EntityType> acceptedTypes;

    public SpecificTypeGroup(Set<EntityType> acceptedTypes) {
        this.acceptedTypes = acceptedTypes;
    }


    @Override
    public String getName() {
        return "default_specific_type";
    }

    @Override
    public ProtoMemberStatus inProtoGroup(SnapshotEntity entity, ProtoGroup<? extends SnapshotEntity> protoGroup) {
        if(acceptedTypes.contains(entity.getType())) {
            return ProtoMemberStatus.MEMBER;
        }
        return ProtoMemberStatus.NON_MEMBER;
    }

    @Override
    public MembershipEligibility getMembershipEligibility(SnapshotEntity candidate) {
        return acceptedTypes.contains(candidate.getType()) ? MembershipEligibility.CENTRE_OR_MEMBER : MembershipEligibility.CENTRE;
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

    public static EntityGroupParser<SpecificTypeGroup> parser() {
        return new EntityGroupParser<SpecificTypeGroup>() {
            @Override
            public SpecificTypeGroup fromJson(JsonObject jsonObject) {
                Set<EntityType> acceptedTypes = new HashSet<EntityType>();
                for (JsonElement jsonElement : jsonObject.get("acceptedTypes").getAsJsonArray()) {
                    EntityType entityType = EntityType.valueOf(jsonElement.getAsString());
                    acceptedTypes.add(entityType);
                }

                return new SpecificTypeGroup(acceptedTypes);
            }
        };
    }
}
