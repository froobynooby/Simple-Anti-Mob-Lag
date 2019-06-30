package com.froobworld.saml.group;

import org.bukkit.entity.LivingEntity;

public interface GroupStatusUpdater {
    public void updateStatus(LivingEntity entity);
    public boolean isGroup();
}
