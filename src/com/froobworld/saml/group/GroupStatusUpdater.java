package com.froobworld.saml.group;

public interface GroupStatusUpdater {
    public void updateStatus(TypedEntity typedEntity);
    public boolean isGroup();
}
