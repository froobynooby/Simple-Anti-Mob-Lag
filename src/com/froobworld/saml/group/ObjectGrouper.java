package com.froobworld.saml.group;

import java.util.*;

public class ObjectGrouper {

    public static <T> List<GroupedObject<T>> groupObjects(Collection<T> objects, Set<? extends Group<? super T>> groups, long maxOperationTime) {
        long startTime = System.currentTimeMillis();
        HashMap<T, List<ProtoGroupedObject<T>>> protoGroupedObjects = new HashMap<>();

        for(Group<? super T> group : groups) {
            boolean isVolatile = group.getGroupMetadata().isVolatile();
            List<ProtoGroupedObject<T>> groupProtoGroupedObjects = new ArrayList<>();
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
                        nextProtoGroupedObject.centreGroup = new ProtoGroup<T>(group, object);
                    }

                    for(ProtoGroupedObject<T> otherProtoGroupedObject : groupProtoGroupedObjects) {
                        ProtoGroup<T> nextCentreGroup = nextProtoGroupedObject.centreGroup;
                        ProtoGroup<T> otherCentreGroup = otherProtoGroupedObject.centreGroup;
                        boolean nextIsDefiniteGroup = !isVolatile && nextCentreGroup != null && nextCentreGroup.isGroup();
                        boolean otherIsDefiniteGroup = !isVolatile && otherCentreGroup != null && otherCentreGroup.isGroup();
                        if(nextIsDefiniteGroup && otherIsDefiniteGroup) {
                            continue;
                        }

                        if(!(nextIsDefiniteGroup && otherProtoGroupedObject.quickMemberStatus)) {
                            if (nextCentreGroup != null && (otherProtoGroupedObject.membershipEligibility == Group.MembershipEligibility.MEMBER || otherProtoGroupedObject.membershipEligibility == Group.MembershipEligibility.CENTRE_OR_MEMBER)) {
                                Group.ProtoMemberStatus protoMemberStatus = nextCentreGroup.attemptAddMember(otherProtoGroupedObject.object);
                                if (protoMemberStatus == Group.ProtoMemberStatus.MEMBER) {
                                    otherProtoGroupedObject.memberGroups.add(nextCentreGroup);
                                    if (nextIsDefiniteGroup) {
                                        otherProtoGroupedObject.quickMemberStatus = true;
                                    }
                                }
                            }
                        }
                        if(!(otherIsDefiniteGroup && nextProtoGroupedObject.quickMemberStatus)) {
                            if (otherCentreGroup != null && (nextProtoGroupedObject.membershipEligibility == Group.MembershipEligibility.MEMBER || nextProtoGroupedObject.membershipEligibility == Group.MembershipEligibility.CENTRE_OR_MEMBER)) {
                                Group.ProtoMemberStatus protoMemberStatus = otherCentreGroup.attemptAddMember(nextProtoGroupedObject.object);
                                if (protoMemberStatus == Group.ProtoMemberStatus.MEMBER) {
                                    nextProtoGroupedObject.memberGroups.add(otherCentreGroup);
                                    if (otherIsDefiniteGroup) {
                                        nextProtoGroupedObject.quickMemberStatus = true;
                                    }
                                }
                            }
                        }
                    }

                    protoGroupedObjectsList.add(nextProtoGroupedObject);
                    groupProtoGroupedObjects.add(nextProtoGroupedObject);
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
            groupedObjects.add(new GroupedObject<T>(entry.getKey(), objectGroups));
        }

        return groupedObjects;
    }

    public static <T> List<GroupedObject<T>> groupObjects(Collection<T> objects, Set<? extends Group<? super T>> groups) {
        return groupObjects(objects, groups, 0);
    }

    private static class ProtoGroupedObject<T> {
        private T object;
        private Group<? super T> group;
        private Group.MembershipEligibility membershipEligibility;
        private ProtoGroup<T> centreGroup;
        private List<ProtoGroup<T>> memberGroups;
        private boolean quickMemberStatus;

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

    }

}
