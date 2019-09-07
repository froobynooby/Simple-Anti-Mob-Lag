package com.froobworld.saml.group.entity.groups.helpers;

import com.froobworld.saml.group.GroupMetadata;
import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.group.entity.EntityGroupParser;
import com.froobworld.saml.group.entity.SnapshotEntity;
import com.froobworld.saml.utils.SetUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SpecificTypeGroup implements EntityGroup {
    private static final GroupMetadata METADATA = new GroupMetadata.Builder()
            .setVolatile(false)
            .setRestrictsMembers(true)
            .setRestrictsGroupStatus(false)
            .build();

    private Set<String> acceptedTypes;

    public SpecificTypeGroup(Set<String> acceptedTypes) {
        this.acceptedTypes = acceptedTypes;
    }


    @Override
    public String getName() {
        return "default_specific_type";
    }

    @Override
    public GroupMetadata getGroupMetadata() {
        return METADATA;
    }

    @Override
    public MembershipEligibility getMembershipEligibility(SnapshotEntity candidate) {
        return SetUtils.disjoint(acceptedTypes, candidate.getTypeIdentifiers()) ? MembershipEligibility.CENTRE : MembershipEligibility.CENTRE_OR_MEMBER;
    }

    @Override
    public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
        return new GroupStatusUpdater<SnapshotEntity>() {
            @Override
            public ProtoMemberStatus getProtoMemberStatus(SnapshotEntity candidate, ProtoGroup<? extends SnapshotEntity> protoGroup) {
                if(!SetUtils.disjoint(acceptedTypes, candidate.getTypeIdentifiers())) {
                    return ProtoMemberStatus.MEMBER;
                }
                return ProtoMemberStatus.NON_MEMBER;
            }

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
                Set<String> acceptedTypes = new HashSet<>();
                for (JsonElement jsonElement : jsonObject.get("acceptedTypes").getAsJsonArray()) {
                    acceptedTypes.add(jsonElement.getAsString());
                }

                return new SpecificTypeGroup(acceptedTypes);
            }
        };
    }
}
