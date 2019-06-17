package com.froobworld.saml.data;

import com.froobworld.saml.Saml;
import com.froobworld.saml.utils.CompatibilityUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FrozenEntityData {
    private long time;
    private List<String> groups;
    private long minimumFreezeTime;

    private FrozenEntityData(long time, List<String> groups, long minimumFreezeTime) {
        this.time = time;
        this.groups = groups;
        this.minimumFreezeTime = minimumFreezeTime;
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
        Optional.ofNullable(jsonObject.get("groups")).ifPresent( e -> e.getAsJsonArray().forEach( o -> builder.addGroup(o.getAsString()) ) );
        Optional.ofNullable(jsonObject.get("minimumFreezeTime")).ifPresent( e -> builder.setMinimumFreezeTime(e.getAsNumber().longValue()) );

        return builder.build();
    }

    public static Optional<FrozenEntityData> getFrozenEntityData(Saml saml, LivingEntity entity) {
        return Optional.ofNullable(entity.getPersistentDataContainer().get(new NamespacedKey(saml, "frozenEntityData"), new FrozenEntityDataType()));
    }

    public void setAsFrozenEntityData(Saml saml, LivingEntity entity) {
        entity.getPersistentDataContainer().set(new NamespacedKey(saml, "frozenEntityData"), new FrozenEntityDataType(), this);
    }

    public static void stripOfFrozenEntityData(Saml saml, LivingEntity entity) {
        entity.getPersistentDataContainer().remove(new NamespacedKey(saml, "frozenEntityData"));
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
            if(!CompatibilityUtils.PERSISTENT_DATA) {
                return null;
            }
            return new FrozenEntityData(time, groups, minimumFreezeTime);
        }
    }
}
