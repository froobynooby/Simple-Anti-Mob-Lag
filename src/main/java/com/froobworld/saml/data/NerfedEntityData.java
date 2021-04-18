package com.froobworld.saml.data;

import com.froobworld.saml.Saml;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.*;

public class NerfedEntityData {
    private FreezeReason freezeReason;
    private long time;
    private Set<String> groups;
    private long minimumNerfTime;

    private NerfedEntityData(FreezeReason freezeReason, long time, Set<String> groups, long minimumNerfTime) {
        this.freezeReason = freezeReason;
        this.time = time;
        this.groups = groups;
        this.minimumNerfTime = minimumNerfTime;
    }

    public FreezeReason getFreezeReason() {
        return freezeReason;
    }

    public long getTimeAtNerf() {
        return time;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public long getMinimumNerfTime() {
        return minimumNerfTime;
    }

    public void setAsNerfedEntityData(Saml saml, LivingEntity entity) {
        entity.setMetadata("nerfedEntityData", new FixedMetadataValue(saml, this));
    }

    public static Optional<NerfedEntityData> getNerfedEntityData(Saml saml, LivingEntity entity) {
        List<MetadataValue> metadataValues = entity.getMetadata("nerfedEntityData");
        if(metadataValues.isEmpty()) {
            return Optional.empty();
        }
        MetadataValue metadataValue = null;
        for(MetadataValue value : metadataValues) {
            if(saml.equals(value.getOwningPlugin())) {
                metadataValue = value;
            }
        }
        if(metadataValue instanceof FixedMetadataValue) {
            if(metadataValue.value() instanceof NerfedEntityData) {
                return Optional.ofNullable((NerfedEntityData) metadataValue.value());
            }
        }
        return Optional.empty();
    }

    public static void stripOfNerfedEntityData(Saml saml, LivingEntity entity) {
        entity.removeMetadata("nerfedEntityData", saml);
    }

    public static class Builder {
        private FreezeReason freezeReason;
        private long time;
        private Set<String> groups;
        private long minimumNerfTime;

        public Builder() {
            this.freezeReason = FreezeReason.DEFAULT;
            this.time = System.currentTimeMillis();
            this.groups = new HashSet<>();
            this.minimumNerfTime = 0;
        }


        public Builder setFreezeReason(FreezeReason freezeReason) {
            this.freezeReason = freezeReason;
            return this;
        }

        public Builder setTimeNerfed(long time) {
            this.time = time;
            return this;
        }

        public Builder addGroup(String group) {
            this.groups.add(group);
            return this;
        }

        public Builder setMinimumNerfTime(long minimumNerfTime) {
            this.minimumNerfTime = minimumNerfTime;
            return this;
        }

        public NerfedEntityData build() {
            return new NerfedEntityData(freezeReason, time, groups, minimumNerfTime);
        }

    }

}
