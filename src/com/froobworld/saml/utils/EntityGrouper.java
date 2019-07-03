package com.froobworld.saml.utils;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.group.GroupedEntity;
import com.froobworld.saml.group.ProtoGroup;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class EntityGrouper {

    public static List<GroupedEntity> groupEntities(Collection<LivingEntity> entities, Set<Group> groups) {
        List<ProtoGroupedEntity> protoGroupedEntities = new ArrayList<ProtoGroupedEntity>(entities.size());

        for(LivingEntity entity : entities) {
            ProtoGroupedEntity nextProtoGroupedEntity = new ProtoGroupedEntity(entity);
            for(Group group : groups) {
                nextProtoGroupedEntity.protoGroups.put(group, new ArrayList<ProtoGroup>());
                if(group.canBeCentre(entity)) {
                    ProtoGroup nextProtoGroup = new ProtoGroup(group, entity);
                    nextProtoGroupedEntity.centres.put(group, nextProtoGroup);

                    for(ProtoGroupedEntity otherProtoGroupedEntity : protoGroupedEntities) {
                        ProtoGroup otherProtoGroup = otherProtoGroupedEntity.centres.get(group);
                        if(otherProtoGroup != null) {
                            if(!otherProtoGroup.isGroup() || !nextProtoGroup.isGroup()) {
                                if(otherProtoGroup.symmetricAddMemberConditional(nextProtoGroup)) {
                                    otherProtoGroupedEntity.protoGroups.get(group).add(nextProtoGroup);
                                    nextProtoGroupedEntity.protoGroups.get(group).add(otherProtoGroup);
                                }
                            }
                        }
                    }
                }
            }
            protoGroupedEntities.add(nextProtoGroupedEntity);
        }

        ArrayList<GroupedEntity> groupedEntities = new ArrayList<GroupedEntity>(entities.size());
        for(ProtoGroupedEntity protoGroupedEntity : protoGroupedEntities) {
            GroupedEntity groupedEntity = new GroupedEntity(protoGroupedEntity.entity);
            for(Group group : groups) {
                if(protoGroupedEntity.inGroup(group)) {
                    groupedEntity.getGroups().add(group);
                }
            }
            groupedEntities.add(groupedEntity);
        }

        return groupedEntities;
    }

    private static class ProtoGroupedEntity {
        private LivingEntity entity;
        private HashMap<Group, ProtoGroup> centres;
        private HashMap<Group, List<ProtoGroup>> protoGroups;

        public ProtoGroupedEntity(LivingEntity entity) {
            this.entity = entity;
            this.centres = new HashMap<Group, ProtoGroup>();
            this.protoGroups = new HashMap<Group, List<ProtoGroup>>();
        }


        public boolean inGroup(Group group) {
            if(centres.get(group) != null && centres.get(group).isGroup()) {
                return true;
            }
            for(ProtoGroup protoGroup : protoGroups.getOrDefault(group, Collections.emptyList())) {
                if(protoGroup.isGroup()) {
                    return true;
                }
            }
            return false;
        }
    }

}
