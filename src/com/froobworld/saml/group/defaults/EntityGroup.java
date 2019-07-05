package com.froobworld.saml.group.defaults;

import com.froobworld.saml.group.Group;
import com.froobworld.saml.utils.SnapshotEntity;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public interface EntityGroup extends Group<SnapshotEntity> {
    public Map<String, Object> getSnapshotProperties(LivingEntity entity);
}
