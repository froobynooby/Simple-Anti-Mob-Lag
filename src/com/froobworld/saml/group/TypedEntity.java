package com.froobworld.saml.group;

import org.bukkit.entity.LivingEntity;

public class TypedEntity {
    private LivingEntity entity;
    private int typeId;

    public TypedEntity(LivingEntity entity, int typeId) {
        this.entity = entity;
        this.typeId = typeId;
    }


    public LivingEntity getEntity() {
        return entity;
    }

    public int getTypeId() {
        return typeId;
    }

}
