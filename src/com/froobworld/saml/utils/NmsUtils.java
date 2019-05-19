package com.froobworld.saml.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

public class NmsUtils {
    private final static String name = Bukkit.getServer().getClass().getPackage().getName();
    private final static String version = name.substring(name.lastIndexOf('.') + 1);
    private final static DecimalFormat format = new DecimalFormat("##.##");

    private static Object serverInstance = null;
    private static Field tpsField = null;

    private static Class<?> getNMSClass(String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + version + "." + className);
    }

    public static Double getTPS() {
        try {
            if(serverInstance == null || tpsField == null) {
                serverInstance = getNMSClass("MinecraftServer").getMethod("getServer").invoke(null);
                tpsField = serverInstance.getClass().getField("recentTps");
            }

            double[] tps = ((double[]) tpsField.get(serverInstance));
            return Double.valueOf(format.format(tps[0]));
        } catch (IllegalAccessException | NoSuchMethodException | NoSuchFieldException | ClassNotFoundException | InvocationTargetException e) {
            return null;
        }
    }
}
