package com.froobworld.saml.group;

public class GroupOperations {

    public static <T> Group<T> conjunction(String resultName, Group<T> group1, Group<T> group2) {
        return new Group<T>() {
            @Override
            public String getName() {
                return resultName;
            }

            @Override
            public boolean inProtoGroup(T candidate, ProtoGroup<T> protoGroup) {
                return group1.inProtoGroup(candidate, protoGroup) && group2.inProtoGroup(candidate, protoGroup);
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
            public boolean inProtoGroup(T candidate, ProtoGroup<T> protoGroup) {
                in1 = false;
                in2 = false;
                if(group1.inProtoGroup(candidate, protoGroup)) {
                    in1 = true;
                }
                if(group2.inProtoGroup(candidate, protoGroup)) {
                    in2 = true;
                }
                return in1 || in2;
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
            public boolean inProtoGroup(T candidate, ProtoGroup<T> protoGroup) {
                in1 = false;
                in2 = false;
                if(group1.canBeMember(protoGroup.getCentre()) && group1.inProtoGroup(candidate, protoGroup)) {
                    in1 = true;
                }
                if(group2.canBeMember(protoGroup.getCentre()) && group2.inProtoGroup(candidate, protoGroup)) {
                    in2 = true;
                }
                return in1 || in2;
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
