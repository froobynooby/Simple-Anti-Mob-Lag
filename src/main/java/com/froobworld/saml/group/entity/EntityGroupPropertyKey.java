package com.froobworld.saml.group.entity;

import java.util.Objects;

public class EntityGroupPropertyKey {
    private final EntityGroup entityGroup;
    private final String key;

    public EntityGroupPropertyKey(EntityGroup entityGroup, String key) {
        this.entityGroup = entityGroup;
        this.key = key;
    }


    public String getKey() {
        return key;
    }

    public EntityGroup getEntityGroup() {
        return entityGroup;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof EntityGroupPropertyKey && ((EntityGroupPropertyKey) obj).entityGroup == entityGroup && ((EntityGroupPropertyKey) obj).key.equals(key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityGroup, key);
    }
}
