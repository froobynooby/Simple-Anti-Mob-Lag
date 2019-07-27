package com.froobworld.saml.group;

public class GroupOperations {

    public static <T> Group<T> conjunction(String resultName, Group<T> group1, Group<T> group2) {
        return new Group<T>() {
            @Override
            public String getName() {
                return resultName;
            }

            @Override
            public ProtoMemberStatus inProtoGroup(T candidate, ProtoGroup<? extends T> protoGroup) {
                ProtoMemberStatus protoMemberStatus1 = group1.inProtoGroup(candidate, protoGroup);
                ProtoMemberStatus protoMemberStatus2 = group2.inProtoGroup(candidate, protoGroup);

                if(protoMemberStatus1 == ProtoMemberStatus.NON_MEMBER || protoMemberStatus2 == ProtoMemberStatus.NON_MEMBER) {
                    return ProtoMemberStatus.NON_MEMBER;
                }

                return (protoMemberStatus1 == ProtoMemberStatus.CONDITIONAL && protoMemberStatus2 == ProtoMemberStatus.CONDITIONAL) ? ProtoMemberStatus.CONDITIONAL : ProtoMemberStatus.MEMBER;
            }

            @Override
            public boolean canBeMember(T candidate) {
                return group1.canBeMember(candidate) && group2.canBeMember(candidate);
            }

            @Override
            public GroupStatusUpdater<T> groupStatusUpdater() {
                GroupStatusUpdater<T> groupStatusUpdater1 = group1.groupStatusUpdater();
                GroupStatusUpdater<T> groupStatusUpdater2 = group2.groupStatusUpdater();
                return new GroupStatusUpdater<T>() {
                    @Override
                    public void updateStatus(T member) {
                        groupStatusUpdater1.updateStatus(member);
                        groupStatusUpdater2.updateStatus(member);
                    }

                    @Override
                    public boolean isGroup() {
                        return groupStatusUpdater1.isGroup() && groupStatusUpdater2.isGroup();
                    }
                };
            }
        };
    }

    public static <T> Group<T> weakConjunction(String resultName, Group<T> group1, Group<T> group2) {
        return new Group<T>() {
            private boolean in1 = false;
            private boolean in2 = false;

            @Override
            public String getName() {
                return resultName;
            }

            @Override
            public ProtoMemberStatus inProtoGroup(T candidate, ProtoGroup<? extends T> protoGroup) {
                ProtoMemberStatus protoMemberStatus1 = group1.inProtoGroup(candidate, protoGroup);
                ProtoMemberStatus protoMemberStatus2 = group2.inProtoGroup(candidate, protoGroup);
                in1 = false;
                in2 = false;
                if(protoMemberStatus1 != ProtoMemberStatus.NON_MEMBER) {
                    in1 = true;
                }
                if(protoMemberStatus2 != ProtoMemberStatus.NON_MEMBER) {
                    in2 = true;
                }
                if(!in1 && !in2) {
                    return ProtoMemberStatus.NON_MEMBER;
                }

                return (protoMemberStatus1 == ProtoMemberStatus.MEMBER || protoMemberStatus2 == ProtoMemberStatus.MEMBER) ? ProtoMemberStatus.MEMBER : ProtoMemberStatus.CONDITIONAL;
            }

            @Override
            public boolean canBeMember(T candidate) {
                return group1.canBeMember(candidate) && group2.canBeMember(candidate);
            }

            @Override
            public GroupStatusUpdater<T> groupStatusUpdater() {
                GroupStatusUpdater<T> groupStatusUpdater1 = group1.groupStatusUpdater();
                GroupStatusUpdater<T> groupStatusUpdater2 = group2.groupStatusUpdater();
                return new GroupStatusUpdater<T>() {
                    @Override
                    public void updateStatus(T member) {
                        if(in1) {
                            groupStatusUpdater1.updateStatus(member);
                        }
                        if(in2) {
                            groupStatusUpdater2.updateStatus(member);
                        }
                    }

                    @Override
                    public boolean isGroup() {
                        return groupStatusUpdater1.isGroup() && groupStatusUpdater2.isGroup();
                    }
                };
            }
        };
    }

    public static <T> Group<T> disjunction(String resultName, Group<T> group1, Group<T> group2) {
        return new Group<T>() {
            private boolean in1 = false;
            private boolean in2 = false;

            @Override
            public String getName() {
                return resultName;
            }

            @Override
            public ProtoMemberStatus inProtoGroup(T candidate, ProtoGroup<? extends T> protoGroup) {
                ProtoMemberStatus protoMemberStatus1 = group1.inProtoGroup(candidate, protoGroup);
                ProtoMemberStatus protoMemberStatus2 = group2.inProtoGroup(candidate, protoGroup);
                in1 = false;
                in2 = false;
                if(protoMemberStatus1 != ProtoMemberStatus.NON_MEMBER) {
                    in1 = true;
                }
                if(protoMemberStatus2 != ProtoMemberStatus.NON_MEMBER) {
                    in2 = true;
                }
                if(!in1 && !in2) {
                    return ProtoMemberStatus.NON_MEMBER;
                }

                return (protoMemberStatus1 == ProtoMemberStatus.MEMBER || protoMemberStatus2 == ProtoMemberStatus.MEMBER) ? ProtoMemberStatus.MEMBER : ProtoMemberStatus.CONDITIONAL;
            }

            @Override
            public boolean canBeMember(T candidate) {
                return group1.canBeMember(candidate) || group2.canBeMember(candidate);
            }

            @Override
            public GroupStatusUpdater<T> groupStatusUpdater() {
                GroupStatusUpdater<T> groupStatusUpdater1 = group1.groupStatusUpdater();
                GroupStatusUpdater<T> groupStatusUpdater2 = group2.groupStatusUpdater();
                return new GroupStatusUpdater<T>() {
                    @Override
                    public void updateStatus(T member) {
                        if(in1) {
                            groupStatusUpdater1.updateStatus(member);
                        }
                        if(in2) {
                            groupStatusUpdater2.updateStatus(member);
                        }
                    }

                    @Override
                    public boolean isGroup() {
                        return groupStatusUpdater1.isGroup() || groupStatusUpdater2.isGroup();
                    }
                };
            }
        };
    }

}
