package com.froobworld.saml.utils;

import org.bukkit.entity.LivingEntity;

public class EntityFreezer {

    public static void freezeEntity(LivingEntity entity) {
        entity.setAI(false);
    }

    public static void unfreezeEntity(LivingEntity entity) {
        entity.setAI(true);
    }

    public static boolean isFrozen(LivingEntity entity) {
        return !entity.hasAI();
    }
}
