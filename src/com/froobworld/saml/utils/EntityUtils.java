package com.froobworld.saml.utils;

import org.bukkit.entity.*;

import java.util.HashSet;
import java.util.Set;

public class EntityUtils {

    public static Set<String> getTypeIdentifiers(LivingEntity entity) {
        Set<String> identifiers = new HashSet<>();
        identifiers.add(entity.getType().name());
        if(entity instanceof Ambient) {
            identifiers.add("ambient");
        }
        if(entity instanceof Animals) {
            identifiers.add("animal");
        }
        if(entity instanceof Fish) {
            identifiers.add("fish");
        }
        if(entity instanceof Flying) {
            identifiers.add("flying");
        }
        if(entity instanceof Monster) {
            identifiers.add("monster");
        }
        if(entity instanceof Raider) {
            identifiers.add("raider");
        }
        if(entity instanceof WaterMob) {
            identifiers.add("watermob");
        }

        return identifiers;
    }

}
