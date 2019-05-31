package com.froobworld.saml.utils;

import com.froobworld.saml.SamlConfiguration;

import java.util.function.Consumer;

public class CompatibilityUtils {
    private static final boolean ANIMAL_LOVE_MODE;
    private static final boolean USE_PAPER_GET_CHUNK_ASYNC;
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
    }

    public static boolean getIgnoreLoveModeOption(SamlConfiguration config) {
        return ANIMAL_LOVE_MODE && config.getBoolean("ignore-love-mode");
    }

    public static boolean getCanUsePaperAsyncChunkGet() {
        return USE_PAPER_GET_CHUNK_ASYNC;
    }
}
