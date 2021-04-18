package com.froobworld.saml.utils;

import com.froobworld.saml.Saml;
import com.froobworld.saml.data.FrozenEntityData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class EntityFreezer {

    public static void freezeEntity(Saml saml, LivingEntity entity, FrozenEntityData frozenEntityData) {
        entity.setAI(false);
        frozenEntityData.setAsFrozenEntityData(saml, entity);
    }

    public static void unfreezeEntity(Saml saml, LivingEntity entity) {
        entity.setAI(true);
        entity.setVelocity(new Vector());
        FrozenEntityData.stripOfFrozenEntityData(saml, entity);
    }

    public static boolean isFrozen(LivingEntity entity) {
        return !entity.hasAI() && !(entity instanceof Player) && !(entity instanceof ArmorStand);
    }

    public static boolean isSamlFrozen(Saml saml, LivingEntity entity) {
        return isFrozen(entity) && FrozenEntityData.getFrozenEntityData(saml, entity).isPresent();
    }
}
