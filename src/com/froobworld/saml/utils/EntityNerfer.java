package com.froobworld.saml.utils;

import com.froobworld.saml.Saml;
import com.froobworld.saml.data.NerfedEntityData;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class EntityNerfer {
    private static Class ENTITY_INSENTIENT_CLASS;
    private static Method LIVING_ENTITY_GET_HANDLE_METHOD;
    private static Field PATHFINDER_GOAL_SELECTOR_GOALS_FIELD;
    private static Field ENTITY_INSENTIENT_GOAL_SELECTOR_FIELD;
    private static Field ENTITY_INSENTIENT_TARGET_SELECTOR_FIELD;
    private static Field PATHFINDER_GOAL_WRAPPED_UNWRAPPED_FIELD;

    static {
        Map<String, String> PATHFINDER_GOAL_WRAPPED_CLASS_NAMES = new HashMap<>();
        String defaultPathfinderGoalWrappedClassName = "PathfinderGoalWrapped";
        PATHFINDER_GOAL_WRAPPED_CLASS_NAMES.put("v1_14_R1", "PathfinderGoalWrapped");
        PATHFINDER_GOAL_WRAPPED_CLASS_NAMES.put("v1_13_R2", "PathfinderGoalSelector.PathfinderGoalSelectorItem");
        PATHFINDER_GOAL_WRAPPED_CLASS_NAMES.put("v1_13_R1", "PathfinderGoalSelector.PathfinderGoalSelectorItem");
        PATHFINDER_GOAL_WRAPPED_CLASS_NAMES.put("v1_12_R1", "PathfinderGoalSelector.PathfinderGoalSelectorItem");
        PATHFINDER_GOAL_WRAPPED_CLASS_NAMES.put("v1_11_R1", "PathfinderGoalSelector.PathfinderGoalSelectorItem");
        PATHFINDER_GOAL_WRAPPED_CLASS_NAMES.put("v1_10_R1", "PathfinderGoalSelector.PathfinderGoalSelectorItem");
        PATHFINDER_GOAL_WRAPPED_CLASS_NAMES.put("v1_9_R2", "PathfinderGoalSelector.PathfinderGoalSelectorItem");
        PATHFINDER_GOAL_WRAPPED_CLASS_NAMES.put("v1_9_R1", "PathfinderGoalSelector.PathfinderGoalSelectorItem");

        try {
            ENTITY_INSENTIENT_CLASS = Class.forName("net.minecraft.server." + NmsUtils.VERSION + "." + "EntityInsentient");
            LIVING_ENTITY_GET_HANDLE_METHOD = Class.forName("org.bukkit.craftbukkit." + NmsUtils.VERSION + ".entity.CraftLivingEntity").getMethod("getHandle");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            PATHFINDER_GOAL_WRAPPED_UNWRAPPED_FIELD = Class.forName("net.minecraft.server." + NmsUtils.VERSION + "." + PATHFINDER_GOAL_WRAPPED_CLASS_NAMES.getOrDefault(NmsUtils.VERSION, defaultPathfinderGoalWrappedClassName)).getDeclaredField("a");
            PATHFINDER_GOAL_SELECTOR_GOALS_FIELD = Class.forName("net.minecraft.server." + NmsUtils.VERSION + ".PathfinderGoalSelector").getDeclaredField("d");
            ENTITY_INSENTIENT_GOAL_SELECTOR_FIELD = Class.forName("net.minecraft.server." + NmsUtils.VERSION + ".EntityInsentient").getField("goalSelector");
            ENTITY_INSENTIENT_TARGET_SELECTOR_FIELD  = Class.forName("net.minecraft.server." + NmsUtils.VERSION + ".EntityInsentient").getField("targetSelector");
            PATHFINDER_GOAL_WRAPPED_UNWRAPPED_FIELD.setAccessible(true);
            PATHFINDER_GOAL_SELECTOR_GOALS_FIELD.setAccessible(true);
            ENTITY_INSENTIENT_GOAL_SELECTOR_FIELD.setAccessible(true);
            ENTITY_INSENTIENT_TARGET_SELECTOR_FIELD.setAccessible(true);
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private static Object getPathfinderGoalFromWrapped(Object wrappedPathfinderGoal) {
        try {
            return PATHFINDER_GOAL_WRAPPED_UNWRAPPED_FIELD.get(wrappedPathfinderGoal);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPathfinderGoalName(Object wrappedPathfinderGoal) {
        Object pathfinderGoal = getPathfinderGoalFromWrapped(wrappedPathfinderGoal);
        if(pathfinderGoal == null) {
            return null;
        }

        String canonicalName = pathfinderGoal.getClass().getCanonicalName();
        String packageName = pathfinderGoal.getClass().getPackage().getName();

        return canonicalName.replaceFirst(Pattern.quote(packageName + "."), "");
    }

    private static void wrapGoalSet(Saml saml, Object goalSelector, LivingEntity entity) {
        try {
            Set goals = (Set) PATHFINDER_GOAL_SELECTOR_GOALS_FIELD.get(goalSelector);
            if(!(goals instanceof GoalSkippingSetWrapper)) {
                PATHFINDER_GOAL_SELECTOR_GOALS_FIELD.set(goalSelector, new GoalSkippingSetWrapper(saml, goals, entity));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void unwrapGoalSet(Object goalSelector) {
        try {
            Set goals = (Set) PATHFINDER_GOAL_SELECTOR_GOALS_FIELD.get(goalSelector);
            if(goals instanceof GoalSkippingSetWrapper) {
                PATHFINDER_GOAL_SELECTOR_GOALS_FIELD.set(goalSelector, ((GoalSkippingSetWrapper) goals).getOriginal());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static boolean hasWrappedGoalSet(Object goalSelector) {
        try {
            Set goals = (Set) PATHFINDER_GOAL_SELECTOR_GOALS_FIELD.get(goalSelector);
            if(goals instanceof GoalSkippingSetWrapper) {
                return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static boolean isNerfable(LivingEntity entity) {
        try {
            return ENTITY_INSENTIENT_CLASS.isInstance(LIVING_ENTITY_GET_HANDLE_METHOD.invoke(entity));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isNerfed(LivingEntity entity) {
        if(isNerfable(entity)) {
            try {
                Object handle = LIVING_ENTITY_GET_HANDLE_METHOD.invoke(entity);
                Object goalSelector = ENTITY_INSENTIENT_GOAL_SELECTOR_FIELD.get(handle);
                Object targetSelector = ENTITY_INSENTIENT_TARGET_SELECTOR_FIELD.get(handle);

                if(hasWrappedGoalSet(goalSelector) || hasWrappedGoalSet(targetSelector)) {
                    return true;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static void nerf(Saml saml, LivingEntity entity, NerfedEntityData nerfedEntityData) {
        if(isNerfable(entity)) {
            Object handle = null;
            try {
                handle = LIVING_ENTITY_GET_HANDLE_METHOD.invoke(entity);
                Object goalSelector = ENTITY_INSENTIENT_GOAL_SELECTOR_FIELD.get(handle);
                Object targetSelector = ENTITY_INSENTIENT_TARGET_SELECTOR_FIELD.get(handle);

                wrapGoalSet(saml, goalSelector, entity);
                wrapGoalSet(saml, targetSelector, entity);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            nerfedEntityData.setAsNerfedEntityData(saml, entity);
        }
    }

    public static void unnerf(Saml saml, LivingEntity entity) {
        if(isNerfable(entity)) {
            Object handle = null;
            try {
                handle = LIVING_ENTITY_GET_HANDLE_METHOD.invoke(entity);
                Object goalSelector = ENTITY_INSENTIENT_GOAL_SELECTOR_FIELD.get(handle);
                Object targetSelector = ENTITY_INSENTIENT_TARGET_SELECTOR_FIELD.get(handle);

                unwrapGoalSet(goalSelector);
                unwrapGoalSet(targetSelector);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            NerfedEntityData.stripOfNerfedEntityData(saml, entity);
        }
    }

}
