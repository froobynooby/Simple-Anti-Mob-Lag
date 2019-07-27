package com.froobworld.saml.group.entity.helpergroups;

import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.group.entity.EntityGroupParser;
import com.froobworld.saml.group.entity.SnapshotEntity;
import com.froobworld.saml.utils.metric.EuclideanMetric;
import com.froobworld.saml.utils.metric.Metric;
import com.froobworld.saml.utils.metric.SupremumMetric;
import com.google.gson.JsonObject;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class DistanceGroup implements EntityGroup {
    private double separationDistance;
    private Metric metric;
    private boolean scaleToTps;
    private double scaledSeparationDistanceSquared;
    private double maximumScaledSeparationDistance;
    private double minimumScaleTpsRatio;

    private DistanceGroup(double separationDistance, Metric metric, boolean scaleToTps, double maximumScaledSeparationDistance, double minimumScaleTpsRatio) {
        this.separationDistance = separationDistance;
        this.scaledSeparationDistanceSquared = Math.pow(separationDistance, 2.0);
        this.metric = metric;
        this.scaleToTps = scaleToTps;
        this.maximumScaledSeparationDistance = maximumScaledSeparationDistance;
        this.minimumScaleTpsRatio = minimumScaleTpsRatio;
    }


    @Override
    public String getName() {
        return "default_distance";
    }

    @Override
    public ProtoMemberStatus inProtoGroup(SnapshotEntity entity, ProtoGroup<? extends SnapshotEntity> protoGroup) {
        return (metric.distanceSquared(entity.getLocation(), protoGroup.getCentre().getLocation()) <= scaledSeparationDistanceSquared) ? ProtoMemberStatus.MEMBER : ProtoMemberStatus.NON_MEMBER;
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
    }

    public static EntityGroupParser<DistanceGroup> parser() {
        return new EntityGroupParser<DistanceGroup>() {
            @Override
            public DistanceGroup fromJson(JsonObject jsonObject) {
                Builder builder = new Builder(jsonObject.get("separationDistance").getAsDouble());
                if(jsonObject.has("metric")) {
                    builder.setMetric(jsonObject.get("metric").getAsString());
                }
                if(jsonObject.has("weightX")) {
                    builder.setWeightX(jsonObject.get("weightX").getAsDouble());
                }
                if(jsonObject.has("weightY")) {
                    builder.setWeightY(jsonObject.get("weightY").getAsDouble());
                }
                if(jsonObject.has("weightZ")) {
                    builder.setWeightZ(jsonObject.get("weightZ").getAsDouble());
                }
                if(jsonObject.has("scaleToTps")) {
                    builder.setScaleToTps(jsonObject.get("scaleToTps").getAsBoolean());
                }
                if(jsonObject.has("maximumScaledSeparationDistance")) {
                    builder.setMaximumScaledSeparationDistance(jsonObject.get("maximumScaledSeparationDistance").getAsDouble());
                }
                if(jsonObject.has("minimumScaleTpsRatio")) {
                    builder.setMinimumScaleTpsRatio(jsonObject.get("minimumScaleTpsRatio").getAsDouble());
                }

                return builder.build();
            }
        };
    }

    public static class Builder {
        private double seperationDistance;
        private Metric metric;
        private double weightX;
        private double weightY;
        private double weightZ;
        private boolean scaleToTps;
        private double maximumScaledSeparationDistance;
        private double minimumScaleTpsRatio;

        public Builder(double separationDistance) {
            this.seperationDistance = separationDistance;
            this.weightX = 1;
            this.weightY = 1;
            this.weightZ = 1;
            this.metric = new EuclideanMetric(weightX, weightY, weightZ);
            this.scaleToTps = false;
            this.maximumScaledSeparationDistance = Double.POSITIVE_INFINITY;
            this.minimumScaleTpsRatio = 0;
        }


        public Builder setSeparationDistance(double separationDistance) {
            this.seperationDistance = separationDistance;
            return this;
        }

        public Builder setMetric(String metric) {
            if(metric.equalsIgnoreCase("euclidean")) {
                this.metric = new EuclideanMetric(weightX, weightY, weightZ);
                return this;
            }
            if(metric.equalsIgnoreCase("supremum")) {
                this.metric = new SupremumMetric(weightX, weightY, weightZ);
                return this;
            }
            throw new IllegalArgumentException("Unknown metric '" + metric + "'");
        }

        public Builder setMetric(Metric metric) {
            this.metric = metric;
            metric.weight(weightX, weightY, weightZ);
            return this;
        }

        public Builder setWeightX(double weightX) {
            this.weightX = weightX;
            metric.weight(weightX, weightY, weightZ);
            return this;
        }

        public Builder setWeightY(double weightY) {
            this.weightY = weightY;
            metric.weight(weightX, weightY, weightZ);
            return this;
        }

        public Builder setWeightZ(double weightZ) {
            this.weightZ = weightZ;
            metric.weight(weightX, weightY, weightZ);
            return this;
        }

        public Builder setScaleToTps(boolean scaleToTps) {
            this.scaleToTps = scaleToTps;
            return this;
        }

        public Builder setMaximumScaledSeparationDistance(double maximumScaledSeparationDistance) {
            this.maximumScaledSeparationDistance = maximumScaledSeparationDistance;
            return this;
        }

        public Builder setMinimumScaleTpsRatio(double minimumScaleTpsRatio) {
            this.minimumScaleTpsRatio = minimumScaleTpsRatio;
            return this;
        }

        public DistanceGroup build() {
            return new DistanceGroup(seperationDistance, metric, scaleToTps, maximumScaledSeparationDistance, minimumScaleTpsRatio);
        }

    }
}
