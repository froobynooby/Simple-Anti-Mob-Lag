package com.froobworld.saml.group;

import org.bukkit.entity.LivingEntity;

public interface Group {
    public String getName();
    public boolean inProtoGroup(LivingEntity entity, ProtoGroup protoGroup);
    public boolean canBeCentre(LivingEntity entity);
    public GroupStatusUpdater groupStatusUpdater();
}
