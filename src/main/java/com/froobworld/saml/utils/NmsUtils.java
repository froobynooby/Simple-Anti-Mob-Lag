package com.froobworld.saml.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class NmsUtils {
    private static final String NAME = Bukkit.getServer().getClass().getPackage().getName();
    public static final String VERSION = NAME.substring(NAME.lastIndexOf('.') + 1);

    private static Object serverInstance = null;
    private static Field tpsField = null;

    private static Class<?> getNMSClass(String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + VERSION + "." + className);
    }

    public static Double getTPS() {
        try {
            if(serverInstance == null || tpsField == null) {
                serverInstance = getNMSClass("MinecraftServer").getMethod("getServer").invoke(null);
                tpsField = serverInstance.getClass().getField("recentTps");
            }

            double[] tps = ((double[]) tpsField.get(serverInstance));

            return tps[0];
        } catch (IllegalAccessException | NoSuchMethodException | NoSuchFieldException | ClassNotFoundException | InvocationTargetException e) {
            return null;
        }
    }
}
