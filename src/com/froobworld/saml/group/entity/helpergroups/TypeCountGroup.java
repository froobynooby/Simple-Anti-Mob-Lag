package com.froobworld.saml.group.entity.helpergroups;

import com.froobworld.saml.group.GroupStatusUpdater;
import com.froobworld.saml.group.ProtoGroup;
import com.froobworld.saml.group.entity.EntityGroup;
import com.froobworld.saml.group.entity.EntityGroupParser;
import com.froobworld.saml.group.entity.SnapshotEntity;
import com.google.gson.JsonObject;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TypeCountGroup implements EntityGroup {
    private Map<EntityType, Double> typedMinimumSize;
    private Map<EntityType, Double> scaledTypedMinimumSize;
    private boolean scaleToTps;
    private Map<EntityType, Double> typedMinimumScaledMinimumSize;
    private double minimumScaleTpsRatio;

    private TypeCountGroup(Map<EntityType, Double> typedMinimumSize, boolean scaleToTps, Map<EntityType, Double> typedMinimumScaledMinimumSize, double minimumScaleTpsRatio) {
        this.typedMinimumSize = typedMinimumSize;
        this.scaledTypedMinimumSize = typedMinimumSize;
        this.scaleToTps = scaleToTps;
        this.typedMinimumScaledMinimumSize = typedMinimumScaledMinimumSize;
        this.minimumScaleTpsRatio = minimumScaleTpsRatio;
    }


    @Override
    public String getName() {
        return "default_type_count";
    }

    @Override
    public boolean inProtoGroup(SnapshotEntity entity, ProtoGroup<? extends SnapshotEntity> protoGroup) {
        return true;
    }

    @Override
    public boolean canBeMember(SnapshotEntity candidate) {
        return true;
    }

    @Override
    public GroupStatusUpdater<SnapshotEntity> groupStatusUpdater() {
        return new GroupStatusUpdater<SnapshotEntity>() {
            private Map<EntityType, Integer> typedCounts = new HashMap<EntityType, Integer>();
            private boolean group = false;

            @Override
            public void updateStatus(SnapshotEntity member) {
                typedCounts.put(member.getType(), typedCounts.getOrDefault(member.getType(), 0) + 1);
                group = typedMinimumSize.entrySet().stream().allMatch( e -> typedCounts.getOrDefault(e.getKey(), 0) >= scaledTypedMinimumSize.get(e.getKey()) );
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
            for (Map.Entry<EntityType, Double> entry : typedMinimumSize.entrySet()) {
                double scaledSize = tps <= minimumScaleTpsRatio * expectedTps ? typedMinimumScaledMinimumSize.get(entry.getKey()) : (entry.getValue() + (expectedTps - tps) / (expectedTps - minimumScaleTpsRatio * expectedTps) * (typedMinimumScaledMinimumSize.get(entry.getKey()) - entry.getValue()));
                scaledTypedMinimumSize.put(entry.getKey(), scaledSize);
            }
        }
    }

    public static EntityGroupParser<TypeCountGroup> parser() {
        return new EntityGroupParser<TypeCountGroup>() {
            @Override
            public TypeCountGroup fromJson(JsonObject jsonObject) {
                Builder builder = new Builder();
                if(jsonObject.has("typedMinimumSize")) {
                    JsonObject typeCountObject = jsonObject.get("typedMinimumSize").getAsJsonObject();
                    for (EntityType type : EntityType.values()) {
                        if (typeCountObject.has(type.name())) {
                            builder.setTypeMinimumSize(type, typeCountObject.get(type.name()).getAsDouble());
                        }
                    }
                }
                if(jsonObject.has("scaleToTps")) {
                    builder.setScaleToTps(jsonObject.get("scaleToTps").getAsBoolean());
                }
                if(jsonObject.has("typedMinimumScaledMinimumSize")) {
                    JsonObject typeMinimumScaledObject = jsonObject.get("typedMinimumScaledMinimumSize").getAsJsonObject();
                    for (EntityType type : EntityType.values()) {
                        if (typeMinimumScaledObject.has(type.name())) {
                            builder.setTypeMinimumScaledMinimumSize(type, typeMinimumScaledObject.get(type.name()).getAsDouble());
                        }
                    }
                }
                if(jsonObject.has("minimumScaleTpsRatio")) {
                    builder.setMinimumScaleTpsRatio(jsonObject.get("minimumScaleTpsRatio").getAsDouble());
                }

                return builder.build();
            }
        };
    }

    public static class Builder {
        private Map<EntityType, Double> typedMinimumSize;
        private boolean scaleToTps;
        private Map<EntityType, Double> typedMinimumScaledMinimumSize;
        private double minimumScaleTpsRatio;

        public Builder() {
            this.typedMinimumSize = new HashMap<EntityType, Double>();
            Arrays.stream(EntityType.values()).forEach( e -> typedMinimumSize.put(e, 0.0) );
            this.scaleToTps = false;
            this.typedMinimumScaledMinimumSize = new HashMap<EntityType, Double>();
            Arrays.stream(EntityType.values()).forEach( e -> typedMinimumScaledMinimumSize.put(e, 0.0) );
            this.minimumScaleTpsRatio = 0;
        }


        public Builder setTypeMinimumSize(EntityType type, double minimumSize) {
            typedMinimumSize.put(type, minimumSize);
            return this;
        }

        public Builder setScaleToTps(boolean scaleToTps) {
            this.scaleToTps = scaleToTps;
            return this;
        }

        public Builder setTypeMinimumScaledMinimumSize(EntityType type, double minimumScaledMinimumSize) {
            typedMinimumScaledMinimumSize.put(type, minimumScaledMinimumSize);
            return this;
        }

        public Builder setMinimumScaleTpsRatio(double minimumScaleTpsRatio) {
            this.minimumScaleTpsRatio = minimumScaleTpsRatio;
            return this;
        }

        public TypeCountGroup build() {
            return new TypeCountGroup(typedMinimumSize, scaleToTps, typedMinimumScaledMinimumSize, minimumScaleTpsRatio);
        }

    }
}
