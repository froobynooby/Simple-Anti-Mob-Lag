package com.froobworld.saml.utils;

import java.util.function.Consumer;

public class CompatibilityUtils {
    public static final boolean ANIMAL_LOVE_MODE;
    public static final boolean USE_PAPER_GET_CHUNK_ASYNC;
    public static final boolean MOB_TARGET;
    public static final boolean PERSISTENT_DATA;
    public static final boolean FISH_EXIST;
    public static final boolean RAIDERS_EXIST;
    static {
        boolean loveModeMethodExists;
        try {
            org.bukkit.entity.Animals.class.getMethod("isLoveMode");
            loveModeMethodExists = true;
        } catch (NoSuchMethodException e) {
            loveModeMethodExists = false;
        }
        ANIMAL_LOVE_MODE = loveModeMethodExists;

        boolean asyncChunkGetMethodExists;
        try {
            org.bukkit.World.class.getMethod("getChunkAtAsync", int.class, int.class, Consumer.class);
            asyncChunkGetMethodExists = true;
        } catch (NoSuchMethodException e) {
            asyncChunkGetMethodExists = false;
        }
        USE_PAPER_GET_CHUNK_ASYNC = asyncChunkGetMethodExists;

        boolean mobClassExists;
        try {
            org.bukkit.entity.Mob.class.getClass();
            mobClassExists = true;
        } catch (NoClassDefFoundError e) {
            mobClassExists = false;
        }
        MOB_TARGET = mobClassExists;

        boolean persistentDataHolderClassExists;
        try {
            org.bukkit.persistence.PersistentDataHolder.class.getClass();
            persistentDataHolderClassExists = true;
        } catch (NoClassDefFoundError e) {
            persistentDataHolderClassExists = false;
        }
        PERSISTENT_DATA = persistentDataHolderClassExists;

        boolean fishExist;
        try {
            org.bukkit.entity.Fish.class.getClass();
            fishExist = true;
        } catch (NoClassDefFoundError e) {
            fishExist = false;
        }
        FISH_EXIST = fishExist;

        boolean raidersExist;
        try {
            org.bukkit.entity.Raider.class.getClass();
            raidersExist = true;
        } catch (NoClassDefFoundError e) {
            raidersExist = false;
        }
        RAIDERS_EXIST = raidersExist;
    }
}
