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

public class BoundedCoordsGroup implements EntityGroup {
    private static final GroupMetadata METADATA = new GroupMetadata.Builder()
            .setVolatile(false)
            .setRestrictsMembers(true)
            .setRestrictsGroupStatus(false)
            .build();

    private double lowerX,lowerY,lowerZ,upperX,upperY,upperZ;

    private BoundedCoordsGroup(double lowerX, double lowerY, double lowerZ, double upperX, double upperY, double upperZ) {
        this.lowerX = lowerX;
        this.lowerY = lowerY;
        this.lowerZ = lowerZ;
        this.upperX = upperX;
        this.upperY = upperY;
        this.upperZ = upperZ;
    }


    @Override
    public String getName() {
        return "default_bounded_coords";
    }

    @Override
    public GroupMetadata getGroupMetadata() {
        return METADATA;
    }

    @Override
    public MembershipEligibility getMembershipEligibility(SnapshotEntity candidate) {
        double x = candidate.getLocation().getX();
        double y = candidate.getLocation().getY();
        double z = candidate.getLocation().getZ();

        return (x > lowerX) && (x < upperX) && (y > lowerY) && (y < upperY) && (z > lowerZ) && (z < upperZ) ? MembershipEligibility.CENTRE_OR_MEMBER : MembershipEligibility.NONE;
    }

    @Override
    public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
        return new GroupStatusUpdater<SnapshotEntity>() {
            @Override
            public ProtoMemberStatus getProtoMemberStatus(SnapshotEntity candidate, ProtoGroup<? extends SnapshotEntity> protoGroup) {
                double x = candidate.getLocation().getX();
                double y = candidate.getLocation().getY();
                double z = candidate.getLocation().getZ();

                return (x > lowerX) && (x < upperX) && (y > lowerY) && (y < upperY) && (z > lowerZ) && (z < upperZ) ? ProtoMemberStatus.MEMBER : ProtoMemberStatus.NON_MEMBER;
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

    public static EntityGroupParser<BoundedCoordsGroup> parser() {
        return new EntityGroupParser<BoundedCoordsGroup>() {
            @Override
            public BoundedCoordsGroup fromJson(JsonObject jsonObject) {
                Builder builder = new Builder();
                if(jsonObject.has("lowerX")) {
                    builder.setLowerX(jsonObject.get("lowerX").getAsDouble());
                }
                if(jsonObject.has("lowerY")) {
                    builder.setLowerY(jsonObject.get("lowerY").getAsDouble());
                }
                if(jsonObject.has("lowerZ")) {
                    builder.setLowerZ(jsonObject.get("lowerZ").getAsDouble());
                }
                if(jsonObject.has("upperX")) {
                    builder.setUpperX(jsonObject.get("upperX").getAsDouble());
                }
                if(jsonObject.has("upperY")) {
                    builder.setUpperY(jsonObject.get("upperY").getAsDouble());
                }
                if(jsonObject.has("upperZ")) {
                    builder.setUpperZ(jsonObject.get("upperZ").getAsDouble());
                }
                return builder.build();
            }
        };
    }

    public static class Builder {
        private double lowerX,lowerY,lowerZ,upperX,upperY,upperZ;

        public Builder() {
            this.lowerX = Double.NEGATIVE_INFINITY;
            this.lowerY = Double.NEGATIVE_INFINITY;
            this.lowerZ = Double.NEGATIVE_INFINITY;
            this.upperX = Double.POSITIVE_INFINITY;
            this.upperY = Double.POSITIVE_INFINITY;
            this.upperZ = Double.POSITIVE_INFINITY;
        }


        public Builder setLowerX(double lowerX) {
            this.lowerX = lowerX;
            return this;
        }

        public Builder setLowerY(double lowerY) {
            this.lowerY = lowerY;
            return this;
        }

        public Builder setLowerZ(double lowerZ) {
            this.lowerZ = lowerZ;
            return this;
        }

        public Builder setUpperX(double upperX) {
            this.upperX = upperX;
            return this;
        }

        public Builder setUpperY(double upperY) {
            this.upperY = upperY;
            return this;
        }

        public Builder setUpperZ(double upperZ) {
            this.upperZ = upperZ;
            return this;
        }

        public BoundedCoordsGroup build() {
            return new BoundedCoordsGroup(lowerX, lowerY, lowerZ, upperX, upperY, upperZ);
        }

    }
}
