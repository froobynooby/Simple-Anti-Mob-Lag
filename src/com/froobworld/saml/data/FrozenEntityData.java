package com.froobworld.saml.data;

import com.froobworld.saml.Saml;
import com.froobworld.saml.utils.CompatibilityUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FrozenEntityData {
    private long time;
    private List<String> groups;
    private long minimumFreezeTime;

    private FrozenEntityData(long time, List<String> groups, long minimumFreezeTime) {
        this.time = time;
        this.groups = groups;
        this.minimumFreezeTime = minimumFreezeTime;
    }


    public long getTimeAtFreeze() {
        return time;
    }

    public List<String> getGroups() {
        return groups;
    }

    public long getMinimumFreezeTime() {
        return minimumFreezeTime;
    }

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("time", time);
        JsonArray groupsArray = new JsonArray();
        for(String group : groups) {
            groupsArray.add(group);
        }
        jsonObject.add("groups", groupsArray);
        jsonObject.addProperty("minimumFreezeTime", minimumFreezeTime);

        return jsonObject;
    }

    public static FrozenEntityData fromJsonObject(JsonObject jsonObject) {
        Builder builder = new Builder();
        Optional.ofNullable(jsonObject.get("time")).ifPresent( e -> builder.setTimeFrozen(e.getAsNumber().longValue()) );
        Optional.ofNullable(jsonObject.get("groups")).ifPresent( e -> e.getAsJsonArray().forEach( o -> {
            if(!o.isJsonNull()) {
                builder.addGroup(o.getAsString());
            }
        } ) );
        Optional.ofNullable(jsonObject.get("minimumFreezeTime")).ifPresent( e -> builder.setMinimumFreezeTime(e.getAsNumber().longValue()) );

        return builder.build();
    }

    public static Optional<FrozenEntityData> getFrozenEntityData(Saml saml, LivingEntity entity) {
        if (CompatibilityUtils.PERSISTENT_DATA) {
            return new Supplier<Optional<FrozenEntityData>>() {
                @Override
                public Optional<FrozenEntityData> get() {
                    return Optional.ofNullable(entity.getPersistentDataContainer().get(new NamespacedKey(saml, "frozenEntityData"), new FrozenEntityDataType()));
                }
            }.get();
        } else {
            List<MetadataValue> metadataValues = entity.getMetadata("frozenEntityData");
            if(metadataValues.isEmpty()) {
                return Optional.empty();
            }
            MetadataValue metadataValue = null;
            for(MetadataValue value : metadataValues) {
                if(value.getOwningPlugin() == saml) {
                    metadataValue = value;
                }
            }
            if(metadataValue == null) {
                return Optional.empty();
            }
            if(metadataValue instanceof FixedMetadataValue) {
                if(metadataValue.value() instanceof FrozenEntityData) {
                    return Optional.ofNullable((FrozenEntityData) metadataValue.value());
                }
            }
        }

        return Optional.empty();
    }

    public void setAsFrozenEntityData(Saml saml, LivingEntity entity) {
        if(CompatibilityUtils.PERSISTENT_DATA) {
            final FrozenEntityData thiz = this;
            new Consumer<Void>() {
                @Override
                public void accept(Void aVoid) {
                    entity.getPersistentDataContainer().set(new NamespacedKey(saml, "frozenEntityData"), new FrozenEntityDataType(), thiz);
                }
            }.accept(null);
        } else {
            entity.setMetadata("frozenEntityData", new FixedMetadataValue(saml, this));
        }
    }

    public static void stripOfFrozenEntityData(Saml saml, LivingEntity entity) {
        if(CompatibilityUtils.PERSISTENT_DATA) {
            entity.getPersistentDataContainer().remove(new NamespacedKey(saml, "frozenEntityData"));
        } else {
            entity.removeMetadata("frozenEntityData", saml);
        }
    }

    public static class Builder {
        private long time;
        private List<String> groups;
        private long minimumFreezeTime;

        public Builder() {
            this.time = System.currentTimeMillis();
            this.groups = new ArrayList<String>();
            this.minimumFreezeTime = 0;
        }


        private Builder setTimeFrozen(long time) {
            this.time = time;
            return this;
        }

        public Builder addGroup(String group) {
            groups.add(group);
            return this;
        }

        public Builder setMinimumFreezeTime(long minimumFreezeTime) {
            this.minimumFreezeTime = minimumFreezeTime;
            return this;
        }

        public FrozenEntityData build() {
            return new FrozenEntityData(time, groups, minimumFreezeTime);
        }
    }
}
