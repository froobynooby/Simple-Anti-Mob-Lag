package com.froobworld.saml.group.entity.groups;

import com.froobworld.saml.Saml;
import com.froobworld.saml.config.ConfigKeys;
import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.group.entity.EntityGroupParser;
import com.froobworld.saml.group.entity.SnapshotEntity;
import com.google.gson.JsonObject;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class DefaultGroup implements EntityGroup {
    private double minimumSize;
    private double scaledMinimumSize;
    private double minimumScaledMinimumSize;
    private double separationDistance;
    private boolean scaleToTps;
    private double scaledSeparationDistanceSquared;
    private double maximumScaledSeparationDistance;
    private double minimumScaleTpsRatio;
    private boolean sameType;

    public DefaultGroup(Saml saml) {
        minimumSize = saml.getSamlConfig().getDouble(ConfigKeys.CNF_GROUP_MINIMUM_SIZE);
        scaledMinimumSize = minimumSize;
        minimumScaledMinimumSize = saml.getSamlConfig().getDouble(ConfigKeys.CNF_GROUP_MINIMUM_SCALED_SIZE);
        separationDistance = saml.getSamlConfig().getDouble(ConfigKeys.CNF_GROUP_MAXIMUM_RADIUS);
        scaleToTps = saml.getSamlConfig().getBoolean(ConfigKeys.CNF_USE_SMART_SCALING);
        scaledSeparationDistanceSquared = Math.pow(separationDistance, 2.0);
        maximumScaledSeparationDistance = saml.getSamlConfig().getDouble(ConfigKeys.CNF_GROUP_MAXIMUM_SCALED_RADIUS);
        minimumScaleTpsRatio = saml.getSamlConfig().getDouble(ConfigKeys.CNF_MINIMUM_SCALE_TPS_RATIO);
        sameType = saml.getSamlConfig().getBoolean(ConfigKeys.CNF_GROUP_REQUIRE_SAME_TYPE);
    }

    @Override
    public String getName() {
        return "default_group";
    }

    @Override
    public ProtoMemberStatus inProtoGroup(SnapshotEntity entity, ProtoGroup<? extends SnapshotEntity> protoGroup) {
        return (!sameType || entity.getType() == protoGroup.getCentre().getType()) && entity.getLocation().distanceSquared(protoGroup.getCentre().getLocation()) <= scaledSeparationDistanceSquared ? ProtoMemberStatus.MEMBER : ProtoMemberStatus.NON_MEMBER;
    }

    @Override
    public MembershipEligibility getMembershipEligibility(SnapshotEntity candidate) {
        return MembershipEligibility.CENTRE_OR_MEMBER;
    }

    @Override
    public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
        return new GroupStatusUpdater<SnapshotEntity>() {
            private int count;
            private boolean group;

            @Override
            public void updateStatus(SnapshotEntity member) {
                count++;
                if(count >= scaledMinimumSize) {
                    group = true;
                }
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

    @Override
    public void scaleToTps(double tps, double expectedTps) {
        if(scaleToTps) {
            if (tps <= expectedTps * minimumScaleTpsRatio || minimumScaleTpsRatio == 1) {
                if(maximumScaledSeparationDistance == Double.POSITIVE_INFINITY) {
                    scaledSeparationDistanceSquared = Double.POSITIVE_INFINITY;
                } else {
                    scaledSeparationDistanceSquared = Math.pow(maximumScaledSeparationDistance, 2.0);
                }
                return;
            }
            if(maximumScaledSeparationDistance == Double.POSITIVE_INFINITY) {
                scaledSeparationDistanceSquared = Math.pow(separationDistance - 1 + 1 / ((tps - expectedTps * minimumScaleTpsRatio) / (expectedTps - expectedTps * minimumScaleTpsRatio)), 2.0);
            } else {
                scaledSeparationDistanceSquared = Math.pow(separationDistance + (expectedTps - tps) / (expectedTps - minimumScaleTpsRatio * expectedTps) * (maximumScaledSeparationDistance - separationDistance), 2.0);
            }
        }
        if(scaleToTps) {
            if (tps <= expectedTps * minimumScaleTpsRatio) {
                scaledMinimumSize = minimumScaledMinimumSize;
                return;
            }
            scaledMinimumSize = minimumSize + (expectedTps - tps) / (expectedTps - minimumScaleTpsRatio * expectedTps) * (minimumScaledMinimumSize - minimumSize);
        }
    }

    public static EntityGroupParser<DefaultGroup> parser(Saml saml) {
        return new EntityGroupParser<DefaultGroup>() {
            @Override
            public DefaultGroup fromJson(JsonObject jsonObject) {
                return new DefaultGroup(saml);
            }
        };
    }

}
