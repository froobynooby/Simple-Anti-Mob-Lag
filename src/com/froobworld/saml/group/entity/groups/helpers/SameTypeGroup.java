package com.froobworld.saml.group.entity.groups.helpers;

import com.froobworld.saml.group.GroupMetadata;
import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.group.entity.EntityGroupParser;
import com.froobworld.saml.group.entity.SnapshotEntity;
import com.google.gson.JsonObject;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class SameTypeGroup implements EntityGroup {
    private static final GroupMetadata METADATA = new GroupMetadata.Builder()
            .setVolatile(false)
            .setRestrictsMembers(true)
            .setRestrictsGroupStatus(false)
            .build();

    @Override
    public String getName() {
        return "default_same_type";
    }

    @Override
    public GroupMetadata getGroupMetadata() {
        return METADATA;
    }

    @Override
    public MembershipEligibility getMembershipEligibility(SnapshotEntity candidate) {
        return MembershipEligibility.CENTRE_OR_MEMBER;
    }

    @Override
    public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
        return new GroupStatusUpdater<SnapshotEntity>() {

            @Override
            public ProtoMemberStatus getProtoMemberStatus(SnapshotEntity candidate, ProtoGroup<? extends SnapshotEntity> protoGroup) {
                return (candidate.getType() == protoGroup.getCentre().getType()) ? ProtoMemberStatus.MEMBER : ProtoMemberStatus.NON_MEMBER;
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

    public static EntityGroupParser<SameTypeGroup> parser() {
        return new EntityGroupParser<SameTypeGroup>() {
            @Override
            public SameTypeGroup fromJson(JsonObject jsonObject) {
                return new SameTypeGroup();
            }
        };
    }
}
