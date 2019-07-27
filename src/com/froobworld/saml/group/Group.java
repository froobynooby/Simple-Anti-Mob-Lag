package com.froobworld.saml.group;

public interface Group<T> {
    public String getName();
    public ProtoMemberStatus inProtoGroup(T entity, ProtoGroup<? extends T> protoGroup);
    public boolean canBeMember(T candidate);
    public GroupStatusUpdater<T> groupStatusUpdater();

    public static enum ProtoMemberStatus {
        MEMBER,
        NON_MEMBER,
        CONDITIONAL;
    }
}
