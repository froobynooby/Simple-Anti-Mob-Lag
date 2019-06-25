package com.froobworld.saml.utils;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.group.ProtoGroup;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class EntityGrouper {

    public static Map<LivingEntity, Set<Group>> groupEntities(Iterator<LivingEntity> entities, Set<Group> groups) {
        Map<LivingEntity, Set<Group>> groupEntities = new HashMap<LivingEntity, Set<Group>>();
        Map<Group, List<ProtoGroup>> protoGroups = new HashMap<Group, List<ProtoGroup>>();
        Map<Group, Map<LivingEntity, GroupedEntity>> groupedGroupEntities = new HashMap<Group, Map<LivingEntity,GroupedEntity>>();

        for(Group group : groups) {
            protoGroups.put(group, new ArrayList<ProtoGroup>());
            groupedGroupEntities.put(group, new HashMap<LivingEntity, GroupedEntity>());
        }

        while(entities.hasNext()) {
            LivingEntity next = entities.next();
            groupEntities.put(next, new HashSet<Group>());
            for(Group group : groups) {
                if(group.canBeCentre(next)) {
                    ProtoGroup nextProtoGroup = new ProtoGroup(group, next);
                    List<ProtoGroup> otherProtoGroups = protoGroups.get(group);
                    Map<LivingEntity, GroupedEntity> groupedEntities = groupedGroupEntities.get(group);
                    GroupedEntity nextGroupedEntity = new GroupedEntity(next);
                    nextGroupedEntity.protoGroups.add(nextProtoGroup);
                    groupedEntities.put(next, nextGroupedEntity);
                    for(ProtoGroup otherProtoGroup : otherProtoGroups) {
                        if(!(nextProtoGroup.isGroup() && otherProtoGroup.isGroup())) {
                            if(otherProtoGroup.symmetricAddMemberConditional(nextProtoGroup)) {
                                GroupedEntity otherGroupedEntity = groupedEntities.get(otherProtoGroup.getCentre());
                                nextGroupedEntity.protoGroups.add(otherProtoGroup);
                                otherGroupedEntity.protoGroups.add(nextProtoGroup);
                            }
                        }
                    }
                    otherProtoGroups.add(nextProtoGroup);
                }
            }
        }

        for(Map.Entry<Group,Map<LivingEntity, GroupedEntity>> entry1 : groupedGroupEntities.entrySet()) {
            for(Map.Entry<LivingEntity, GroupedEntity> entry2 : entry1.getValue().entrySet()) {
                if(entry2.getValue().inGroup()) {
                    groupEntities.get(entry2.getKey()).add(entry1.getKey());
                }
            }
        }

        return groupEntities;
    }

    private static class GroupedEntity {
        private LivingEntity entity;
        private List<ProtoGroup> protoGroups;

        public GroupedEntity(LivingEntity entity) {
            this.entity = entity;
            this.protoGroups = new ArrayList<ProtoGroup>();
        }


        public boolean inGroup() {
            for(ProtoGroup protoGroup : protoGroups) {
                if(protoGroup.isGroup()) {
                    return true;
                }
            }
            return false;
        }
    }

}
