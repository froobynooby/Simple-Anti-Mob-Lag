package com.froobworld.saml.utils;

import com.froobworld.saml.Saml;
import com.froobworld.saml.data.FrozenEntityData;
import org.bukkit.entity.LivingEntity;

public class EntityFreezer {

    public static void freezeEntity(Saml saml, LivingEntity entity, FrozenEntityData frozenEntityData) {
        entity.setAI(false);
        if(CompatibilityUtils.PERSISTENT_DATA) {
            frozenEntityData.setAsFrozenEntityData(saml, entity);
        }
    }

    public static void unfreezeEntity(Saml saml, LivingEntity entity) {
        entity.setAI(true);
        if(CompatibilityUtils.PERSISTENT_DATA) {
            FrozenEntityData.stripOfFrozenEntityData(saml, entity);
        }
    }

    public static boolean isFrozen(LivingEntity entity) {
        return !entity.hasAI();
    }

    public static boolean isSamlFrozen(Saml saml, LivingEntity entity) {
        return !entity.hasAI() && (!CompatibilityUtils.PERSISTENT_DATA || FrozenEntityData.getFrozenEntityData(saml, entity).isPresent());
    }
}
