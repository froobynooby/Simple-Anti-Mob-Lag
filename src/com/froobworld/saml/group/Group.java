package com.froobworld.saml.group;

public interface Group<T> {
    public String getName();
    public boolean inProtoGroup(T entity, ProtoGroup<T> protoGroup);
    public boolean canBeCentre(T candidate);
    public GroupStatusUpdater<T> groupStatusUpdater();
}
