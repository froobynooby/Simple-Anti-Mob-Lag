package com.froobworld.saml.group;

public interface Group<T> {
    public String getName();
    public GroupMetadata getGroupMetadata();
    public MembershipEligibility getMembershipEligibility(T candidate);
    public GroupStatusUpdater<T> groupStatusUpdater();

    public static enum MembershipEligibility {
        CENTRE,
        MEMBER,
        CENTRE_OR_MEMBER,
        NONE
    }

    public static enum ProtoMemberStatus {
        MEMBER,
        NON_MEMBER,
        CONDITIONAL;
    }
}
