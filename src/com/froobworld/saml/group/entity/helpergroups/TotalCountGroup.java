package com.froobworld.saml.group.entity.helpergroups;

import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.group.entity.EntityGroupParser;
import com.froobworld.saml.group.entity.SnapshotEntity;
import com.google.gson.JsonObject;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class TotalCountGroup implements EntityGroup {
    private double minimumSize;
    private double scaledMinimumSize;
    private boolean scaleToTps;
    private double minimumScaledMinimumSize;
    private double minimumScaleTpsRatio;

    private TotalCountGroup(double minimumSize, boolean scaleToTps, double minimumScaledMinimumSize, double minimumScaleTpsRatio) {
        this.minimumSize = minimumSize;
        this.scaledMinimumSize = minimumSize;
        this.scaleToTps = scaleToTps;
        this.minimumScaledMinimumSize = minimumScaledMinimumSize;
        this.minimumScaleTpsRatio = minimumScaleTpsRatio;
    }


    @Override
    public String getName() {
        return "default_total_count";
    }

    @Override
    public ProtoMemberStatus inProtoGroup(SnapshotEntity entity, ProtoGroup<? extends SnapshotEntity> protoGroup) {
        return ProtoMemberStatus.MEMBER;
    }

    @Override
    public boolean canBeMember(SnapshotEntity candidate) {
        return true;
    }

    @Override
    public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
        return new GroupStatusUpdater<SnapshotEntity>() {
            private int count;
            private boolean group;

            @Override
            public void updateStatus(SnapshotEntity member) {
                count++;
                group = count >= scaledMinimumSize;
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
            if (tps <= expectedTps * minimumScaleTpsRatio) {
                scaledMinimumSize = minimumScaledMinimumSize;
                return;
            }
            scaledMinimumSize = minimumSize + (expectedTps - tps) / (expectedTps - minimumScaleTpsRatio * expectedTps) * (minimumScaledMinimumSize - minimumSize);
        }
    }

    public static EntityGroupParser<TotalCountGroup> parser() {
        return new EntityGroupParser<TotalCountGroup>() {
            @Override
            public TotalCountGroup fromJson(JsonObject jsonObject) {
                Builder builder = new Builder(jsonObject.get("minimumSize").getAsDouble());
                if(jsonObject.has("scaleToTps")) {
                    builder.setScaleToTps(jsonObject.get("scaleToTps").getAsBoolean());
                }
                if(jsonObject.has("minimumScaledMinimumSize")) {
                    builder.setMinimumScaledMinimumSize(jsonObject.get("minimumScaledMinimumSize").getAsDouble());
                }
                if(jsonObject.has("minimumScaleTpsRatio")) {
                    builder.setMinimumScaleTpsRatio(jsonObject.get("minimumScaleTpsRatio").getAsDouble());
                }

                return builder.build();
            }
        };
    }

    public static class Builder {
        private double minimumSize;
        private boolean scaleToTps;
        private double minimumScaledMinimumSize;
        private double minimumScaleTpsRatio;

        public Builder(double minimumSize) {
            this.minimumSize = minimumSize;
            this.scaleToTps = false;
            this.minimumScaledMinimumSize = 1;
            this.minimumScaleTpsRatio = 0;
        }


        public Builder setMinimumSize(double minimumSize) {
            this.minimumSize = minimumSize;
            return this;
        }

        public Builder setScaleToTps(boolean scaleToTps) {
            this.scaleToTps = scaleToTps;
            return this;
        }

        public Builder setMinimumScaledMinimumSize(double minimumScaledMinimumSize) {
            this.minimumScaledMinimumSize = minimumScaledMinimumSize;
            return this;
        }

        public Builder setMinimumScaleTpsRatio(double minimumScaleTpsRatio) {
            this.minimumScaleTpsRatio = minimumScaleTpsRatio;
            return this;
        }

        public TotalCountGroup build() {
            return new TotalCountGroup(minimumSize, scaleToTps, minimumScaledMinimumSize, minimumScaleTpsRatio);
        }

    }

}
