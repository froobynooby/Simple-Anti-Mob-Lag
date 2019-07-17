package com.froobworld.saml.group;

import java.util.*;

public class ProtoGroup<T> {
    private GroupStatusUpdater<? super T> groupStatusUpdater;
    private T centre;
    private List<T> members;

    public ProtoGroup(Group<? super T> group, T centre) {
        this.groupStatusUpdater = group.groupStatusUpdater();
        this.centre = centre;
        this.members = new ArrayList<T>();

        members.add(centre);
        groupStatusUpdater.updateStatus(centre);
    }

    public void addMember(T member) {
        members.add(member);
        groupStatusUpdater.updateStatus(member);
    }

    public T getCentre() {
        return centre;
    }

    public boolean isGroup() {
        return groupStatusUpdater.isGroup();
    }

    public int size() {
        return members.size();
    }

    public Iterator<T> membersIterator() {
        return members.iterator();
    }

}
