package com.froobworld.saml.group;

public class ProtoGroup<T> {
    private GroupStatusUpdater<? super T> groupStatusUpdater;
    private T centre;

    public ProtoGroup(Group<? super T> group, T centre) {
        this.groupStatusUpdater = group.groupStatusUpdater();
        this.centre = centre;

        groupStatusUpdater.updateStatus(centre);
    }

    public Group.ProtoMemberStatus attemptAddMember(T candidate) {
        return groupStatusUpdater.attemptUpdateStatus(candidate, this);
    }

    public T getCentre() {
        return centre;
    }

    public boolean isGroup() {
        return groupStatusUpdater.isGroup();
    }

}
