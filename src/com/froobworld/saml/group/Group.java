package com.froobworld.saml.group;

public interface Group<T> {
    public String getName();
    public boolean inProtoGroup(T entity, ProtoGroup<? extends T> protoGroup);
    public boolean canBeMember(T candidate);
    public GroupStatusUpdater<T> groupStatusUpdater();
}
