package com.froobworld.saml.group;

public class GroupModifiers {

    public static <T> Group<T> conditionalise(String resultName, Group<T> group) {
        return new Group<T>() {
            @Override
            public String getName() {
                return resultName;
            }

            @Override
            public GroupMetadata getGroupMetadata() {
                return group.getGroupMetadata();
            }

            @Override
            public MembershipEligibility getMembershipEligibility(T candidate) {
                return group.getMembershipEligibility(candidate);
            }

            @Override
            public GroupStatusUpdater<T> groupStatusUpdater() {
                GroupStatusUpdater<T> groupStatusUpdater = group.groupStatusUpdater();
                return new GroupStatusUpdater<T>() {
                    @Override
                    public ProtoMemberStatus getProtoMemberStatus(T candidate, ProtoGroup<? extends T> protoGroup) {
                        ProtoMemberStatus protoMemberStatus = groupStatusUpdater.getProtoMemberStatus(candidate, protoGroup);
                        return protoMemberStatus == ProtoMemberStatus.MEMBER ? ProtoMemberStatus.CONDITIONAL : protoMemberStatus;
                    }

                    @Override
                    public void updateStatus(T member) {
                        groupStatusUpdater.updateStatus(member);
                    }

                    @Override
                    public ProtoMemberStatus attemptUpdateStatus(T candidate, ProtoGroup<? extends T> protoGroup) {
                        ProtoMemberStatus protoMemberStatus = groupStatusUpdater.attemptUpdateStatus(candidate, protoGroup);
                        return protoMemberStatus == ProtoMemberStatus.MEMBER ? ProtoMemberStatus.CONDITIONAL : protoMemberStatus;
                    }

                    @Override
                    public boolean isGroup() {
                        return groupStatusUpdater.isGroup();
                    }
                };
            }
        };
    }

    public static <T> Group<T> negateStatus(String resultName, Group<T> group) {
        return new Group<T>() {
            private final GroupMetadata metadata = new GroupMetadata.Builder()
                    .setRestrictsEligibility(true)
                    .setRestrictsMemberStatus(group.getGroupMetadata().restrictsMemberStatus())
                    .setRestrictsGroupStatus(group.getGroupMetadata().restrictsGroupStatus())
                    .setVolatile(true)
                    .build();

            @Override
            public String getName() {
                return resultName;
            }

            @Override
            public GroupMetadata getGroupMetadata() {
                return metadata;
            }

            @Override
            public MembershipEligibility getMembershipEligibility(T candidate) {
                return group.getMembershipEligibility(candidate);
            }

            @Override
            public GroupStatusUpdater<T> groupStatusUpdater() {
                GroupStatusUpdater<T> groupStatusUpdater = group.groupStatusUpdater();
                return new GroupStatusUpdater<T>() {
                    @Override
                    public ProtoMemberStatus getProtoMemberStatus(T candidate, ProtoGroup<? extends T> protoGroup) {
                        return groupStatusUpdater.getProtoMemberStatus(candidate, protoGroup);
                    }

                    @Override
                    public void updateStatus(T member) {
                        groupStatusUpdater.updateStatus(member);
                    }

                    @Override
                    public ProtoMemberStatus attemptUpdateStatus(T candidate, ProtoGroup<? extends T> protoGroup) {
                        return groupStatusUpdater.attemptUpdateStatus(candidate, protoGroup);
                    }

                    @Override
                    public boolean isGroup() {
                        return !groupStatusUpdater.isGroup();
                    }
                };
            }
        };
    }

    public static <T> Group<T> negateEligibility(String resultName, Group<T> group) {
        return new Group<T>() {
            private final GroupMetadata metadata = new GroupMetadata.Builder()
                    .setRestrictsEligibility(true)
                    .setRestrictsMemberStatus(group.getGroupMetadata().restrictsMemberStatus())
                    .setRestrictsGroupStatus(group.getGroupMetadata().restrictsGroupStatus())
                    .setVolatile(group.getGroupMetadata().isVolatile())
                    .build();

            @Override
            public String getName() {
                return resultName;
            }

            @Override
            public GroupMetadata getGroupMetadata() {
                return metadata;
            }

            @Override
            public MembershipEligibility getMembershipEligibility(T candidate) {
                switch(group.getMembershipEligibility(candidate)) {
                    case CENTRE:
                        return MembershipEligibility.MEMBER;
                    case MEMBER:
                        return MembershipEligibility.CENTRE;
                    case CENTRE_OR_MEMBER:
                        return MembershipEligibility.NONE;
                    case NONE:
                        return MembershipEligibility.CENTRE_OR_MEMBER;
                }
                return null;
            }

            @Override
            public GroupStatusUpdater<T> groupStatusUpdater() {
                return group.groupStatusUpdater();
            }
        };
    }

    public static <T> Group<T> negateMemberStatus(String resultName, Group<T> group) {
        return new Group<T>() {
            private final GroupMetadata metadata = new GroupMetadata.Builder()
                    .setRestrictsGroupStatus(group.getGroupMetadata().restrictsGroupStatus())
                    .setRestrictsEligibility(group.getGroupMetadata().restrictsEligibility())
                    .setRestrictsMemberStatus(true)
                    .setVolatile(group.getGroupMetadata().isVolatile())
                    .build();

            @Override
            public String getName() {
                return resultName;
            }

            @Override
            public GroupMetadata getGroupMetadata() {
                return metadata;
            }

            @Override
            public MembershipEligibility getMembershipEligibility(T candidate) {
                return group.getMembershipEligibility(candidate);
            }

            @Override
            public GroupStatusUpdater<T> groupStatusUpdater() {
                GroupStatusUpdater<T> groupStatusUpdater = group.groupStatusUpdater();
                return new GroupStatusUpdater<T>() {
                    @Override
                    public ProtoMemberStatus getProtoMemberStatus(T candidate, ProtoGroup<? extends T> protoGroup) {
                        ProtoMemberStatus protoMemberStatus = groupStatusUpdater.getProtoMemberStatus(candidate, protoGroup);
                        return protoMemberStatus == ProtoMemberStatus.NON_MEMBER ? ProtoMemberStatus.MEMBER : ProtoMemberStatus.NON_MEMBER;
                    }

                    @Override
                    public void updateStatus(T member) {
                        groupStatusUpdater.updateStatus(member);
                    }

                    @Override
                    public boolean isGroup() {
                        return groupStatusUpdater.isGroup();
                    }
                };
            }
        };
    }

    public static <T> Group<T> negateMembers(String resultName, Group<T> group) {
        Group<T> result = group;
        if(group.getGroupMetadata().restrictsEligibility()) {
            group = negateEligibility(resultName, group);
        }
        if(group.getGroupMetadata().restrictsMemberStatus()) {
            group = negateMemberStatus(resultName, group);
        }

        return result;
    }

}
