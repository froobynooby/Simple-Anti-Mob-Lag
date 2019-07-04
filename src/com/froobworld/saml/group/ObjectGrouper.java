package com.froobworld.saml.group;

import java.util.*;

public class ObjectGrouper {

    public static <T> List<GroupedObject<T>> groupObjects(Collection<T> objects, Set<Group<T>> groups, long maxOperationTime) {
        long startTime = System.currentTimeMillis();
        List<ProtoGroupedObject<T>> protoGroupedObjects = new ArrayList<ProtoGroupedObject<T>>(objects.size());

        for(T object : objects) {
            if(maxOperationTime != 0 && System.currentTimeMillis() - startTime >= maxOperationTime) {
                break;
            }
            ProtoGroupedObject<T> nextProtoGroupedObject = new ProtoGroupedObject<T>(object);
            for(Group<T> group : groups) {
                nextProtoGroupedObject.protoGroups.put(group, new ArrayList<ProtoGroup<T>>());
                if(group.canBeMember(object)) {
                    ProtoGroup<T> nextProtoGroup = new ProtoGroup<T>(group, object);
                    nextProtoGroupedObject.centres.put(group, nextProtoGroup);

                    for(ProtoGroupedObject<T> otherProtoGroupedObject : protoGroupedObjects) {
                        ProtoGroup<T> otherProtoGroup = otherProtoGroupedObject.centres.get(group);
                        if(otherProtoGroup != null) {
                            if(!otherProtoGroup.isGroup() || !nextProtoGroup.isGroup()) {
                                if(otherProtoGroup.symmetricAddMemberConditional(nextProtoGroup)) {
                                    otherProtoGroupedObject.protoGroups.get(group).add(nextProtoGroup);
                                    nextProtoGroupedObject.protoGroups.get(group).add(otherProtoGroup);
                                }
                            }
                        }
                    }
                }
            }
            protoGroupedObjects.add(nextProtoGroupedObject);
        }

        ArrayList<GroupedObject<T>> groupedObjects = new ArrayList<GroupedObject<T>>(objects.size());
        for(ProtoGroupedObject<T> protoGroupedObject : protoGroupedObjects) {
            GroupedObject<T> groupedObject = new GroupedObject<T>(protoGroupedObject.object);
            for(Group<T> group : groups) {
                if(protoGroupedObject.inGroup(group)) {
                    groupedObject.getGroups().add(group);
                }
            }
            groupedObjects.add(groupedObject);
        }

        return groupedObjects;
    }

    public static <T> List<GroupedObject<T>> groupObjects(Collection<T> objects, Set<Group<T>> groups) {
        return groupObjects(objects, groups, 0);
    }

    private static class ProtoGroupedObject<T> {
        private T object;
        private HashMap<Group<T>, ProtoGroup<T>> centres;
        private HashMap<Group<T>, List<ProtoGroup<T>>> protoGroups;

        public ProtoGroupedObject(T object) {
            this.object = object;
            this.centres = new HashMap<Group<T>, ProtoGroup<T>>();
            this.protoGroups = new HashMap<Group<T>, List<ProtoGroup<T>>>();
        }


        public boolean inGroup(Group<T> group) {
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
