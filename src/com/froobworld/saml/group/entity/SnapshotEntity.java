package com.froobworld.saml.group.entity;

import com.froobworld.saml.group.Group;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class SnapshotEntity {
    private EntityType type;
    private Location location;
    private Map<Group, Map<String, Object>> properties;

    public SnapshotEntity(LivingEntity entity, Collection<EntityGroup> groups) {
        this.type = entity.getType();
        this.location = entity.getLocation();
        this.properties = new HashMap<Group, Map<String, Object>>();
        for(EntityGroup group : groups) {
            properties.put(group, group.getSnapshotProperties(entity));
        }
    }


    public EntityType getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public Map<String, Object> getProperties(Group group) {
        return properties.getOrDefault(group, Collections.emptyMap());
    }

}
