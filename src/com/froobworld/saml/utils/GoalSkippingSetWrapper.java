package com.froobworld.saml.utils;

import com.froobworld.saml.Saml;
import com.froobworld.saml.config.ConfigKeys;
import com.froobworld.saml.data.NerfedEntityData;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class GoalSkippingSetWrapper implements Set {
    private static final long TICKS_PER_UPDATE = 6000;
    private Saml saml;
    private Set original;
    private LivingEntity entity;
    private Set trimmedOriginal;
    private long cyclesSinceLastUpdate;

    public GoalSkippingSetWrapper(Saml saml, Set original, LivingEntity entity) {
        this.saml = saml;
        this.original = original;
        this.entity = entity;
        this.trimmedOriginal = new HashSet();
        updateTrimmedOriginal();
    }


    public Set getOriginal() {
        return original;
    }

    private void updateTrimmedOriginal() {
        trimmedOriginal.clear();
        List<String> identifiers = new ArrayList<>();
        identifiers.add("default");
        identifiers.add(entity.getType().name());
        Optional<NerfedEntityData> nerfedEntityData = NerfedEntityData.getNerfedEntityData(saml, entity);
        nerfedEntityData.ifPresent( d -> d.getGroups().forEach( g -> identifiers.add("group." + g) ) );

        boolean includeDefault = true;
        for(String key : saml.getNerferGoals().getSection(ConfigKeys.NRF_UNSPECIFIED).keySet()) {
            if(identifiers.contains(key)) {
                includeDefault = saml.getNerferGoals().getBoolean(ConfigKeys.NRF_UNSPECIFIED + "." + key);
                break;
            }
        }

        for(Object goal : original) {
            String goalName = EntityNerfer.getPathfinderGoalName(goal);
            boolean include = includeDefault;
            for(String key : saml.getNerferGoals().getSection(goalName).keySet()) {
                if(identifiers.contains(key)) {
                    include = saml.getNerferGoals().getBoolean(goalName + "." + key);
                    break;
                }
            }
            if(include) {
                trimmedOriginal.add(goal);
            }
        }
    }

    @Override
    public int size() {
        return original.size();
    }

    @Override
    public boolean isEmpty() {
        return original.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return original.contains(o);
    }

    @Override
    public Iterator iterator() {
        cyclesSinceLastUpdate++;
        if(cyclesSinceLastUpdate > TICKS_PER_UPDATE) {
            updateTrimmedOriginal();
            cyclesSinceLastUpdate = 0;
        }

        return trimmedOriginal.iterator();
    }

    @Override
    public Object[] toArray() {
        return original.toArray();
    }

    @Override
    public boolean add(Object o) {
        return original.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return original.remove(o);
    }

    @Override
    public boolean addAll(Collection c) {
        return original.addAll(c);
    }

    @Override
    public void clear() {
        original.clear();
    }

    @Override
    public boolean removeAll(Collection c) {
        return original.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection c) {
        return original.retainAll(c);
    }

    @Override
    public boolean containsAll(Collection c) {
        return original.containsAll(c);
    }

    @Override
    public Object[] toArray(Object[] a) {
        return original.toArray(a);
    }
}
