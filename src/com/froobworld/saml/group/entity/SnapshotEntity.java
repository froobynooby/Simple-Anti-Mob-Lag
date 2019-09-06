package com.froobworld.saml.group.entity;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.utils.EntityUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class SnapshotEntity {
    private EntityType type;
    private Set<String> typeIdentifiers;
    private Location location;
    private Map<Group, Map<String, Object>> properties;

    public SnapshotEntity(LivingEntity entity, Collection<EntityGroup> groups) {
        this.type = entity.getType();
        this.typeIdentifiers = EntityUtils.getTypeIdentifiers(entity);
        this.location = entity.getLocation();
        this.properties = new HashMap<>();
        for(EntityGroup group : groups) {
            properties.put(group, group.getSnapshotProperties(entity));
        }
    }


    public EntityType getType() {
        return type;
    }

    public Set<String> getTypeIdentifiers() {
        return typeIdentifiers;
    }

    public Location getLocation() {
        return location;
    }

    public Map<String, Object> getProperties(Group group) {
        return properties.getOrDefault(group, Collections.emptyMap());
    }

}
