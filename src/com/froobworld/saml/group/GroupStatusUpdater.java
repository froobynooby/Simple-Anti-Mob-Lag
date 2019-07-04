package com.froobworld.saml.group;

public interface GroupStatusUpdater<T> {
    public void updateStatus(T member);
    public boolean isGroup();
}
