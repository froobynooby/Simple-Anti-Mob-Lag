package com.froobworld.saml.group;

import java.util.*;

public class ObjectGrouper {

    public static <T> List<GroupedObject<T>> groupObjects(Collection<T> objects, Set<? extends Group<? super T>> groups, long maxOperationTime) {
        long startTime = System.currentTimeMillis();
        HashMap<T, List<ProtoGroupedObject<T>>> protoGroupedObjects = new HashMap<>();

        for(Group<? super T> group : groups) {
            List<ProtoGroupedObject<T>> groupProtoGroupedObjects = new ArrayList<>();
            List<ProtoGroupedObject<T>> definiteGroupProtoGroupedObjects = new ArrayList<>();
            for(T object : objects) {
                if(maxOperationTime != 0 && System.currentTimeMillis() - startTime >= maxOperationTime) {
                    break;
                }
                List<ProtoGroupedObject<T>> protoGroupedObjectsList = protoGroupedObjects.computeIfAbsent(object, k -> new ArrayList<>());

                Group.MembershipEligibility nextMembershipEligibility = group.getMembershipEligibility(object);
                if(nextMembershipEligibility != Group.MembershipEligibility.NONE) {
                    ProtoGroupedObject<T> nextProtoGroupedObject = new ProtoGroupedObject<>(object);
                    nextProtoGroupedObject.group = group;
                    nextProtoGroupedObject.membershipEligibility = nextMembershipEligibility;

                    if(nextMembershipEligibility == Group.MembershipEligibility.CENTRE || nextMembershipEligibility == Group.MembershipEligibility.CENTRE_OR_MEMBER) {
                        nextProtoGroupedObject.centreGroup = new ProtoGroup<>(group, object);
                    }

                    ListIterator<ProtoGroupedObject<T>> iterator = definiteGroupProtoGroupedObjects.listIterator(definiteGroupProtoGroupedObjects.size());
                    while (iterator.hasPrevious()) {
                        ProtoGroupedObject<T> otherProtoGroupedObject = iterator.previous();
                        checkMembershipAndUpdate(nextProtoGroupedObject, otherProtoGroupedObject);
                        if(nextProtoGroupedObject.isDefiniteGroup()) {
                            break;
                        }
                    }
                    iterator = groupProtoGroupedObjects.listIterator(groupProtoGroupedObjects.size());
                    while (iterator.hasPrevious()) {
                        ProtoGroupedObject<T> otherProtoGroupedObject = iterator.previous();
                        checkMembershipAndUpdate(nextProtoGroupedObject, otherProtoGroupedObject);
                    }

                    protoGroupedObjectsList.add(nextProtoGroupedObject);
                    if(nextProtoGroupedObject.isDefiniteGroup()) {
                        definiteGroupProtoGroupedObjects.add(nextProtoGroupedObject);
                    } else {
                        groupProtoGroupedObjects.add(nextProtoGroupedObject);
                    }
                }
            }
        }

        ArrayList<GroupedObject<T>> groupedObjects = new ArrayList<>(objects.size());
        for(Map.Entry<T, List<ProtoGroupedObject<T>>> entry : protoGroupedObjects.entrySet()) {
            Set<Group<? super T>> objectGroups = new HashSet<>();
            for(ProtoGroupedObject<T> protoGroupedObject : entry.getValue()) {
                if(protoGroupedObject.inGroup()) {
                    objectGroups.add(protoGroupedObject.group);
                }
            }
            groupedObjects.add(new GroupedObject<>(entry.getKey(), objectGroups));
        }

        return groupedObjects;
    }

    public static <T> List<GroupedObject<T>> groupObjects(Collection<T> objects, Set<? extends Group<? super T>> groups) {
        return groupObjects(objects, groups, 0);
    }

    private static boolean isDefiniteGroup(ProtoGroupedObject protoGroupedObject) {
        return !protoGroupedObject.group.getGroupMetadata().isVolatile() && protoGroupedObject.centreGroup != null && protoGroupedObject.centreGroup.isGroup();
    }

    private static <T> void checkMembershipAndUpdate(ProtoGroupedObject<T> protoGroupedObject1, ProtoGroupedObject<T> protoGroupedObject2) {
        ProtoGroup<T> centreGroup1 = protoGroupedObject1.centreGroup;
        ProtoGroup<T> centreGroup2 = protoGroupedObject2.centreGroup;
        boolean isDefiniteGroup1 = protoGroupedObject1.isDefiniteGroup();
        boolean isDefiniteGroup2 = protoGroupedObject2.isDefiniteGroup();
        if(isDefiniteGroup1 && isDefiniteGroup2) {
            return;
        }
        if(!(isDefiniteGroup1 && protoGroupedObject2.quickMemberStatus)) {
            if (centreGroup1 != null && (protoGroupedObject2.membershipEligibility == Group.MembershipEligibility.MEMBER || protoGroupedObject2.membershipEligibility == Group.MembershipEligibility.CENTRE_OR_MEMBER)) {
                Group.ProtoMemberStatus protoMemberStatus = centreGroup1.attemptAddMember(protoGroupedObject2.object);
                if (protoMemberStatus == Group.ProtoMemberStatus.MEMBER) {
                    protoGroupedObject2.memberGroups.add(centreGroup1);
                    if (isDefiniteGroup1) {
                        protoGroupedObject2.quickMemberStatus = true;
                    }
                }
            }
        }
        if(!(isDefiniteGroup2 && protoGroupedObject1.quickMemberStatus)) {
            if (centreGroup2 != null && (protoGroupedObject1.membershipEligibility == Group.MembershipEligibility.MEMBER || protoGroupedObject1.membershipEligibility == Group.MembershipEligibility.CENTRE_OR_MEMBER)) {
                Group.ProtoMemberStatus protoMemberStatus = centreGroup2.attemptAddMember(protoGroupedObject1.object);
                if (protoMemberStatus == Group.ProtoMemberStatus.MEMBER) {
                    protoGroupedObject1.memberGroups.add(centreGroup2);
                    if (isDefiniteGroup2) {
                        protoGroupedObject1.quickMemberStatus = true;
                    }
                }
            }
        }
    }

    private static class ProtoGroupedObject<T> {
        private T object;
        private Group<? super T> group;
        private Group.MembershipEligibility membershipEligibility;
        private ProtoGroup<T> centreGroup;
        private List<ProtoGroup<T>> memberGroups;
        private boolean quickMemberStatus;
        private boolean definiteGroup;

        public ProtoGroupedObject(T object) {
            this.object = object;
            this.memberGroups = new ArrayList<>();
        }


        public boolean inGroup() {
            if(quickMemberStatus) {
                return true;
            }
            if(centreGroup != null && centreGroup.isGroup()) {
                return true;
            }
            for(ProtoGroup protoGroup : memberGroups) {
                if(protoGroup.isGroup()) {
                    return true;
                }
            }
            return false;
        }

        public boolean isDefiniteGroup() {
            if(definiteGroup) {
                return true;
            }
            if(ObjectGrouper.isDefiniteGroup(this)) {
                definiteGroup = true;
                return true;
            }

            return false;
        }

    }

}
