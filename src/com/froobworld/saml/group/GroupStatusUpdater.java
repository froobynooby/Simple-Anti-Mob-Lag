package com.froobworld.saml.group;

public interface GroupStatusUpdater<T> {
    public Group.ProtoMemberStatus getProtoMemberStatus(T candidate, ProtoGroup<? extends T> protoGroup);
    public void updateStatus(T member);
    public boolean isGroup();

    public default Group.ProtoMemberStatus attemptUpdateStatus(T candidate, ProtoGroup<? extends T> protoGroup) {
        Group.ProtoMemberStatus protoMemberStatus = getProtoMemberStatus(candidate, protoGroup);
        if(protoMemberStatus != Group.ProtoMemberStatus.NON_MEMBER) {
            updateStatus(candidate);
        }
        return protoMemberStatus;
    }
}
