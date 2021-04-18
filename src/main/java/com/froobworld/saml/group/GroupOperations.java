package com.froobworld.saml.group;

public class GroupOperations {

    public static <T> Group<T> conjunction(String resultName, Group<T> group1, Group<T> group2) {
        return new Group<T>() {
            private final GroupMetadata groupMetadata = new GroupMetadata.Builder()
                    .setVolatile(group1.getGroupMetadata().isVolatile() || group2.getGroupMetadata().isVolatile())
                    .setRestrictsEligibility(group1.getGroupMetadata().restrictsEligibility() || group2.getGroupMetadata().restrictsEligibility())
                    .setRestrictsMemberStatus(group1.getGroupMetadata().restrictsMemberStatus() || group2.getGroupMetadata().restrictsMemberStatus())
                    .setRestrictsGroupStatus(group1.getGroupMetadata().restrictsGroupStatus() || group2.getGroupMetadata().restrictsGroupStatus())
                    .build();

            @Override
            public String getName() {
                return resultName;
            }

            @Override
            public GroupMetadata getGroupMetadata() {
                return groupMetadata;
            }

            @Override
            public MembershipEligibility getMembershipEligibility(T candidate) {
                MembershipEligibility membershipEligibility1 = group1.getMembershipEligibility(candidate);
                MembershipEligibility membershipEligibility2 = group2.getMembershipEligibility(candidate);
                if(membershipEligibility1 == MembershipEligibility.CENTRE_OR_MEMBER) {
                    if(membershipEligibility2 == MembershipEligibility.CENTRE_OR_MEMBER) {
                        return MembershipEligibility.CENTRE_OR_MEMBER;
                    } else if(membershipEligibility2 == MembershipEligibility.CENTRE) {
                        return MembershipEligibility.CENTRE;
                    } else if(membershipEligibility2 == MembershipEligibility.MEMBER) {
                        return MembershipEligibility.MEMBER;
                    } else {
                        return MembershipEligibility.NONE;
                    }
                }
                if(membershipEligibility1 == MembershipEligibility.CENTRE) {
                    if(membershipEligibility2 == MembershipEligibility.CENTRE_OR_MEMBER) {
                        return MembershipEligibility.CENTRE;
                    } else if(membershipEligibility2 == MembershipEligibility.CENTRE) {
                        return MembershipEligibility.CENTRE;
                    } else if(membershipEligibility2 == MembershipEligibility.MEMBER) {
                        return MembershipEligibility.NONE;
                    } else {
                        return MembershipEligibility.NONE;
                    }
                }
                if(membershipEligibility1 == MembershipEligibility.MEMBER) {
                    if(membershipEligibility2 == MembershipEligibility.CENTRE_OR_MEMBER) {
                        return MembershipEligibility.MEMBER;
                    } else if(membershipEligibility2 == MembershipEligibility.CENTRE) {
                        return MembershipEligibility.NONE;
                    } else if(membershipEligibility2 == MembershipEligibility.MEMBER) {
                        return MembershipEligibility.MEMBER;
                    } else {
                        return MembershipEligibility.NONE;
                    }
                }

                return MembershipEligibility.NONE;
            }

            @Override
            public GroupStatusUpdater<T> groupStatusUpdater() {
                GroupStatusUpdater<T> groupStatusUpdater1 = group1.groupStatusUpdater();
                GroupStatusUpdater<T> groupStatusUpdater2 = group2.groupStatusUpdater();
                return new GroupStatusUpdater<T>() {

                    @Override
                    public ProtoMemberStatus getProtoMemberStatus(T candidate, ProtoGroup<? extends T> protoGroup) {
                        ProtoMemberStatus protoMemberStatus1 = groupStatusUpdater1.getProtoMemberStatus(candidate, protoGroup);
                        ProtoMemberStatus protoMemberStatus2 = groupStatusUpdater2.getProtoMemberStatus(candidate, protoGroup);

                        if(protoMemberStatus1 == ProtoMemberStatus.NON_MEMBER || protoMemberStatus2 == ProtoMemberStatus.NON_MEMBER) {
                            return ProtoMemberStatus.NON_MEMBER;
                        }

                        return (protoMemberStatus1 == ProtoMemberStatus.CONDITIONAL && protoMemberStatus2 == ProtoMemberStatus.CONDITIONAL) ? ProtoMemberStatus.CONDITIONAL : ProtoMemberStatus.MEMBER;
                    }

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
            private final GroupMetadata groupMetadata = new GroupMetadata.Builder()
                    .setVolatile(group1.getGroupMetadata().isVolatile() || group2.getGroupMetadata().isVolatile())
                    .setRestrictsEligibility(group1.getGroupMetadata().restrictsEligibility() && group2.getGroupMetadata().restrictsEligibility())
                    .setRestrictsMemberStatus(group1.getGroupMetadata().restrictsMemberStatus() || group2.getGroupMetadata().restrictsMemberStatus())
                    .setRestrictsGroupStatus(group1.getGroupMetadata().restrictsGroupStatus() || group2.getGroupMetadata().restrictsGroupStatus())
                    .build();

            @Override
            public String getName() {
                return resultName;
            }

            @Override
            public GroupMetadata getGroupMetadata() {
                return groupMetadata;
            }

            @Override
            public MembershipEligibility getMembershipEligibility(T candidate) {
                MembershipEligibility membershipEligibility1 = group1.getMembershipEligibility(candidate);
                MembershipEligibility membershipEligibility2 = group2.getMembershipEligibility(candidate);

                if(membershipEligibility1 == MembershipEligibility.CENTRE_OR_MEMBER) {
                    if(membershipEligibility2 == MembershipEligibility.CENTRE_OR_MEMBER) {
                        return MembershipEligibility.CENTRE_OR_MEMBER;
                    } else if(membershipEligibility2 == MembershipEligibility.CENTRE) {
                        return MembershipEligibility.CENTRE_OR_MEMBER;
                    } else if(membershipEligibility2 == MembershipEligibility.MEMBER) {
                        return MembershipEligibility.MEMBER;
                    } else {
                        return MembershipEligibility.MEMBER;
                    }
                }
                if(membershipEligibility1 == MembershipEligibility.CENTRE) {
                    if(membershipEligibility2 == MembershipEligibility.CENTRE_OR_MEMBER) {
                        return MembershipEligibility.CENTRE_OR_MEMBER;
                    } else if(membershipEligibility2 == MembershipEligibility.CENTRE) {
                        return MembershipEligibility.CENTRE;
                    } else if(membershipEligibility2 == MembershipEligibility.MEMBER) {
                        return MembershipEligibility.MEMBER;
                    } else {
                        return MembershipEligibility.NONE;
                    }
                }
                if(membershipEligibility1 == MembershipEligibility.MEMBER) {
                    if(membershipEligibility2 == MembershipEligibility.CENTRE_OR_MEMBER) {
                        return MembershipEligibility.MEMBER;
                    } else if(membershipEligibility2 == MembershipEligibility.CENTRE) {
                        return MembershipEligibility.MEMBER;
                    } else if(membershipEligibility2 == MembershipEligibility.MEMBER) {
                        return MembershipEligibility.MEMBER;
                    } else {
                        return MembershipEligibility.MEMBER;
                    }
                }
                if(membershipEligibility1 == MembershipEligibility.NONE) {
                    if(membershipEligibility2 == MembershipEligibility.CENTRE_OR_MEMBER) {
                        return MembershipEligibility.MEMBER;
                    } else if(membershipEligibility2 == MembershipEligibility.CENTRE) {
                        return MembershipEligibility.NONE;
                    } else if(membershipEligibility2 == MembershipEligibility.MEMBER) {
                        return MembershipEligibility.MEMBER;
                    } else {
                        return MembershipEligibility.NONE;
                    }
                }

                return MembershipEligibility.NONE;
            }

            @Override
            public GroupStatusUpdater<T> groupStatusUpdater() {
                GroupStatusUpdater<T> groupStatusUpdater1 = group1.groupStatusUpdater();
                GroupStatusUpdater<T> groupStatusUpdater2 = group2.groupStatusUpdater();
                return new GroupStatusUpdater<T>() {
                    @Override
                    public ProtoMemberStatus getProtoMemberStatus(T candidate, ProtoGroup<? extends T> protoGroup) {
                        ProtoMemberStatus protoMemberStatus1 = groupStatusUpdater1.getProtoMemberStatus(candidate, protoGroup);
                        ProtoMemberStatus protoMemberStatus2 = groupStatusUpdater2.getProtoMemberStatus(candidate, protoGroup);
                        if(protoMemberStatus1 == ProtoMemberStatus.NON_MEMBER && protoMemberStatus2 == ProtoMemberStatus.NON_MEMBER) {
                            return ProtoMemberStatus.NON_MEMBER;
                        }

                        return (protoMemberStatus1 == ProtoMemberStatus.MEMBER || protoMemberStatus2 == ProtoMemberStatus.MEMBER) ? ProtoMemberStatus.MEMBER : ProtoMemberStatus.CONDITIONAL;
                    }

                    @Override
                    public void updateStatus(T member) {
                        groupStatusUpdater1.updateStatus(member);
                        groupStatusUpdater2.updateStatus(member);
                    }

                    @Override
                    public ProtoMemberStatus attemptUpdateStatus(T candidate, ProtoGroup<? extends T> protoGroup) {
                        ProtoMemberStatus protoMemberStatus1 = groupStatusUpdater1.getProtoMemberStatus(candidate, protoGroup);
                        ProtoMemberStatus protoMemberStatus2 = groupStatusUpdater2.getProtoMemberStatus(candidate, protoGroup);
                        if(protoMemberStatus1 == ProtoMemberStatus.NON_MEMBER && protoMemberStatus2 == ProtoMemberStatus.NON_MEMBER) {
                            return ProtoMemberStatus.NON_MEMBER;
                        }
                        if(protoMemberStatus1 != ProtoMemberStatus.NON_MEMBER) {
                            groupStatusUpdater1.updateStatus(candidate);
                        }
                        if(protoMemberStatus2 != ProtoMemberStatus.NON_MEMBER) {
                            groupStatusUpdater2.updateStatus(candidate);
                        }

                        return (protoMemberStatus1 == ProtoMemberStatus.MEMBER || protoMemberStatus2 == ProtoMemberStatus.MEMBER) ? ProtoMemberStatus.MEMBER : ProtoMemberStatus.CONDITIONAL;
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
            private final GroupMetadata groupMetadata = new GroupMetadata.Builder()
                    .setVolatile(group1.getGroupMetadata().isVolatile() || group2.getGroupMetadata().isVolatile())
                    .setRestrictsEligibility(group1.getGroupMetadata().restrictsEligibility() && group2.getGroupMetadata().restrictsEligibility())
                    .setRestrictsMemberStatus(group1.getGroupMetadata().restrictsMemberStatus() && group2.getGroupMetadata().restrictsMemberStatus())
                    .setRestrictsGroupStatus(group1.getGroupMetadata().restrictsGroupStatus() && group2.getGroupMetadata().restrictsGroupStatus())
                    .build();

            @Override
            public String getName() {
                return resultName;
            }

            @Override
            public GroupMetadata getGroupMetadata() {
                return groupMetadata;
            }

            @Override
            public MembershipEligibility getMembershipEligibility(T candidate) {
                MembershipEligibility membershipEligibility1 = group1.getMembershipEligibility(candidate);
                MembershipEligibility membershipEligibility2 = group2.getMembershipEligibility(candidate);
                if(membershipEligibility1 == MembershipEligibility.CENTRE_OR_MEMBER) {
                    if(membershipEligibility2 == MembershipEligibility.CENTRE_OR_MEMBER) {
                        return MembershipEligibility.CENTRE_OR_MEMBER;
                    } else if(membershipEligibility2 == MembershipEligibility.CENTRE) {
                        return MembershipEligibility.CENTRE_OR_MEMBER;
                    } else if(membershipEligibility2 == MembershipEligibility.MEMBER) {
                        return MembershipEligibility.CENTRE_OR_MEMBER;
                    } else {
                        return MembershipEligibility.CENTRE_OR_MEMBER;
                    }
                }
                if(membershipEligibility1 == MembershipEligibility.CENTRE) {
                    if(membershipEligibility2 == MembershipEligibility.CENTRE_OR_MEMBER) {
                        return MembershipEligibility.CENTRE_OR_MEMBER;
                    } else if(membershipEligibility2 == MembershipEligibility.CENTRE) {
                        return MembershipEligibility.CENTRE;
                    } else if(membershipEligibility2 == MembershipEligibility.MEMBER) {
                        return MembershipEligibility.CENTRE_OR_MEMBER;
                    } else {
                        return MembershipEligibility.CENTRE;
                    }
                }
                if(membershipEligibility1 == MembershipEligibility.MEMBER) {
                    if(membershipEligibility2 == MembershipEligibility.CENTRE_OR_MEMBER) {
                        return MembershipEligibility.CENTRE_OR_MEMBER;
                    } else if(membershipEligibility2 == MembershipEligibility.CENTRE) {
                        return MembershipEligibility.CENTRE_OR_MEMBER;
                    } else if(membershipEligibility2 == MembershipEligibility.MEMBER) {
                        return MembershipEligibility.MEMBER;
                    } else {
                        return MembershipEligibility.MEMBER;
                    }
                }
                if(membershipEligibility1 == MembershipEligibility.NONE) {
                    if(membershipEligibility2 == MembershipEligibility.CENTRE_OR_MEMBER) {
                        return MembershipEligibility.CENTRE_OR_MEMBER;
                    } else if(membershipEligibility2 == MembershipEligibility.CENTRE) {
                        return MembershipEligibility.CENTRE;
                    } else if(membershipEligibility2 == MembershipEligibility.MEMBER) {
                        return MembershipEligibility.MEMBER;
                    } else {
                        return MembershipEligibility.NONE;
                    }
                }

                return MembershipEligibility.NONE;
            }

            @Override
            public GroupStatusUpdater<T> groupStatusUpdater() {
                GroupStatusUpdater<T> groupStatusUpdater1 = group1.groupStatusUpdater();
                GroupStatusUpdater<T> groupStatusUpdater2 = group2.groupStatusUpdater();
                return new GroupStatusUpdater<T>() {
                    @Override
                    public ProtoMemberStatus getProtoMemberStatus(T candidate, ProtoGroup<? extends T> protoGroup) {
                        ProtoMemberStatus protoMemberStatus1 = groupStatusUpdater1.getProtoMemberStatus(candidate, protoGroup);
                        ProtoMemberStatus protoMemberStatus2 = groupStatusUpdater2.getProtoMemberStatus(candidate, protoGroup);
                        if(protoMemberStatus1 == ProtoMemberStatus.NON_MEMBER && protoMemberStatus2 == ProtoMemberStatus.NON_MEMBER) {
                            return ProtoMemberStatus.NON_MEMBER;
                        }

                        return (protoMemberStatus1 == ProtoMemberStatus.MEMBER || protoMemberStatus2 == ProtoMemberStatus.MEMBER) ? ProtoMemberStatus.MEMBER : ProtoMemberStatus.CONDITIONAL;
                    }

                    @Override
                    public void updateStatus(T member) {
                        groupStatusUpdater1.updateStatus(member);
                        groupStatusUpdater2.updateStatus(member);
                    }

                    @Override
                    public ProtoMemberStatus attemptUpdateStatus(T candidate, ProtoGroup<? extends T> protoGroup) {
                        ProtoMemberStatus protoMemberStatus1 = groupStatusUpdater1.getProtoMemberStatus(candidate, protoGroup);
                        ProtoMemberStatus protoMemberStatus2 = groupStatusUpdater2.getProtoMemberStatus(candidate, protoGroup);
                        if(protoMemberStatus1 == ProtoMemberStatus.NON_MEMBER && protoMemberStatus2 == ProtoMemberStatus.NON_MEMBER) {
                            return ProtoMemberStatus.NON_MEMBER;
                        }
                        if(protoMemberStatus1 != ProtoMemberStatus.NON_MEMBER) {
                            groupStatusUpdater1.updateStatus(candidate);
                        }
                        if(protoMemberStatus2 != ProtoMemberStatus.NON_MEMBER) {
                            groupStatusUpdater2.updateStatus(candidate);
                        }

                        return (protoMemberStatus1 == ProtoMemberStatus.MEMBER || protoMemberStatus2 == ProtoMemberStatus.MEMBER) ? ProtoMemberStatus.MEMBER : ProtoMemberStatus.CONDITIONAL;
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
