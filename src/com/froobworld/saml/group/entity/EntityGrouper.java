package com.froobworld.saml.group.entity;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.group.GroupedObject;
import com.froobworld.saml.group.ObjectGrouper;
import org.bukkit.entity.LivingEntity;

import java.util.*;
import java.util.stream.Collectors;

public class EntityGrouper {

    public static List<GroupedObject<LivingEntity>> groupEntities(Collection<LivingEntity> entities, Set<EntityGroup> groups) {
        Collection<IdentifiedSnapshotEntity> snapshotCollection = entities.stream().map( e -> new IdentifiedSnapshotEntity(e, groups) ).collect(Collectors.toList());
        List<GroupedObject<SnapshotEntity>> groupedSnapshotEntities = ObjectGrouper.<SnapshotEntity>groupObjects(new ArrayList<SnapshotEntity>(snapshotCollection), new HashSet<Group<SnapshotEntity>>(groups));


        return groupedSnapshotEntities.stream().map( g -> new GroupedObject<LivingEntity>(((IdentifiedSnapshotEntity) g.getObject()).entity, g.getGroups()) ).collect(Collectors.toList());
    }

    private static class IdentifiedSnapshotEntity extends SnapshotEntity  {
        private LivingEntity entity;

        public IdentifiedSnapshotEntity(LivingEntity entity, Collection<EntityGroup> groups) {
            super(entity, groups);
            this.entity = entity;
        }
    }
}
