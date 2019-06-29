package com.froobworld.saml.group;

import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;

public class GroupedEntity {
    private LivingEntity entity;
    private Set<Group> groups;

    public GroupedEntity(LivingEntity entity) {
        this.entity = entity;
        this.groups = new HashSet<Group>();
    }


    public LivingEntity getEntity() {
        return entity;
    }

    public Set<Group> getGroups() {
        return groups;
    }

}
