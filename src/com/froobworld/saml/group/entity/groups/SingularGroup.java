package com.froobworld.saml.group.entity.groups;

import com.froobworld.saml.group.GroupMetadata;
import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.group.entity.EntityGroupParser;
import com.froobworld.saml.group.entity.EntityGroupPropertyKey;
import com.froobworld.saml.group.entity.SnapshotEntity;
import com.froobworld.saml.utils.SetUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SingularGroup implements EntityGroup {
    private final GroupMetadata metadata;

    private Set<String> acceptedTypes;

    private SingularGroup(Set<String> acceptedTypes) {
        this.acceptedTypes = acceptedTypes;
        this.metadata = new GroupMetadata.Builder()
                .setVolatile(false)
                .setRestrictsEligibility(acceptedTypes != null)
                .setRestrictsMemberStatus(true)
                .setRestrictsGroupStatus(false)
                .build();
    }

    @Override
    public String getName() {
        return "default_singular";
    }

    @Override
    public GroupMetadata getGroupMetadata() {
        return metadata;
    }

    @Override
    public MembershipEligibility getMembershipEligibility(SnapshotEntity candidate) {
        return (acceptedTypes == null || !SetUtils.disjoint(acceptedTypes, candidate.getTypeIdentifiers())) ? MembershipEligibility.CENTRE : MembershipEligibility.NONE;
    }

    @Override
    public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
        return new GroupStatusUpdater<SnapshotEntity>() {

            @Override
            public ProtoMemberStatus getProtoMemberStatus(SnapshotEntity candidate, ProtoGroup<? extends SnapshotEntity> protoGroup) {
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
    public Map<EntityGroupPropertyKey, Object> getSnapshotProperties(LivingEntity entity) {
        return null;
    }

    public static EntityGroupParser<SingularGroup> parser() {
        return new EntityGroupParser<SingularGroup>() {
            @Override
            public SingularGroup fromJson(JsonObject jsonObject) {
                Set<String> acceptedTypes = null;
                if(jsonObject.has("acceptedTypes")) {
                    acceptedTypes = new HashSet<>();
                    for (JsonElement jsonElement : jsonObject.get("acceptedTypes").getAsJsonArray()) {
                        acceptedTypes.add(jsonElement.getAsString());
                    }
                }
                return new SingularGroup(acceptedTypes);
            }
        };
    }
}
