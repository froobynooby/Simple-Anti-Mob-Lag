package com.froobworld.saml.group;

import java.util.*;

public class ObjectGrouper {

    public static <T> List<GroupedObject<T>> groupObjects(Collection<T> objects, Set<? extends Group<? super T>> groups, long maxOperationTime) {
        long startTime = System.currentTimeMillis();
        List<ProtoGroupedObject<T>> protoGroupedObjects = new ArrayList<>(objects.size());

        for(T object : objects) {
            if(maxOperationTime != 0 && System.currentTimeMillis() - startTime >= maxOperationTime) {
                break;
            }
            ProtoGroupedObject<T> nextProtoGroupedObject = new ProtoGroupedObject<>(object);
            for(Group<? super T> group : groups) {
                nextProtoGroupedObject.protoGroups.put(group, new ArrayList<>());
                Group.MembershipEligibility nextMembershipEligibility = group.getMembershipEligibility(object);
                nextProtoGroupedObject.groupMembershipEligibility.put(group, nextMembershipEligibility);
                if(nextMembershipEligibility != Group.MembershipEligibility.NONE) {
                    ProtoGroup<T> nextProtoGroup = null;
                    if(nextMembershipEligibility == Group.MembershipEligibility.CENTRE || nextMembershipEligibility == Group.MembershipEligibility.CENTRE_OR_MEMBER) {
                        nextProtoGroup = new ProtoGroup<>(group, object);
                        nextProtoGroupedObject.centres.put(group, nextProtoGroup);
                    }

                    for(ProtoGroupedObject<T> otherProtoGroupedObject : protoGroupedObjects) {
                        Group.MembershipEligibility otherMembershipEligibility = otherProtoGroupedObject.groupMembershipEligibility.get(group);
                        if(otherMembershipEligibility != Group.MembershipEligibility.NONE) {
                            ProtoGroup<T> otherProtoGroup = otherProtoGroupedObject.centres.get(group);

                            if (otherProtoGroup != null && (nextMembershipEligibility == Group.MembershipEligibility.MEMBER || nextMembershipEligibility == Group.MembershipEligibility.CENTRE_OR_MEMBER)) {
                                Group.ProtoMemberStatus protoMemberStatus = otherProtoGroup.attemptAddMember(nextProtoGroupedObject.object);
                                if (protoMemberStatus == Group.ProtoMemberStatus.MEMBER) {
                                    nextProtoGroupedObject.protoGroups.get(group).add(otherProtoGroup);
                                }
                            }
                            if (nextProtoGroup != null && (otherMembershipEligibility == Group.MembershipEligibility.MEMBER || otherMembershipEligibility == Group.MembershipEligibility.CENTRE_OR_MEMBER)) {
                                Group.ProtoMemberStatus protoMemberStatus = nextProtoGroup.attemptAddMember(otherProtoGroupedObject.object);
                                if (protoMemberStatus == Group.ProtoMemberStatus.MEMBER) {
                                    otherProtoGroupedObject.protoGroups.get(group).add(nextProtoGroup);
                                }
                            }
                        }
                    }
                }
            }
            protoGroupedObjects.add(nextProtoGroupedObject);
        }

        ArrayList<GroupedObject<T>> groupedObjects = new ArrayList<>(objects.size());
        for(ProtoGroupedObject<T> protoGroupedObject : protoGroupedObjects) {
            GroupedObject<T> groupedObject = new GroupedObject<>(protoGroupedObject.object);
            for(Group<? super T> group : groups) {
                if(protoGroupedObject.inGroup(group)) {
                    groupedObject.getGroups().add(group);
                }
            }
            groupedObjects.add(groupedObject);
        }

        return groupedObjects;
    }

    public static <T> List<GroupedObject<T>> groupObjects(Collection<T> objects, Set<? extends Group<? super T>> groups) {
        return groupObjects(objects, groups, 0);
    }

    private static class ProtoGroupedObject<T> {
        private T object;
        private HashMap<Group<? super T>, Group.MembershipEligibility> groupMembershipEligibility;
        private HashMap<Group<? super T>, ProtoGroup<T>> centres;
        private HashMap<Group<? super T>, List<ProtoGroup<T>>> protoGroups;

        public ProtoGroupedObject(T object) {
            this.object = object;
            this.groupMembershipEligibility = new HashMap<>();
            this.centres = new HashMap<>();
            this.protoGroups = new HashMap<>();
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
